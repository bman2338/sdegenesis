package ch.usi.inf.genesis.data.bugtracker;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.RestClientException;
import com.atlassian.jira.rest.client.auth.AnonymousAuthenticationHandler;
import com.atlassian.jira.rest.client.domain.BasicComponent;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.BasicUser;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.domain.User;
import com.atlassian.jira.rest.client.domain.Version;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory;


public class JiraCrawler implements IBugTrackerCrawler{
	private final JerseyJiraRestClientFactory factory;
	private final URI jiraServerUri;
	private final JiraRestClient restClient;

	/**
	 * @author Luca Ponzanelli
	 * @param uri The uri to jira bug tracker
	 * 
	 * It initializes an anonymous Jira Crawler. Due to the anonymous access not all information can be retrieved.
	 * It can miss some bugs and/or detailed user information.
	 */
	public JiraCrawler(final String uri) throws URISyntaxException{
		this.factory = new JerseyJiraRestClientFactory();
		this.jiraServerUri = new URI(uri);
		this.restClient = factory.create(jiraServerUri,new AnonymousAuthenticationHandler());
	}

	/**
	 * @author Luca Ponzanelli
	 * @param uri The uri to jira bug tracker
	 * @param username The user's id to log in
	 * @param password The user's password to log in
	 * 
	 * It initializes an anonymous Jira Crawler. 
	 * The amount of information accessible is related to the permissions granted to the user.
	 */
	public JiraCrawler(final String uri, final String username, final String password) throws URISyntaxException{
		this.factory = new JerseyJiraRestClientFactory();
		this.jiraServerUri = new URI(uri);
		this.restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, username, password);
	}
	

	/**
	 * @author Luca Ponzanelli
	 * @param project The project's id in jira
	 * @param component The component's id in jira
	 * @return A JQL compliant query to filter out bug entries on project id and component id
	 */
	private String buildQueryString(final String project, final String component){
		final String projectPar = project == null || project.length() == 0? null : String.format("project = \"%s\"", project);
		final String componentPar = component == null || component.length() == 0? null : String.format("component = \"%s\"", component);
		
		if(projectPar != null && componentPar != null)
			return projectPar + "AND " + componentPar;
		
		if(componentPar != null)
			return componentPar;
		
		if(projectPar != null)
			return projectPar;
		
		return ""; //No Parameters in query
	}
	
	@Override
	public List<BugInfo> getBugList(final String project, final String component){	
		final NullProgressMonitor pm = new NullProgressMonitor();
		int windowEnd = 1000;
		final int step = 1000;
		int windowStart = 0;
		SearchResult result = null;
		final List<BugInfo> bugList = new ArrayList<BugInfo>();
		
		final String query = buildQueryString(project, component);

		do{
			try{
				result = restClient.getSearchClient().searchJql(query,windowEnd,windowStart,pm);
				for(final BasicIssue i : result.getIssues()){
					try{
						final Issue issue = restClient.getIssueClient().getIssue(i.getKey(), pm);
						//if(!issue.getIssueType().getName().toLowerCase().equals("bug")) //only bugs? enhancement?
						//	continue;

						User jiraAssignee = null;
						BugTrackerUser assignee = null;
						final BasicUser basicAssignee = issue.getAssignee();
						if(basicAssignee != null){
							try{
								jiraAssignee = restClient.getUserClient().getUser(basicAssignee.getName(), pm);
								assignee  = new BugTrackerUser(jiraAssignee.getName(), 
										jiraAssignee.getDisplayName(), jiraAssignee.getEmailAddress());
							}
							catch(final RestClientException ex){
								assignee  = new BugTrackerUser(basicAssignee.getName(), 
										basicAssignee.getDisplayName(), "");
							}
						}

						User jiraReporter = null;
						BugTrackerUser reporter = null;
						final BasicUser basicReporter = issue.getReporter();
						if(basicReporter != null){
							try{
								jiraReporter = restClient.getUserClient().getUser(basicReporter.getName(), pm);
								reporter  = new BugTrackerUser(jiraReporter.getName(), 
										jiraReporter.getDisplayName(), jiraReporter.getEmailAddress());
							}catch(final RestClientException ex){
								reporter  = new BugTrackerUser(basicReporter.getName(),basicReporter.getDisplayName(),"");
							}
						}


						//extract all versions
						final List<String> versions = new ArrayList<String>();
						for(final Version v : issue.getAffectedVersions())
							versions.add(v.getName());
						
						final String status = issue.getStatus().getName();
						//extract all components
						final List<String> components = new ArrayList<String>();
						for(final BasicComponent c : issue.getComponents())
							components.add(c.getName());
						

						bugList.add(new BugInfo(
								issue.getKey(),
								status,
								issue.getResolution() == null? "" : issue.getResolution().getName(),
								issue.getSummary(),
								issue.getProject().getKey(),
								components,
								"",
								"",
								versions,
								basicAssignee == null? new BugTrackerUser() : assignee,
								issue.getWatchers().getNumWatchers(),
								reporter == null? new BugTrackerUser(): reporter,
								new ArrayList<BugTrackerUser>(),
								issue.getSelf().toURL().toString(),
								issue.getCreationDate().toDate(),
								issue.getUpdateDate().toDate(),
								issue.getPriority().getName(),
								"",
								issue.getVotes().getVotes()
								));
					}
					catch (final RestClientException rex){
						System.out.println(i.getKey()+":  " + rex);
					}
					catch(final MalformedURLException e){
						System.out.println(e);
					}
				}
				windowStart = windowEnd+1;
				windowEnd += step;
			}catch (final RestClientException rex){
				System.out.println(rex);
			}
		}
		while(result != null && result.getIssues().iterator().hasNext());

		return bugList;
	}

	@Override
	public List<BugInfo> getBugList() {
		return getBugList("","");
	}
	
	@Override
	public URL getURL(){
		try {
			return this.jiraServerUri.toURL();
		} catch (final MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
	

}

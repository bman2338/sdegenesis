package ch.usi.inf.genesis.data.bugtracker;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.NullProgressMonitor;
import com.atlassian.jira.rest.client.RestClientException;
import com.atlassian.jira.rest.client.auth.AnonymousAuthenticationHandler;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.BasicUser;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.domain.User;
import com.atlassian.jira.rest.client.internal.jersey.JerseyJiraRestClientFactory;


public class JiraCrawler implements BugTrackerCrawler{
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
	 * @param username The user id to log in
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

	@Override
	public List<BugInfo> getBugList(){	
		final NullProgressMonitor pm = new NullProgressMonitor();
		int windowEnd = 1000;
		final int step = 1000;
		int windowStart = 0;
		SearchResult result = null;
		final List<BugInfo> bugList = new ArrayList<BugInfo>();

		do{
			try{
				result = restClient.getSearchClient().searchJql("",windowEnd,windowStart,pm);
				for(final BasicIssue i : result.getIssues()){
					try{
						final Issue issue = restClient.getIssueClient().getIssue(i.getKey(), pm);
						if(!issue.getIssueType().getName().toLowerCase().equals("bug")) //only bugs? enhancement?
							continue;

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


						final String status = issue.getStatus().getName();
						
						System.out.println("Reporter Name: " + reporter.getDisplayName());
						bugList.add(new BugInfo(
								issue.getKey(),
								status,
								issue.getResolution() == null? "" : issue.getResolution().getName(),
								issue.getSummary(),
								issue.getProject().toString(),
								"", //TODO FIX COMPONENTS BETWEEN BUGZILLA AND JIRA
								"",
								"",
								"",
								basicAssignee == null? new BugTrackerUser() : assignee,
								issue.getWatchers().getNumWatchers(),
								reporter == null? new BugTrackerUser(): reporter,
								new ArrayList<BugTrackerUser>(),
								issue.getSelf().toURL().toString(),
								issue.getCreationDate().toDate(),
								issue.getUpdateDate().toDate(),
								"",
								"",
								issue.getVotes().getVotes()
								));
					}
					catch (final RestClientException rex){
						System.out.println(i.getKey()+":  " + rex.getErrorMessages());
					}
					catch(final MalformedURLException e){}
				}
				windowStart = windowEnd+1;
				windowEnd += step;
			}catch (final RestClientException rex){
				System.out.println(rex.getErrorMessages());
			}
		}
		while(result != null && result.getIssues().iterator().hasNext());

		return bugList;
	}

}

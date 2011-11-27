package ch.usi.inf.genesis.data.bugtracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class BugzillaCrawler implements IBugTrackerCrawler{

	private final URL bugzillaURL;
	
	/**
	 * @author Luca Ponzanelli
	 * @param url the url to bugzilla bugtracker
	 * @throws MalformedURLException
	 * 
	 * Example of url: https://issues.apache.org/bugzilla/
	 */
	public BugzillaCrawler(final String url) throws MalformedURLException{
		bugzillaURL = new URL(url);
	}

	

	/**
	 * @author Luca Ponzanelli
	 * @param product The id of the project's bugs to crawl
	 * @param component The id of the component's bugs to crawl
	 * @return The list of the bugs to be retrieved
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private List<Integer> retrieveBugList(final String product, final String component) throws MalformedURLException, IOException{
		//Format Parameters String
		final String formattedProduct = product.replaceAll(" ", "%20");
		final String formattedComponent = component.replaceAll(" ", "%20");
		final String csvBugListParams = String.format("buglist.cgi?ctype=csv&query_format=advanced&order=bug_id&product=%s&component=%s", formattedProduct,formattedComponent);
		final String url = bugzillaURL.toString();
		final List<Integer> bugIdList = new ArrayList<Integer>();

		//Retrieve bug list in CSV
		final URLConnection con = new URL(concatenateURLs(url,csvBugListParams)).openConnection();
		final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String bugEntry;
		while ((bugEntry = in.readLine()) != null){ 
			final int index = bugEntry.indexOf(",");
			final String str = bugEntry.substring(0, index);
			if(!str.matches("[\\d]*"))
				continue;
			bugIdList.add(Integer.parseInt(str));
		}
		in.close();
		return bugIdList;
	}


	private String concatenateURLs(final String first, final String second){
		if(first.lastIndexOf("/") != first.length()-1)
			return first+"/"+second;

		return first+second;
	}


	@Override
	public List<BugInfo> getBugList() {
		return getBugList("","");
	}

	@Override
	public List<BugInfo> getBugList(final String project, final String component) {
		final List<BugInfo> bugList = new ArrayList<BugInfo>();
		final int uriLength = 7168;
		String param;
		try{
			final List<Integer> bugIdList = retrieveBugList(project, component);
			for(int current = 0; current < bugIdList.size(); ++current){
				String xmlParams = "show_bug.cgi?ctype=xml";
				do{
					param = "&id="+bugIdList.get(current);
					xmlParams += param;
					++current;
				}while(((xmlParams + param).length() <= uriLength) && (current < bugIdList.size()));

				final URLConnection con = new URL(concatenateURLs(bugzillaURL.toString(), xmlParams)).openConnection();
				final List<BugInfo> bugs = BugzillaParser.parse(new InputStreamReader(con.getInputStream()));

				//Retrieve Bugs' History
				for(final BugInfo bug : bugs)
					bug.setHistory(BugzillaParser.parseHistory(new URL(bugzillaURL.toString()+"show_activity.cgi?id="+bug.getId())));

				bugList.addAll(bugs);
			}
		}
		catch(final IOException e) {
			System.out.println(e.getMessage());
		}

		return bugList;
	}

	@Override
	public URL getURL(){
		return this.bugzillaURL;
	}
}

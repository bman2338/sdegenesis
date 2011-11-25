package ch.usi.inf.genesis.data.bugtracker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class BugzillaCrawler implements BugTrackerCrawler{

	private final URL bugzillaURL;
	private final String firstBug = "buglist.cgi?ctype=csv&query_format=advanced&order=bug_id&limit=1";
	private final String lastBug = "buglist.cgi?ctype=csv&query_format=advanced&order=bug_id%20DESC&limit=1";
	public BugzillaCrawler(final String url) throws MalformedURLException{
		bugzillaURL = new URL(url);
	}

	/**
	 * 
	 * @param bugParams
	 * @return The id of the first bug in the retrieved CSV list.
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private int retrieveBugId(final String bugParams) throws MalformedURLException, IOException{
		//retrieve BugList in CSV.
		final String url = bugzillaURL.toString();
		//Retrieve bug id
		final URLConnection con = new URL(concatenateURLs(url, bugParams)).openConnection();
		final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String bugEntry;
		int firstID=-1;
		while ((bugEntry = in.readLine()) != null){ 
			final int index = bugEntry.indexOf(",");
			final String str = bugEntry.substring(0, index);
			if(!str.matches("[\\d]*"))
				continue;
			firstID = Integer.parseInt(str);
		}
		in.close();
		return firstID;
	}


	private String concatenateURLs(final String first, final String second){
		if(first.lastIndexOf("/") != first.length()-1)
			return first+"/"+second;

		return first+second;
	}


	@Override
	public List<BugInfo> getBugList() {
		final List<BugInfo> bugList = new ArrayList<BugInfo>();
		final int uriLength = 7168;
		int current = 0;
		String param;
		try{
			final int firstID = retrieveBugId(firstBug);
			final int lastID = retrieveBugId(lastBug);

			System.out.println("First: " + firstID);
			System.out.println("Last: " + lastID);

			current = firstID;
			do{
				String xmlParams = "show_bug.cgi?ctype=xml";
				do{
					param = "&id="+current;
					xmlParams += param;
					++current;
				}while(((xmlParams + param).length() <= uriLength) && (current <= lastID));

				final URLConnection con = new URL(concatenateURLs(bugzillaURL.toString(), xmlParams)).openConnection();
				final List<BugInfo> bugs = BugzillaParser.parse(new InputStreamReader(con.getInputStream()));

				//Retrieve Bugs' History
				for(final BugInfo bug : bugs){
					bug.setHistory(BugzillaParser.parseHistory(new URL(bugzillaURL.toString()+"show_activity.cgi?id="+bug.getId())));
				}
				bugList.addAll(bugs);

				System.out.println(bugs);
				System.out.println("Downloaded: " + (current-firstID));
			}
			while(current <= lastID);
		}
		catch(final IOException e) {
			System.out.println(e.getMessage());
		}

		return bugList;
	}

}

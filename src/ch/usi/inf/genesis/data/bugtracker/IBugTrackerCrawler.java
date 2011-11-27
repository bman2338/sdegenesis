package ch.usi.inf.genesis.data.bugtracker;

import java.util.List;
import java.net.URL;

public interface IBugTrackerCrawler {

	public List<BugInfo>getBugList();
	public List<BugInfo>getBugList(final String project, final String component);
	public URL getURL();
}

package ch.usi.inf.genesis.data.bugtracker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class BugInfo {

	private String id;
	private String status;
	private String resolution;
	private String summary;
	private String project;
	private List<String> components;
	private String operatingSys;
	private String platform;
	private String version;
	private BugTrackerUser assignee;
	private List<BugTrackerUser> ccUsers;
	private long watches;
	private BugTrackerUser reporter;
	private String uri;
	private Date creationDate;
	private Date updateDate;
	private String priority;
	private String severity;
	private long votes;
	private List<BugHistoryEntry> history;


	public BugInfo(){
		this.ccUsers = new ArrayList<BugTrackerUser>();
		this.history = new ArrayList<BugHistoryEntry>();
		this.components = new ArrayList<String>();
	}

	public BugInfo(final String id,final String status,final String resolution, final String summary, final String product, 
			final List<String> component, final String opertatingSys, final String platform, final String version, final BugTrackerUser assignee,
			final long watches, final BugTrackerUser reporter, final List<BugTrackerUser> ccUsers, final String uri,
			final Date creationDate, final Date updateDate, final String priority, final String severity,
			final long votes) {
		this.id = id;
		this.status = status;
		this.resolution = resolution;
		this.summary = summary;
		this.assignee = assignee;
		this.watches = watches;
		this.reporter = reporter;
		this.uri = uri;
		this.creationDate = creationDate;
		this.updateDate = updateDate;
		this.votes = votes;
		this.severity = severity;
		this.priority = priority;
		this.ccUsers = ccUsers;
		this.history = new ArrayList<BugHistoryEntry>();
		this.project = product;
		this.components = component;
		this.version = version;
		this.operatingSys = opertatingSys;
		this.platform = platform;
	}


	public String getId() {
		return id;
	}
	public void setId(final String id) {
		this.id = id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(final String status) {
		this.status = status;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(final String summary) {
		this.summary = summary;
	}
	public BugTrackerUser getAssignee() {
		return assignee;
	}
	public void setAssignee(final BugTrackerUser assignee) {
		this.assignee = assignee;
	}
	public long getWatchers() {
		return watches;
	}
	public void setWatchers(final long watches) {
		this.watches = watches;
	}
	public BugTrackerUser getReporter() {
		return reporter;
	}
	public void setReporter(final BugTrackerUser reporter) {
		this.reporter = reporter;
	}
	public String getUri() {
		return uri;
	}
	public void setUrl(final String uri) {
		this.uri = uri;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(final Date creationDate) {
		this.creationDate = creationDate;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(final Date updateDate) {
		this.updateDate = updateDate;
	}
	public long getVotes() {
		return votes;
	}
	public void setVotes(final long votes) {
		this.votes = votes;
	}
	public String getResolution() {
		return resolution;
	}
	public void setResolution(final String resolution) {
		this.resolution = resolution;
	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(final String priority) {
		this.priority = priority;
	}
	public String getSeverity() {
		return severity;
	}
	public void setSeverity(final String severity) {
		this.severity = severity;
	}

	public List<BugTrackerUser> getCcUsers() {
		return ccUsers;
	}

	public void setCcUsers(final List<BugTrackerUser> ccUsers) {
		this.ccUsers = ccUsers;
	}

	public void addCcUser(final BugTrackerUser ccUser){
		this.ccUsers.add(ccUser);
	}

	public String toString(){
		return String.format("ID: %s\nSUMMARY: %s\nSTATUS: %s\nRESOLUTON: %s\n", id, summary, status, resolution) + 
				String.format("PROJECT: %s\nCOMPONENTS: %s\n", project, components) + 
				String.format("PRIORITY: %s\nSEVERITY: %s\nCREATION_DATE:%s\nUPDATE_DATE:%s\n", priority,severity,creationDate, updateDate)+
				String.format("REPORTER: %s\nASSIGNEE: %s\nCC: %s\nWATCHES: %s\nVOTES: %s\n",reporter, assignee, ccUsers, watches, votes)+
				String.format("HISTORY: %s\n", history);
	}

	public List<BugHistoryEntry> getHistory() {
		return history;
	}

	public void setHistory(final List<BugHistoryEntry> history) {
		this.history = history;
	}

	public void addHistory(final BugHistoryEntry entry){
		this.history.add(entry);
	}

	public String getProduct() {
		return project;
	}

	public void setProduct(final String product) {
		this.project = product;
	}

	public List<String> getComponent() {
		return components;
	}

	public void setComponent(final List<String> component) {
		this.components = component;
	}

	public String getOperatingSys() {
		return operatingSys;
	}

	public void setOperatingSys(final String operatingSys) {
		this.operatingSys = operatingSys;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(final String platform) {
		this.platform = platform;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(final String version) {
		this.version = version;
	}

	public long getWatches() {
		return watches;
	}

	public void setWatches(final long watches) {
		this.watches = watches;
	}

	public void setUri(final String uri) {
		this.uri = uri;
	}

	public void addComponent(String componentName) {
		this.components.add(componentName);
	}
}

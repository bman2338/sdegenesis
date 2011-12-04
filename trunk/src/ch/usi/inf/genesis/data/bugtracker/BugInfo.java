package ch.usi.inf.genesis.data.bugtracker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//TODO Refactor BugInfo and all ch.usi.inf.genesis.data.bugtracker. Rewrite it to Scala and use model core entities.
public class BugInfo {

	private String id;
	private String status;
	private String resolution;
	private String summary;
	private String project;
	private List<String> components;
	private String operatingSys;
	private String platform;
	private List<String> versions;
	private BugTrackerUser assignee;
	private List<BugTrackerUser> ccUsers;
	private int watches;
	private BugTrackerUser reporter;
	private String uri;
	private Date creationDate;
	private Date updateDate;
	private String priority;
	private String severity;
	private int votes;
    private BugHistory history;



	public BugInfo(){
		this.ccUsers = new ArrayList<BugTrackerUser>();
        this.history = new BugHistory();
		this.components = new ArrayList<String>();
		this.versions = new ArrayList<String>();
	}

	public BugInfo(final String id,final String status,final String resolution, final String summary, final String product, 
			final List<String> component, final String opertatingSys, final String platform, final List<String> versions, final BugTrackerUser assignee,
			final int watches, final BugTrackerUser reporter, final List<BugTrackerUser> ccUsers, final String uri,
			final Date creationDate, final Date updateDate, final String priority, final String severity,
			final int votes) {
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
		//this.history = new ArrayList<BugHistoryTransition>();
        this.history = new BugHistory();
		this.project = product;
		this.components = component;
		this.versions = versions;
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
	public int getVotes() {
		return votes;
	}
	public void setVotes(final int votes) {
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

	public BugHistory getHistory() {
		return history;
	}

	public void setHistory(final BugHistory history) {
		this.history = history;
	}

	public void addHistory(final String key, final BugHistoryTransition transition){
		this.history.addTransition(key, transition);
	}

	public String getProject() {
		return project;
	}

	public void setProject(final String project) {
		this.project = project;
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

	public List<String> getVersions() {
		return versions;
	}

	public void setVersions(final List<String> versions) {
		this.versions = versions;
	}
	
	public void addVersion(final String version){
		this.versions.add(version);
	}

	public int getWatches() {
		return watches;
	}

	public void setWatches(final int watches) {
		this.watches = watches;
	}

	public void setUri(final String uri) {
		this.uri = uri;
	}

	public void addComponent(String componentName) {
		this.components.add(componentName);
	}
}

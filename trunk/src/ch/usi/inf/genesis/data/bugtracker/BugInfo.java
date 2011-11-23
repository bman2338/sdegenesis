package ch.usi.inf.genesis.data.bugtracker;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class BugInfo {

	private String id;
	private String status;
	private String resolution;
	private String summary;
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

	public BugInfo(){
		this.ccUsers = new ArrayList<BugTrackerUser>();
	}

	public BugInfo(final String id,final String status,final String resolution, final String summary,final BugTrackerUser assignee,
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
	public void setUrl(String uri) {
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
	
	public String toString(){ //COMPLETE TO STRING FOR DEBUG
		return String.format("ID: %s\nSUMMARY: %s\nSTATUS: %s\nRESOLUTON: %s", id, summary, status, resolution) + 
				String.format("PRIORITY: %s\nSEVERITY: %s\n CREATION_DATE:%s\nUPDATE_DATE:%s\n", priority,severity,creationDate, updateDate)+
				String.format("REPORTER: %s\nASSIGNEE: %s\nCC: %s\nWATCHES: %s\nVOTES: %s",reporter, assignee, ccUsers, watches, votes);
	}
}

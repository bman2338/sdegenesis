package ch.usi.inf.genesis.data.bugtracker;

import java.util.Date;

public class BugHistoryEntry {

	private String who;
	private Date when;
	private String what;
	private String removed;
	private String added;

	public BugHistoryEntry(){}

	public BugHistoryEntry(final String who, final Date when, final String what,
			final String removed, final String added) {
		this.who = who;
		this.when = when;
		this.what = what;
		this.removed = removed;
		this.added = added;
	}


	public String getWho() {
		return who;
	}

	public void setWho(final String who) {
		this.who = who;
	}

	public Date getWhen() {
		return when;
	}

	public void setWhen(final Date when) {
		this.when = when;
	}

	public String getWhat() {
		return what;
	}

	public void setWhat(final String what) {
		this.what = what;
	}

	public String getRemoved() {
		return removed;
	}

	public void setRemoved(final String removed) {
		this.removed = removed;
	}

	public String getAdded() {
		return added;
	}

	public void setAdded(final String added) {
		this.added = added;
	}

	public String toString(){
		return String.format("WHO: %s\nWHEN: %s\nWHAT: %s\nADDED: %s\nREMOVED: %s\n",who,when,what,added,removed);
	}
}

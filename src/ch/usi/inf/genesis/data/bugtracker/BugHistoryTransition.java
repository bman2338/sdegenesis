package ch.usi.inf.genesis.data.bugtracker;

import java.util.Date;

public class BugHistoryTransition {

	private String who;
	private Date when;
	private String removed;
	private String added;

	public BugHistoryTransition(){}

	public BugHistoryTransition(final String who, final Date when,
                                final String removed, final String added) {
		this.who = who;
		this.when = when;
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
		return String.format("WHO: %s\nWHEN: %s\nADDED: %s\nREMOVED: %s\n",who,when,added,removed);
	}
}

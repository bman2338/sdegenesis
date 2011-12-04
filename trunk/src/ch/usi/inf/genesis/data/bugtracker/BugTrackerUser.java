package ch.usi.inf.genesis.data.bugtracker;

public class BugTrackerUser{
	
	private String name;
	private String displayName;
	private String email;
	
	
	public BugTrackerUser(){}
	
	public BugTrackerUser(final String name, final String displayName, 
			final String email){
		this.name = name;
		this.displayName = displayName;
		this.email = email;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getDisplayName() {
		return displayName;
	}


	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}
	
	@Override
	public String toString(){
		return String.format("NAME: %s\nDISPLAY_NAME: %s\nE-MAIL: %s\n", name,displayName,email);
	}
}
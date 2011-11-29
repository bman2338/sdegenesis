package ch.usi.inf.genesis.data.repository;

public final class RepositoryUserAuth {
	private final String username;
	private final String password;
	
	public RepositoryUserAuth(final String username, final String password){
		this.username = username;
		this.password = password;
	}
	
	public RepositoryUserAuth(final String username){
		this(username, "");
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}

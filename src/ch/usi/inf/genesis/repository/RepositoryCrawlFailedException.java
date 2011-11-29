package ch.usi.inf.genesis.data.repository;

public class RepositoryCrawlFailedException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6368677932523157787L;
	final String errorMessage;
	
	public RepositoryCrawlFailedException(){
		this.errorMessage = "Error in crawling Repository.";
	}
	
	public RepositoryCrawlFailedException(final String errorMessage){
		this.errorMessage = errorMessage;
	}
	
	@Override
	public String getMessage(){
		return errorMessage;
	}

}

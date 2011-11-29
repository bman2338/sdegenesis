package ch.usi.inf.genesis.data.repository;
public interface IRepositoryCrawler {
	public void crawl(final long firstRev, final long lastRev) throws RepositoryCrawlFailedException;
	public void crawl(final long firstRev) throws RepositoryCrawlFailedException;
	public void crawl() throws RepositoryCrawlFailedException;
}

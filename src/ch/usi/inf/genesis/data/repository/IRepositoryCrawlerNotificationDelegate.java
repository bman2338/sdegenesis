package ch.usi.inf.genesis.data.repository;
public interface IRepositoryCrawlerNotificationDelegate {
	public void onParsingCompleted(final RevisionInformation revisionInfo);
}

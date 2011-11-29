package ch.usi.inf.genesis.data.repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.ISVNDiffStatusHandler;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNDiffStatus;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatusType;
import org.tmatesoft.svn.core.wc.SVNWCUtil;


public class SVNCrawler implements IRepositoryCrawler{


	private final SVNURL url;
	private final ISVNAuthenticationManager authManager;
	private final ISVNOptions options;
	private final SVNClientManager manager;
	private final long lastRevisionNumber;
	private final List<String> allowedExtensions;
	private final String projectName;
	private final File localPath;
	private final SVNRepository repository;
	private final FamixLanguage projectLang;
	private final List<IRepositoryCrawlerNotificationDelegate> delegates;

	public SVNCrawler(final String url, final String projectName, final FamixLanguage projectLang, final String... extensions) throws SVNException{
		this.url = SVNURL.parseURIEncoded(url);
		this.options = SVNWCUtil.createDefaultOptions(true);
		this.authManager = SVNWCUtil.createDefaultAuthenticationManager( );
		this.manager = SVNClientManager.newInstance(options, authManager); 
		this.projectName = projectName;
		this.localPath = new File(projectName);
		
		//Repo Init
		DAVRepositoryFactory.setup();
		SVNRepositoryFactoryImpl.setup();
		FSRepositoryFactory.setup();
		
		this.repository = SVNRepositoryFactory.create(this.url);
		this.repository.setAuthenticationManager(authManager);
		this.lastRevisionNumber = this.repository.getLatestRevision();
		this.allowedExtensions = new ArrayList<String>();
		for(final String s : extensions)
			this.allowedExtensions.add(s);
		this.projectLang = projectLang;
		this.delegates = new ArrayList<IRepositoryCrawlerNotificationDelegate>();
	}

	public SVNCrawler(final String url, final String projectName, final FamixLanguage projectLang, final RepositoryUserAuth auth, final String... extensions) throws SVNException{
		this.url = SVNURL.parseURIEncoded(url);
		this.options = SVNWCUtil.createDefaultOptions(true);
		this.authManager = SVNWCUtil.createDefaultAuthenticationManager(auth.getUsername(), auth.getPassword());
		this.manager = SVNClientManager.newInstance(options, authManager); 
		this.projectName = projectName;
		this.localPath = new File(projectName);
		
		//Repo Init
		DAVRepositoryFactory.setup();
		SVNRepositoryFactoryImpl.setup();
		FSRepositoryFactory.setup();
		
		this.repository = SVNRepositoryFactory.create(this.url);
		this.repository.setAuthenticationManager(authManager);
		lastRevisionNumber = this.repository.getLatestRevision();
		this.allowedExtensions = new ArrayList<String>();
		for(final String s : extensions)
			this.allowedExtensions.add(s);
		this.projectLang = projectLang;
		this.delegates = new ArrayList<IRepositoryCrawlerNotificationDelegate>();
	}

	@Override
	public void crawl() throws RepositoryCrawlFailedException{
		crawl(1, this.lastRevisionNumber);
	}

	@Override
	public void crawl(final long firstRev) throws RepositoryCrawlFailedException{
		crawl(firstRev, this.lastRevisionNumber);
	}

	@Override
	public void crawl(final long firstRev, final long lastRev) throws RepositoryCrawlFailedException {

		final InFamixWrapper infamix = new InFamixWrapper("inFamix/MacOS/inFamix",projectLang);
		try{
			manager.getUpdateClient().doCheckout(url, localPath, SVNRevision.create(firstRev) , SVNRevision.create(firstRev), SVNDepth.INFINITY, false);
			for(long i = firstRev; i < lastRev; ++i){
				final long rev = i;
				final List<File> modifiedFiles = new ArrayList<File>();
				final List<File> deletedFiles = new ArrayList<File>();
				final List<File> addedFiles = new ArrayList<File>();
				
				manager.getUpdateClient().doUpdate(localPath, SVNRevision.create(i+1), SVNDepth.INFINITY, false, false);
				manager.getDiffClient().doDiffStatus(localPath, SVNRevision.create(i), localPath, SVNRevision.create(i+1), SVNDepth.INFINITY, false, new ISVNDiffStatusHandler() {

					@Override
					public void handleDiffStatus(final SVNDiffStatus diffStatus) throws SVNException {
						final File diffFile = diffStatus.getFile();
						if(diffFile == null || diffFile.isDirectory())
							return;

						final int dotIndex = diffFile.getName().lastIndexOf('.');
						if(dotIndex == -1) //No Extension
							return;

						final String extension = diffFile.getName().substring(dotIndex, diffFile.getName().length());
						if(!allowedExtensions.contains(extension))
							return;
						
						if(diffStatus.getModificationType() == SVNStatusType.STATUS_MODIFIED ||
								diffStatus.getModificationType() == SVNStatusType.STATUS_REPLACED) //TODO: Consider Merged???
							modifiedFiles.add(diffFile);
						else if(diffStatus.getModificationType() == SVNStatusType.STATUS_ADDED)
							addedFiles.add(diffFile); 
						else if(diffStatus.getModificationType() == SVNStatusType.STATUS_DELETED)
							deletedFiles.add(diffFile); 
					}
				});
				if(!modifiedFiles.isEmpty() || !addedFiles.isEmpty()){
					//Put all files together to get change log. (no deleted files)
					final List<File> allUpdatedFiles = new ArrayList<File>();
					allUpdatedFiles.addAll(modifiedFiles);
					allUpdatedFiles.addAll(addedFiles);

					final File[] files = new File[allUpdatedFiles.size()];
					allUpdatedFiles.toArray(files);
					
					//Retrieve Change log for modified files and added files
					manager.getLogClient().doLog(files, SVNRevision.create(i+1), SVNRevision.create(i+1), SVNRevision.create(i+1), false, true, (long) -1, new ISVNLogEntryHandler() {

						@Override
						public void handleLogEntry(final SVNLogEntry logEntry)
								throws SVNException {
							
							try {
								final File mseFile = infamix.execute(localPath.getAbsolutePath(), "mse/" + projectName + "_rev_" + (rev+1) +".mse", false);
								notifyOnParsingComplete(new RevisionInformation(
										logEntry.getAuthor(),logEntry.getDate(), logEntry.getMessage(), logEntry.getRevision(),
										addedFiles, deletedFiles, modifiedFiles, mseFile));
							} catch (final IOException e) {
								e.printStackTrace();
							}
						}
					});
					
					
				}
				System.out.println("###########  REV " + (i+1) +"  ###########\n\n");
			}
		}catch(final SVNException e){
			throw new RepositoryCrawlFailedException(e.toString());
		}
	}
	
	public void notifyOnParsingComplete(final RevisionInformation revisionsInfo){
		for(final IRepositoryCrawlerNotificationDelegate d : delegates)
			d.onParsingCompleted(revisionsInfo);
	}
	
	public void addOnParsingCompleteDelegate(final IRepositoryCrawlerNotificationDelegate delegate){
		this.delegates.add(delegate);
	}
	
}

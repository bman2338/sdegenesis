package ch.usi.inf.genesis.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNProperty;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;


public class SVNCrawler {

	private long latestRevision;
	private String svnUrl, projectName;
	private SVNRepository repository;
	private ArrayList<SVNDirEntry> directories;
	private ArrayList<ArrayList<SVNDirEntry>> files;

	public SVNCrawler(final String url, final String projName){
		svnUrl = url;
		setupLibrary();
		this.projectName = projName;
		String name = "guest";
		String password = "";

		try {
			repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
			ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager( name , password );
            repository.setAuthenticationManager( authManager );
			latestRevision = repository.getLatestRevision();

		} catch (SVNException e) {
			System.err.println("error creating SVNRepository for: '" + url + "': " + e.getMessage());
			System.exit(1);
			e.printStackTrace();
		}
	}

	public long getLatestRevisionNum(){
		return latestRevision;
	}

	public String getSvnUrl(){
		return svnUrl;
	}


	public void downloadRevision(final long revNum){
		try {
			SVNNodeKind nodeKind = repository.checkPath("",  revNum);

			if ( nodeKind == SVNNodeKind.NONE ) {
				System.err.println( "No entry at: '" + svnUrl + "'." );
				System.exit( 1 );
			} 
			File dir = new File(projectName +"_rev_"+revNum);
			dir.mkdirs();
			downloadRevision(revNum, "");

		} catch (SVNException e) {
			e.printStackTrace();
		}

	}

	/*
	 * Initializes the library to work with a repository via 
	 * different protocols.
	 */
	private static void setupLibrary() {
		// For using over http:// and https://
		DAVRepositoryFactory.setup();
		// For using over svn:// and svn+xxx://
		SVNRepositoryFactoryImpl.setup();
		// For using over file:///
		FSRepositoryFactory.setup();
	}


	/*
	 * download a revision of the repository
	 */
	private void downloadRevision(final long rev, final String path) throws SVNException{
		Collection entries = repository.getDir(path, rev, null, (Collection) null);
		ArrayList<SVNDirEntry> dirEntryList = new ArrayList<SVNDirEntry>();
		dirEntryList.addAll(entries);
		
		directories = new ArrayList<SVNDirEntry>();

		while(!dirEntryList.isEmpty()){

			final SVNDirEntry entry = dirEntryList.get(0);
			dirEntryList.remove(0);
			System.out.println("entry: "+ entry);
			//tags / branches check outside...
			// check for images, txt, doc, jar? class
			if(entry.getKind() == SVNNodeKind.DIR && entry.getURL().toString().contains("trunk")){
				String dirPath = entry.getURL().toString().replace(entry.getRepositoryRoot().toString(), "");

				System.out.println("adding dir: " + entry.getName() + " : "+dirPath);
				
				dirEntryList.addAll(repository.getDir(dirPath, rev, null, (Collection)null));
				File dir = new File(projectName +"_rev_"+rev+"/"+dirPath);
				dir.mkdir();
				//TODO: optimize

			}else if(entry.getKind()==SVNNodeKind.FILE){
				String filePath = entry.getURL().toString().replace(entry.getRepositoryRoot().toString(), "");
				filePath = filePath.replaceAll("\\%20", " ");
				System.out.println("file path: " + filePath);
				//TODO: optimize fino al writing

				SVNProperties fileProperties = new SVNProperties();

				String mimeType = ( String ) fileProperties.getStringValue(SVNProperty.MIME_TYPE);
				boolean isTextType = SVNProperty.isTextMimeType(mimeType);

				if(isTextType){
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					repository.getFile(filePath , rev , fileProperties , baos);

					OutputStream fileOutput;
					try {
						System.out.println("writing: " +path+"/"+entry.getName());
						fileOutput = new FileOutputStream(projectName +"_rev_"+rev+ "/" + filePath);
						baos.writeTo(fileOutput); 
						fileOutput.close();
					} catch (FileNotFoundException e) {

						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}


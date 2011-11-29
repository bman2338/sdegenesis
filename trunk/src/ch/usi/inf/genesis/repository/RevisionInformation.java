package ch.usi.inf.genesis.data.repository;
import java.io.File;
import java.util.Date;
import java.util.List;


public class RevisionInformation {

	private final String author;
	private final Date date;
	private final String comment;
	private final long revisionNumber;
	private final List<File> addedFiles;
	private final List<File> deletedFiles;
	private final List<File> modifiedFiles;
	private final File mseFile;
	
	
	public RevisionInformation(final String author, final Date date, final String comment, 
			final long revisionNumber, final List<File> addedFiles, final List<File> deletedFiles, 
			final List<File> modifiedFiles, final File mseFile){
		this.author = author;
		this.comment = comment;
		this.date = date;
		this.revisionNumber = revisionNumber;
		this.addedFiles = addedFiles;
		this.deletedFiles = deletedFiles;
		this.modifiedFiles = modifiedFiles;
		this.mseFile = mseFile;
	}
	
	public List<File> getAddedFiles() {
		return addedFiles;
	}


	public List<File> getDeletedFiles() {
		return deletedFiles;
	}


	public List<File> getModifiedFiles() {
		return modifiedFiles;
	}

	public String getAuthor() {
		return author;
	}


	public Date getDate() {
		return date;
	}


	public String getComment() {
		return comment;
	}


	public long getRevisionNumber() {
		return revisionNumber;
	}
	
	
	public File getMseFile() {
		return mseFile;
	}
	
	@Override
	public boolean equals(final Object o){
		if(o.getClass() != RevisionInformation.class)
			return false;
		
		return revisionNumber == ((RevisionInformation)o).revisionNumber;
	}
	
	@Override
	public String toString(){
		return String.format("Revision: %d\nAuthor: %s\nDate: %s\nComment: %s\nAdded Files: %s\n"+
				"Deleted Files: %s\nModified Files: %s\nMSE File: %s\n", 
				revisionNumber,author,date,comment,
				addedFiles,deletedFiles, modifiedFiles, mseFile);
	}
}

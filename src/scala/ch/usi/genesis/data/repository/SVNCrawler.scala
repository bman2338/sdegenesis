package ch.usi.genesis.data.repository

import org.tmatesoft.svn.core.io.SVNRepositoryFactory
import org.tmatesoft.svn.core.wc._
import collection.mutable.ListBuffer
import org.tmatesoft.svn.core.{ISVNLogEntryHandler, SVNLogEntry, SVNDepth, SVNURL}
import ch.usi.inf.genesis.model.core.{IntValue, StringValue}
import java.io._
import ch.usi.inf.genesis.model.core.famix.{DeveloperEntity, RevisionEntity}

/**
 * @author Luca Ponzanelli
 * @param url The url of the repository
 * @param projectName The name of the project
 * @param projectPath Path where checkout the repository
 * @param mseOutputPath The path to the directory where mse will be saved
 * @param parser The external parser to be used to generate MSE files
 * @param allowedExtensions The extension to be taken into consideration while crawling the repository
 *
 * It crawls an SVN repository an generates MSE meta-model files related to every revision taken into account.
 * It notifies that an mse file has been created at every delegate added to onSourceParsingCompleteDelegates.
 */

class SVNCrawler(url: String, projectName: String, projectPath: String,
                  mseOutputPath : String, parser : ExternalParserWrapper,
                  auth : RepositoryUserAuth, allowedExtensions: String*) extends RepositoryCrawler {
  val svnUrl = SVNURL.parseURIEncoded(url)
  val authManager = if (auth == null) SVNWCUtil.createDefaultAuthenticationManager
                    else SVNWCUtil.createDefaultAuthenticationManager(auth.getUserName, auth.getPassword)
  val options = SVNWCUtil.createDefaultOptions(true)
  val manager= SVNClientManager.newInstance(options, authManager)
  val repository = SVNRepositoryFactory.create(svnUrl)
  repository.setAuthenticationManager(authManager)
  val lastRevisionNumber = repository.getLatestRevision
  val localPath = new File(projectPath)
  val onSourceParsingCompleteDelegates = new ListBuffer[(RevisionEntity, File)=>Unit]


  def crawl(firstRev: Int, step : Int) {
    crawl(firstRev, lastRevisionNumber.toInt, step)
  }


  def crawl(firstRev: Int, lastRev: Int, step: Int) {
    val addedFiles = new ListBuffer[File]
    val modifiedFiles = new ListBuffer[File]
    val deletedFiles = new ListBuffer[File]


    //Project Checkout
    manager.getUpdateClient.doCheckout(svnUrl, new File(projectPath),
    SVNRevision.create(firstRev), SVNRevision.create(firstRev), SVNDepth.INFINITY, false)

    //Check Diffs
    var current = firstRev

    while(current < lastRev){
      println("Updating to Revision " + (current+step))
      manager.getUpdateClient.doUpdate(localPath, SVNRevision.create(current+step), SVNDepth.INFINITY, false, false)
      manager.getDiffClient.doDiffStatus(localPath, SVNRevision.create(current), localPath, SVNRevision.create(current+step), SVNDepth.INFINITY, false, new ISVNDiffStatusHandler {

        def handleDiffStatus(diffStatus: SVNDiffStatus) {
          val diffFile = diffStatus.getFile

          if (diffFile == null || diffFile.isDirectory)
            return

          //Check file extension
          val dotIndex: Int = diffFile.getName.lastIndexOf('.')
          if (dotIndex == -1)
            return

          val extension: String = diffFile.getName.substring(dotIndex, diffFile.getName.length)
          if (!allowedExtensions.contains(extension))
            return

          if (diffStatus.getModificationType.equals(SVNStatusType.STATUS_MODIFIED) ||
            diffStatus.getModificationType.equals(SVNStatusType.STATUS_REPLACED))
            modifiedFiles += diffFile
          else if (diffStatus.getModificationType.equals(SVNStatusType.STATUS_ADDED))
            addedFiles += diffFile
          else if (diffStatus.getModificationType.equals(SVNStatusType.STATUS_DELETED))
            deletedFiles += diffFile
        }
      })
      if (!modifiedFiles.isEmpty || !addedFiles.isEmpty || !deletedFiles.isEmpty) {
        val allUpdatedFiles = modifiedFiles ++ addedFiles
        val files: Array[File] = new Array[File](allUpdatedFiles.size)
        println("ADDED: " + addedFiles.toString())
        println("MODIFIED: " + modifiedFiles.toString())
        println("DELETED: " + modifiedFiles.toString())
        allUpdatedFiles.copyToArray(files)
        manager.getLogClient.doLog(files, SVNRevision.create(current+step), SVNRevision.create(current+step), SVNRevision.create(current+step), false, true, -1.asInstanceOf[Long], new ISVNLogEntryHandler {
          def handleLogEntry(logEntry: SVNLogEntry) {
            try {
              println("Generatig MSE files...")
              val mseFilePath = mseOutputPath + "/" + projectName + "_rev_" + (current+step) + ".mse"
              val mseFile = parser.execute(localPath.getCanonicalPath,  mseFilePath, false)
              println("Generation Done.")
              val revisionEntity = new RevisionEntity
              revisionEntity.addProperty("number", new IntValue(logEntry.getRevision.toString.toInt))
              revisionEntity.addProperty("comment", new StringValue(logEntry.getMessage))
              revisionEntity.addProperty("date", new StringValue(logEntry.getDate.toString))
              val developerEntity = new DeveloperEntity
              developerEntity.addProperty("name", new StringValue(logEntry.getAuthor))
              revisionEntity.addProperty("author", developerEntity)


              addedFiles foreach(
                (f) =>
                  revisionEntity.addProperty("addedFiles",new StringValue(f.getCanonicalPath
                  /*pathDifference(f.getCanonicalPath,localPath.getCanonicalPath)*/))
                )
              deletedFiles foreach(
                (f) =>
                  revisionEntity.addProperty("deletedFiles",new StringValue(f.getCanonicalPath
                  /*pathDifference(f.getCanonicalPath,localPath.getCanonicalPath)*/))
                )
              modifiedFiles foreach(
                (f) =>
                  revisionEntity.addProperty("modifiedFiles",new StringValue(f.getCanonicalPath
                  /*pathDifference(f.getCanonicalPath,localPath.getCanonicalPath)*/))
                )

              notifyOnParsingComplete(revisionEntity, mseFile)
            }
            catch {
              case e: IOException => {
                e.printStackTrace()
              }
            }
          }
        })
      }
      current += step
    }

  }

  def pathDifference(firstPath : String, secondPath : String) : String = {
        var index : Int = 0
      var go = true
        while(go && index < firstPath.length && index < secondPath.length){
        val c1 : Char = firstPath.charAt(index)
        val c2 : Char  = secondPath.charAt(index)
        go = (c1 == c2)
        index+=1
      }

      firstPath.substring(index)
  }


  private def notifyOnParsingComplete(revision : RevisionEntity, mseFile : File){
    onSourceParsingCompleteDelegates foreach((del) => del(revision, mseFile))
  }


}
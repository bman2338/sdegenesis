package ch.usi.genesis.data.repository

import org.tmatesoft.svn.core.io.SVNRepositoryFactory
import org.tmatesoft.svn.core.wc._
import ch.usi.inf.genesis.model.core.{IntValue, StringValue}
import java.io._
import org.tmatesoft.svn.core._
import collection.mutable.{HashMap, ListBuffer}
import ch.usi.inf.genesis.model.core.famix._
import java.util.Date
import java.lang.String
import ch.usi.inf.genesis.model.core.famix.RevisionEntityField

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
                 mseOutputPath: String, parser: ExternalParserWrapper,
                 auth: RepositoryUserAuth, allowedExtensions: String*) extends RepositoryCrawler {
  val svnUrl = SVNURL.parseURIEncoded(url)
  val authManager = if (auth == null) SVNWCUtil.createDefaultAuthenticationManager
  else SVNWCUtil.createDefaultAuthenticationManager(auth.getUserName, auth.getPassword)

  val options = SVNWCUtil.createDefaultOptions(true)
  val manager = SVNClientManager.newInstance(options, authManager)
  val repository = SVNRepositoryFactory.create(svnUrl)
  repository.setAuthenticationManager(authManager)

  val lastRevisionNumber = repository.getLatestRevision
  val localPath = new File(projectPath)
  val onSourceParsingCompleteDelegates = new ListBuffer[(RevisionEntity, Int, String, File) => Unit]
  val onCrawlingCompleteDelegates = new ListBuffer[(ListBuffer[RevisionEntity], String) => Unit]

  private val AllowedExtensions = ("(" + allowedExtensions.foldLeft("")((s1, s2) => {
    if (s1.length == 0) ".*" + s2 else s1 + "|.*" + s2
  }) + ")$").r

  private val numberOfTries = 5

  def crawl(firstRev: Int, step: Int) {
    crawl(firstRev, lastRevisionNumber.toInt, step)
  }


  def crawl(firstRev: Int, lastRev: Int, step: Int) {
    val history = new ListBuffer[RevisionEntity]

    //Clean already existing repository to prevent errors on interrupted updates.
    try {
      if (localPath.exists()) {
        println("Cleaning up repository")
        val cmd: String = "svn cleanup " + localPath.getCanonicalPath;
        Runtime.getRuntime.exec(cmd)
        println("Cleaning Done.")
      }
    } catch {
      case (e: IOException) => println(e.getMessage)
    }

    //Project Checkout
    doCheckout(firstRev)


    //Check Diffs
    var current = firstRev
    var next = current + 1

    while (next <= lastRev) {

      //Get Diff Status Logs
      val (addedFiles, modifiedFiles, deletedFiles) = doDiffStatus(current, next)

      if (!modifiedFiles.isEmpty || !addedFiles.isEmpty || !deletedFiles.isEmpty) {
        val revisionEntity = new RevisionEntity
        //Get revision's log information
        val logs = getLogInfo(next, addedFiles ++ modifiedFiles)

        //Add Logs for revision and previous revisions (if any)
        logs foreach ((l) => revisionEntity.addProperty(RevisionEntityField.LOGS, l))

        //Add revision Number
        revisionEntity.addProperty(RevisionEntityField.NUMBER, new IntValue(next))
        //Add deleted files in revision
        deletedFiles foreach ((f) => revisionEntity.addProperty(RevisionEntityField.DELETED_FILES, new StringValue(f)))


        if ((next - firstRev) % step == 0) {
          //Update Repository
          doUpdate(next)

          //Add addedFiles for revision with lines ownership
          getBlameInfo(next, addedFiles) foreach ((f) => revisionEntity.addProperty(RevisionEntityField.ADDED_FILES, f))
          //Add addedFiles for revision with lines ownership
          getBlameInfo(next, modifiedFiles) foreach ((f) => revisionEntity.addProperty(RevisionEntityField.MODIFIED_FILES, f))

          //Generate MSE File for that revision.
          println("Generatig MSE files...")
          val mseFilePath = new File(mseOutputPath + File.separator + projectName + "_rev_" + (current) + ".mse").getCanonicalPath
          val mseFile = parser.execute(localPath.getCanonicalPath, mseFilePath, false)
          println("Generation Done.")
          notifyOnParsingComplete(revisionEntity, next, projectName, mseFile)
        } else {
          addedFiles foreach ((f) => revisionEntity.addProperty(RevisionEntityField.ADDED_FILES, new StringValue(f)))
          modifiedFiles foreach ((f) => revisionEntity.addProperty(RevisionEntityField.MODIFIED_FILES, new StringValue(f)))
        }

        history += (revisionEntity)
      }


      current += 1
      next = current + 1
    }

    notifyOnCrawlingComplete(history)

  }


  def pathDifference(firstPath: String, secondPath: String): String = {
    var index: Int = 0
    var go = true
    while (go && index < firstPath.length && index < secondPath.length) {
      val c1: Char = firstPath.charAt(index)
      val c2: Char = secondPath.charAt(index)
      go = (c1 == c2)
      index += 1
    }

    firstPath.substring(index)
  }


  private def notifyOnParsingComplete(lastRevision: RevisionEntity,
                                      currentRevisionNumber: Int, projectName: String,
                                      mseFile: File) {
    onSourceParsingCompleteDelegates foreach ((delegate) => delegate(lastRevision, currentRevisionNumber, projectName, mseFile))
  }

  private def notifyOnCrawlingComplete(history: ListBuffer[RevisionEntity]) {
    onCrawlingCompleteDelegates foreach ((delegate) => delegate(history, projectName))
  }


  private def doCheckout(rev: Int) {
    var retry = false
    var n_tries = numberOfTries
    do{
      try {
        println("Project Checkout at rev. " + rev)
        manager.getUpdateClient.doCheckout(svnUrl, new File(projectPath),
          SVNRevision.create(rev), SVNRevision.create(rev), SVNDepth.INFINITY, false)
      }
      catch {
        case (e: SVNException) => {
          println(e)
          n_tries -= 1
          retry = if (n_tries <= 0) false else true
        }
      }
    }while(retry)
  }


  private def doUpdate(rev: Int) {
    var retry = false
    var n_tries = numberOfTries

    do{
      try {
        println("Updating to Revision " + (rev))
        manager.getUpdateClient.doUpdate(localPath, SVNRevision.create(rev), SVNDepth.INFINITY, false, false)
      }
      catch {
        case (e: SVNException) => {
          println(e)
          n_tries -= 1
          retry = if (n_tries <= 0) false else true
        }
      }
    }while (retry)
  }


  private def doDiffStatus(firstRev: Int, lastRev: Int): Tuple3[ListBuffer[String], ListBuffer[String], ListBuffer[String]] = {

    val modifiedFiles = new ListBuffer[String]
    val addedFiles = new ListBuffer[String]
    val deletedFiles = new ListBuffer[String]
    var retry = false
    var n_tries = numberOfTries

    do{
      try {
        println("Getting diff status between rev. " + firstRev + " and rev." + lastRev)
        manager.getDiffClient.doDiffStatus(svnUrl, SVNRevision.create(firstRev), svnUrl, SVNRevision.create(lastRev), SVNDepth.INFINITY, false, new ISVNDiffStatusHandler {

          def handleDiffStatus(diffStatus: SVNDiffStatus) {
            val diffFilePath = diffStatus.getPath
            //Check file extension
            diffFilePath match {
              case AllowedExtensions(_) => {
                if (diffStatus.getModificationType.equals(SVNStatusType.STATUS_MODIFIED) ||
                  diffStatus.getModificationType.equals(SVNStatusType.STATUS_REPLACED) ||
                  diffStatus.getModificationType.equals(SVNStatusType.MERGED))
                  modifiedFiles += diffFilePath
                else if (diffStatus.getModificationType.equals(SVNStatusType.STATUS_ADDED))
                  addedFiles += diffFilePath
                else if (diffStatus.getModificationType.equals(SVNStatusType.STATUS_DELETED))
                  deletedFiles += diffFilePath
              }
              case _ => return
            }
          }
        })
      } catch {
        case (e: SVNException) =>
          println(e)
          n_tries -= 1
          retry = if (n_tries <= 0) false else true
      }
    }while(retry)

    new Tuple3(addedFiles, modifiedFiles, deletedFiles)
  }


  private def getLogInfo(rev: Int, files: ListBuffer[String]): ListBuffer[LogEntity] = {

    val logEntities = new ListBuffer[LogEntity]
    var retry = false
    var n_tries = numberOfTries
    println("Getting logs between rev. " + rev + " and rev." + (rev))
    do{
      try {
        manager.getLogClient.doLog(svnUrl, files.toArray, SVNRevision.create(rev), SVNRevision.create(rev), SVNRevision.create(rev), false, true, -1L, new ISVNLogEntryHandler {
          def handleLogEntry(logEntry: SVNLogEntry) {

            val logEntity = new LogEntity
            logEntity.addProperty("number", new IntValue(logEntry.getRevision.toString.toInt))
            logEntity.addProperty("comment", new StringValue(logEntry.getMessage))
            logEntity.addProperty("date", new StringValue(logEntry.getDate.toString))
            logEntity.addProperty("authorId", new StringValue(logEntry.getAuthor))
            logEntities += logEntity
          }
        })
      } catch {
        case e: SVNException => {
          println(e)
          n_tries -= 1
          retry = if (n_tries <= 0) false else true
        }
      }
    }while(retry)

    logEntities
  }


  private def getBlameInfo(rev: Int, files: ListBuffer[String]): ListBuffer[FileEntity] = {
    val fileEntitities = new ListBuffer[FileEntity]
    var retry = false
    var n_tries = numberOfTries
    var fileEntity = new FileEntity
    println("Retrieve Blame information")
    files foreach (
      (f) => {

        do{
        try {
          val file = new File(localPath + File.separator + f)
          manager.getLogClient.doAnnotate(file, SVNRevision.create(rev), SVNRevision.create(1), SVNRevision.create(rev), false, new ISVNAnnotateHandler {
            def handleRevision(p1: Date, p2: Long, p3: String, p4: File) = false

            def handleEOF() {
              fileEntity.addProperty(FileEntityProperty.NAME, new StringValue(f))
              fileEntitities += fileEntity
              fileEntity = new FileEntity
            }

            def handleLine(date: Date, revision: Long, author: String, text: String) {}

            def handleLine(date: Date, revision: Long, author: String, text: String,
                           p5: Date, p6: Long, p7: String, p8: String, lineNumber: Int) {
              val line = new LineEntity
              line.addProperty(LineEntityProperty.NUMBER, new IntValue(lineNumber))
              line.addProperty(LineEntityProperty.AUTHOR, new StringValue(author))
              line.addProperty(LineEntityProperty.REVISION, new IntValue(revision.toInt))
              line.addProperty(LineEntityProperty.DATE, new StringValue(date.toString))
              fileEntity.addProperty(FileEntityProperty.LINES, line)
            }
          })
        } catch {
          case e: SVNException => {
            println(e)
            n_tries -= 1
            retry = if (n_tries <= 0) false else true
          }
        }
        }while (retry)
        })

    fileEntitities
  }

}
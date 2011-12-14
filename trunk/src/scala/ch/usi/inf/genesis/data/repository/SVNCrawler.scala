package ch.usi.genesis.data.repository

import org.tmatesoft.svn.core.io.SVNRepositoryFactory
import org.tmatesoft.svn.core.wc._
import java.io._
import org.tmatesoft.svn.core._
import collection.mutable.{HashMap, ListBuffer}
import ch.usi.inf.genesis.model.core.famix._
import java.util.Date
import java.lang.String
import ch.usi.inf.genesis.model.core.famix.RevisionEntityProperty
import ch.usi.inf.genesis.model.core.{BooleanValue, IntValue, StringValue}
import ch.usi.inf.genesis.model.core.Metric

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
  val onSourceParsingCompleteDelegates = new ListBuffer[(RevisionEntity, String, String, File) => Unit]
  val onCrawlingCompleteDelegates = new ListBuffer[(ListBuffer[RevisionEntity], String, String) => Unit]

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
    doCleanup()


    //Project Checkout
    doCheckout(firstRev)

    //Create Revision Entity and Snapshot for first revision
    //    val rev = new RevisionEntity
    //    val (author, date) = getRevisionInfo(firstRev)
    //    rev.addProperty(RevisionEntityProperty.NUMBER, new IntValue(firstRev))
    //    rev.addProperty(RevisionEntityProperty.AUTHOR, new StringValue(author))
    //    rev.addProperty(RevisionEntityProperty.DATE, new StringValue(date))
    //    doSnapshot(rev, firstRev)

    //Check Diffs
    var current = firstRev
    var next = current + 1

    while (next <= lastRev) {

      //Get Diff Status Logs
      val (addedFiles, modifiedFiles, deletedFiles) = doDiffStatus(current, next)

      if (!modifiedFiles.isEmpty || !addedFiles.isEmpty || !deletedFiles.isEmpty ||(next - firstRev) % step == 0) {
        val revisionEntity = new RevisionEntity
        val (author, date) = getRevisionInfo(next)
        revisionEntity.addProperty(RevisionEntityProperty.NUMBER, new IntValue(next))
        revisionEntity.addProperty(RevisionEntityProperty.AUTHOR, new StringValue(author))
        revisionEntity.addProperty(RevisionEntityProperty.DATE, new StringValue(date))

        //Add deleted files in revision
        deletedFiles foreach ((f) => {
          val entity = new FileEntity
          entity.addProperty(FileEntityProperty.NAME, new StringValue(f))
          revisionEntity.addProperty(RevisionEntityProperty.DELETED_FILES, new StringValue(f))
        })
        revisionEntity.addProperty(RevisionEntityProperty.PROJECT, new StringValue(projectName + "_rev" + next))

        if ((next - firstRev) % step == 0) {
          //Update Repository
          doUpdate(next)

          //Add addedFiles for revision with lines ownership
          //getBlameInfo(next, addedFiles) foreach ((f) => revisionEntity.addProperty(RevisionEntityProperty.ADDED_FILES, f))
          //Add addedFiles for revision with lines ownership
          //getBlameInfo(next, modifiedFiles) foreach ((f) => revisionEntity.addProperty(RevisionEntityProperty.MODIFIED_FILES, f))

            revisionEntity.addProperty(RevisionEntityProperty.HAS_MSE, new BooleanValue(true))

          //Generate MSE File for that revision.
          //doSnapshot(revisionEntity, next)

        } else {
          revisionEntity.addProperty(RevisionEntityProperty.HAS_MSE, new BooleanValue(false))

          addedFiles foreach ((f) => {
            val entity = new FileEntity
            entity.addProperty(FileEntityProperty.NAME, new StringValue(f))
            revisionEntity.addProperty(RevisionEntityProperty.ADDED_FILES, entity)
          })
          modifiedFiles foreach ((f) => {
            val entity = new FileEntity
            entity.addProperty(FileEntityProperty.NAME, new StringValue(f))
            revisionEntity.addProperty(RevisionEntityProperty.MODIFIED_FILES, entity)
          })

        }

        revisionEntity.addProperty(RevisionEntityProperty.ADDED_FILES_COUNT, new IntValue(addedFiles.length))
        revisionEntity.addProperty(RevisionEntityProperty.MODIFIED_FILES_COUNT, new IntValue(modifiedFiles.length))
        revisionEntity.addProperty(RevisionEntityProperty.DELETED_FILES_COUNT, new IntValue(deletedFiles.length))

        history += (revisionEntity)
      }

      current += 1
      next = current + 1
      if (history.length >= 100) {
        notifyOnCrawlingComplete(history)
        history.clear()
      }
    }

    if (history.length > 0)
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
                                      projectName: String,
                                      mseFile: File) {
    onSourceParsingCompleteDelegates foreach ((delegate) => delegate(lastRevision, projectName, projectPath, mseFile))
  }

  private def notifyOnCrawlingComplete(history: ListBuffer[RevisionEntity]) {
    onCrawlingCompleteDelegates foreach ((delegate) => delegate(history, projectName, projectPath))
  }


  private def doCheckout(rev: Int) {
    var retry = false
    var n_tries = numberOfTries
    do {
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
    } while (retry)
  }

  private def doCleanup() {
    try {
      println("Cleaning up repository...")
      manager.getWCClient.doCleanup(localPath)
      println("Cleaning Done.")
    } catch {
      case (e: SVNException) => println(e.getMessage)
    }
  }

  private def doUpdate(rev: Int) {
    var retry = false
    var n_tries = numberOfTries

    do {
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
    } while (retry)
  }


  private def doDiffStatus(firstRev: Int, lastRev: Int): Tuple3[ListBuffer[String], ListBuffer[String], ListBuffer[String]] = {

    val modifiedFiles = new ListBuffer[String]
    val addedFiles = new ListBuffer[String]
    val deletedFiles = new ListBuffer[String]
    var retry = false
    var n_tries = numberOfTries

    do {
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
    } while (retry)

    new Tuple3(addedFiles, modifiedFiles, deletedFiles)
  }

  private def getBlameInfo(rev: Int, files: ListBuffer[String]): ListBuffer[FileEntity] = {
    val fileEntities = new ListBuffer[FileEntity]
    var retry = false
    var n_tries = numberOfTries
    var fileEntity = new FileEntity
    println("Retrieve Blame information")
    files foreach (
      (f) => {

        do {
          try {
            val file = new File(localPath + File.separator + f)
            manager.getLogClient.doAnnotate(file, SVNRevision.create(rev), SVNRevision.create(1), SVNRevision.create(rev), false, new ISVNAnnotateHandler {
              def handleRevision(p1: Date, p2: Long, p3: String, p4: File) = false

              def handleEOF() {
                fileEntity.addProperty(FileEntityProperty.NAME, new StringValue(f))
                fileEntities += fileEntity
                fileEntity = new FileEntity
              }

              def handleLine(date: Date, revision: Long, author: String, text: String) {}

              def handleLine(date: Date, revision: Long, author: String, text: String,
                             p5: Date, p6: Long, p7: String, p8: String, lineNumber: Int) {
                if (text.length() <= 0)
                  return

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
        } while (retry)
      })

    fileEntities
  }

  private def doSnapshot(revisionEntity: RevisionEntity, rev: Int) {
    println("Generatig MSE files...")
    //val mseFilePath = new File(mseOutputPath + File.separator + projectName + "_rev_" + (rev) + ".mse").getCanonicalPath
    val mseFilePath = new File(mseOutputPath + File.separator + projectName +"_"+ (rev) + ".mse").getCanonicalPath
    val mseFile = parser.execute(localPath.getCanonicalPath, mseFilePath, false)
    println("Generation Done.")
    notifyOnParsingComplete(revisionEntity, projectName, mseFile)
  }

  private def getRevisionInfo(rev: Int): (String, String) = {
    try {
      println("Retrieving Information for Rev." + rev)
      val info = manager.getWCClient.doInfo(svnUrl, SVNRevision.create(rev), SVNRevision.create(rev))
      println(info.getAuthor + "\t" + info.getCommittedDate)
      return (info.getAuthor, info.getCommittedDate.toString)
    } catch {
      case (e: SVNException) =>
        println(e)
        return ("", "")
    }

  }
}
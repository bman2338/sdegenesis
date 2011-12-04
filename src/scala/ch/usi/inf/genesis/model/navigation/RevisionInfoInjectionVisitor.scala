package ch.usi.inf.genesis.model.navigation

import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import ch.usi.inf.genesis.data.repository.RevisionInformation
import ch.usi.inf.genesis.model.core.famix._
import ch.usi.inf.genesis.model.core._
import java.io.File
import java.util.ArrayList

/**
 * @author Luca Ponzanelli
 * @param revisionInfo the information concerning the revision's information to be pushed
 *
 * Injects Repository Information inside the original MSE meta-model.
 * Structure:
 *          Entity["revision"] => RevisionEntity["number"]        =>  IntValue
 *                                RevisionEntity["comment"]       =>  StringValue
 *                                RevisionEntity["date"]          =>  StringValue
 *                                RevisionEntity["author"]        =>  DeveloperEntity
 *                                RevisionEntity["addedFiles"]    =>  ListBuffer[StringValue]
 *                                RevisionEntity["modifiedFiles"] =>  ListBuffer[StringValue]
 *                                RevisionEntity["deletedFiles"]  =>  ListBuffer[StringValue]
 *
 */

class RevisionInfoInjectionVisitor(revisionInfo : RevisionInformation) extends ModelVisitor {

  def visit(obj: ModelObject): NavigatorOption = {
      //BugTracker Dev. Entity
      val dev = new BTDeveloperEntity
      dev.addProperty("name", new StringValue(revisionInfo.getAuthor))

      //Revision Entity
      val revEntity = new RevisionEntity
      revEntity.addProperty("number", new IntValue(revisionInfo.getRevisionNumber.toString.toInt))
      revEntity.addProperty("author", dev)
      revEntity.addProperty("comment", new StringValue(revisionInfo.getComment))
      revEntity.addProperty("date", new StringValue(revisionInfo.getDate.toString))

      var it = revisionInfo.getAddedFiles.iterator
      while(it.hasNext){
        val file = it.next
        revEntity.addProperty("addedFiles", new StringValue(file.getName))
      }

      it = revisionInfo.getModifiedFiles.iterator
      while(it.hasNext){
        val file = it.next
        revEntity.addProperty("modifiedFiles", new StringValue(file.getName))
      }

      it = revisionInfo.getDeletedFiles.iterator
      while(it.hasNext){
        val file = it.next
        revEntity.addProperty("deletedFiles", new StringValue(file.getName))
      }

      obj.addProperty("revision", revEntity)

			CONTINUE
	}

  /**
   * @author Luca Ponzanelli
   * @return A function pointer to the selection method.
   *
   * It filters out all the ModelObjects that not have a reference to a SourceAnchor and its related FileAnchor.
   * In case the reference to those anchors is found, it filters out objects whose FileAnchor's "fileName" property
   * is not included in the files affected by the revision.
   */
	def selectionMethod() : (ModelObject => Boolean) = {
			((obj) => obj.getProperty("sourceAnchor") match {
				case Some(fileAnchor :ModelObject) =>
					fileAnchor.getProperty("fileName") match{
  						case Some(fileName : StringValue) => isFileAffected(fileName.value)
  						case _ =>	false
				}
				case _ =>	false
			})
	}
	
	private def isFileAffected(fileName : String) : Boolean = {
			val updated = new ArrayList[File] 
			updated.addAll(revisionInfo.getAddedFiles)
			updated.addAll(revisionInfo.getModifiedFiles)
			val it = updated.iterator
			while(it.hasNext){
				val file = it.next
				if(file.toString.contains(fileName)){
					return true
        }
			}
			false
	}
}
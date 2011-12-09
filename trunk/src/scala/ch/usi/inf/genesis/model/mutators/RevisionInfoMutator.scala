package ch.usi.inf.genesis.model.mutators

import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import ch.usi.inf.genesis.model.core.famix._
import ch.usi.inf.genesis.model.core._
import java.io.File
import collection.mutable.ListBuffer

/**
 * @author Luca Ponzanelli
 * @param revisions the information concerning the revisions' information to be pushed
 *
 * Injects Repository Information inside the original MSE meta-model.
 * Structure:
 * Entity["revision"] => ListBuffer[RevisionEntity] => RevisionEntity["number"]        =>  IntValue
 *                                                     RevisionEntity["comment"]       =>  StringValue
 *                                                     RevisionEntity["date"]          =>  StringValue
 *                                                     RevisionEntity["author"]        =>  DeveloperEntity
 *                                                     RevisionEntity["addedFiles"]    =>  ListBuffer[StringValue]
 *                                                     RevisionEntity["modifiedFiles"] =>  ListBuffer[StringValue]
 *                                                     RevisionEntity["deletedFiles"]  =>  ListBuffer[StringValue]
 *
 */

class RevisionInfoMutator(revisions : ListBuffer[RevisionEntity]) extends ModelMutator {

  def visit(obj: ModelObject): NavigatorOption = {

      revisions foreach( (rev) => obj.addProperty("revision", rev))
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
	override def getSelection() : (ModelObject => Boolean) = {
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
      revisions foreach(
        (revisionInfo) =>{
          val updated = new ListBuffer[File]
          revisionInfo.getProperty("addedFiles") match{
            case Some(name : StringValue) =>
              if(name.value.contains(fileName)){
                return true
              }
            case _ =>
          }

          revisionInfo.getProperty("modifiedFiles") match{
            case Some(name : StringValue) =>
              if(name.value.contains(fileName))
                return true
            case _ =>
          }
        })

			false
	}
}
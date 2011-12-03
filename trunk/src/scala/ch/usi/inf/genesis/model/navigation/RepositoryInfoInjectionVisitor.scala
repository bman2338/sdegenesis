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
 *          Entity["revision"] => RevisionEntity["number"]      =>  IntValue
 *                                RevisionEntity["comment"]     =>  StringValue
 *                                RevisionEntity["date"]        =>  StringValue
 *                                RevisionEntity["author"]      =>  BTDeveloperEntity
 *                                RevisionEntity["fileAdded"]   =>  ListBuffer[StringValue]
 *                                RevisionEntity["fileRemoved"] =>  ListBuffer[StringValue]
 *                                RevisionEntity["fileDeleted"] =>  ListBuffer[StringValue]
 *
 */

class RepositoryInfoInjectionVisitor(revisionInfo : RevisionInformation) extends ModelVisitor {

  def visit(obj: ModelObject): NavigatorOption = {
	  obj.getProperty("sourceAnchor") match {
		  case Some(fileAnchor) =>
			  println("File Anchor: " + fileAnchor.getName())
				fileAnchor.getProperty("fileName") match{
  			  case Some(fileName : StringValue) => println(fileName.value); CONTINUE
  				case _ =>	CONTINUE
				}
			case _ =>
			  CONTINUE
			}

      val revEntity = new RevisionEntity
      val btDev = new BTDeveloperEntity
      btDev.addProperty("name", new StringValue(revisionInfo.getAuthor))

      revEntity.addProperty("author", btDev)
      revEntity.addProperty("number", new IntValue(-1))    //TODO change RevisionInformation number field to Integer
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

			CONTINUE
	}


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
			val fileToCheck = new File(fileName)
			val updated = new ArrayList[File] 
			updated.addAll(revisionInfo.getAddedFiles)
			updated.addAll(revisionInfo.getModifiedFiles)
			val it = updated.iterator
			while(it.hasNext()){
				val file = it.next()	
				println(file.getName + "\t" + fileToCheck.getName);
				if(file.getName.equals(fileToCheck.getName))
					true
				
			}
			false
	}
}
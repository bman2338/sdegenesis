package ch.usi.inf.genesis.model.navigation
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.data.repository.RevisionInformation
import ch.usi.inf.genesis.model.core.famix._
import ch.usi.inf.genesis.model.core._
import scala.collection.mutable.ListBuffer
import java.io.File
import java.util.ArrayList



class RepositoryInfoInjectionVisitor(revisionInfo : RevisionInformation) extends ModelVisitor {
  
	def visit(obj: ModelObject): NavigatorOption = {
//			obj.getProperty("sourceAnchor") match {
//				case Some(fileAnchor) => 
//					println("File Anchor: " + fileAnchor.getName())
//					fileAnchor.getProperty("fileName") match{
//  						case Some(fileName : StringValue) => println(fileName.value); CONTINUE
//  						case _ =>	CONTINUE
//					}
//				case _ =>
//						CONTINUE
//			}
	  
			//TODO if visited, add repository information. Already filtered out by selectionMethod
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
			updated.addAll(revisionInfo.getAddedFiles())
			updated.addAll(revisionInfo.getModifiedFiles())
			val it = updated.iterator()
			while(it.hasNext()){
				val file = it.next()	
				println(file.getName() + "\t" + fileToCheck.getName());
				if(file.getName().equals(fileToCheck.getName()))
					true
				
			}
			false
	}
}
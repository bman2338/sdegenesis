package ch.usi.inf.genesis.model.navigation

import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.core.Project
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import scala.collection.mutable.HashSet

class DepthFirstNavigator extends Navigator {
	override def walk(modelObject: ModelObject, visitor: ModelVisitor, selection: Option[HashSet[String]]) : NavigatorOption = {
	  selection match { 
	            case Some(set) => println("Not Implemented")
	            case None =>
	         }
	  
	  //the model Object will call visit on the visitor 
	  modelObject.accept(visitor) match {
	     case CONTINUE =>  {
	       modelObject.properties.foreach((pair) => {
	         val list = pair._2
	        // println(pair._1)
	         list.foreach(value => {
	           walk(value, visitor, selection) match {
	             case CONTINUE =>
	             case STOP => return STOP
	           }
	         }) 
	       })
	       return CONTINUE   
	   }
	     case SKIP_SUBTREE => return CONTINUE
	     case STOP => return STOP
	     
	   }
	}
	
	

	
}
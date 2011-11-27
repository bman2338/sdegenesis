package ch.usi.inf.genesis.model.navigation

import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.core.Project
import ch.usi.inf.genesis.model.navigation.NavigatorOption._

class DepthFirstNavigator extends Navigator {
	override def walk(modelObject: ModelObject, visitor: ModelVisitor) : NavigatorOption = {
	 
	  //the model Object will call visit on the visitor 
	  modelObject.accept(visitor) match {
	     case CONTINUE =>  {
	       modelObject.properties.foreach((pair) => {
	         val list = pair._2
	         list.foreach(value => {
	           walk(value, visitor) match {
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
package ch.usi.inf.genesis.model.core.famix

import scala.collection.mutable.Map
import scala.collection.mutable.HashMap
import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.core.FAMIX


class ClassEntity extends Entity {
	override def internalAddProperty(propertyName:String,propertyValue:ModelObject) = {
	  propertyName match {
	    case FAMIX.CONTAINER =>
	      println(propertyValue)
	      propertyValue.addProperty(FAMIX.CLASSES_PROP,this)
	    case _ => super.internalAddProperty(propertyName,propertyValue)
	  }
	}
}

	 			  
	 			  
	 			  
	 	
package ch.usi.inf.genesis.model.core.famix

import scala.collection.mutable.HashMap
import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.core.FAMIX

class MethodEntity extends Entity {
	override def internalAddProperty(propertyName:String,propertyValue:ModelObject) = {
	  propertyName match {
	    case FAMIX.PARENTTYPE => propertyValue.addProperty(FAMIX.METHODS_PROP,this)
	    case _ => super.internalAddProperty(propertyName,propertyValue)
	  }
	}
}
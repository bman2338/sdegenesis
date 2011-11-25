package ch.usi.inf.genesis.model.core.famix

import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.core.FAMIX

class AttributeEntity (name: String)  extends Entity(name) {
	override def internalAddProperty(propertyName:String,propertyValue:ModelObject) = {
	  propertyName match {
	    case FAMIX.PARENTTYPE => propertyValue.addProperty(FAMIX.ATTRIBUTES_PROP,this)
	    case _ => super.internalAddProperty(propertyName,propertyValue)
	  }
	}
}

package ch.usi.inf.genesis.model.core.famix
import scala.collection.mutable.HashMap

import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.core.FAMIX

class InheritanceDefinitionRelation extends FamixObject {
	override def internalAddProperty(propertyName:String,propertyValue:ModelObject) = {
	  propertyName match {
	    case FAMIX.SUBCLASS => { 
	      val superclass = properties.get(FAMIX.SUPERCLASS_PROP)
	      superclass match {
	        case Some(cl) => 
	        cl.foreach((superclass) => {
	          superclass.addProperty(FAMIX.SUBCLASS_PROP,propertyValue)
	          propertyValue.addProperty(FAMIX.SUPERCLASS_PROP,superclass)
	          })
	          this.properties -= FAMIX.SUPERCLASS_PROP
	        case None => super.internalAddProperty(propertyName,propertyValue)
	      }
	    }
	    case FAMIX.SUPERCLASS => {
	      val subclass = properties.get(FAMIX.SUBCLASS_PROP)
	      subclass match {
	        case Some(cl) => cl.foreach((subclass) => {
	          subclass.addProperty(FAMIX.SUPERCLASS_PROP,propertyValue)
	          propertyValue.addProperty(FAMIX.SUBCLASS_PROP,subclass)
	          this.properties -= FAMIX.SUBCLASS_PROP
	        })
	        case None => super.internalAddProperty(propertyName,propertyValue)
	      }
	    }
	    case _ => super.internalAddProperty(propertyName,propertyValue)
	  }
	}
}
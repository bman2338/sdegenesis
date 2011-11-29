package ch.usi.inf.genesis.model.core.famix

import scala.collection.mutable.HashMap
import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.core.FAMIX

case class InvocationRelation extends OneToManyRelation {
	override def internalAddProperty(propertyName:String,propertyValue:ModelObject) = {
	  propertyName match {
	    //case FAMIX.RECEIVER => propertyValue.addProperty(FAMIX.RECV_INVOCATIONS_PROP,this)
	    //case FAMIX.SENDER => propertyValue.addProperty(FAMIX.SEND_INVOCATIONS_PROP,this)
	    case FAMIX.CANDIDATES => propertyValue.addProperty(FAMIX.RECV_INVOCATIONS_PROP,this)
	    case _ =>
	  }
	  super.internalAddProperty(propertyName,propertyValue)
	}
}
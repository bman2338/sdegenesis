package ch.usi.inf.genesis.model.core.famix

import scala.collection.mutable.HashMap
import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.core.FAMIX

case class InvocationRelation extends OneToManyRelation {
	override def internalAddProperty(propertyName:String,propertyValue:ModelObject) = {
	  propertyName match {
	   	//case FAMIX.RECEIVER => propertyValue.addProperty(FAMIX.RECV_INVOCATIONS_PROP,this)
	    case FAMIX.SENDER => propertyValue.addProperty(FAMIX.SEND_INVOCATIONS_PROP, this)
	    case FAMIX.CANDIDATES => propertyValue.addProperty(FAMIX.RECV_INVOCATIONS_PROP,this)
	    case _ =>
	  }
	//  super.internalAddProperty(propertyName,propertyValue)
//	
	//println(propertyName + " : " + propertyValue +" : " + propertyValue.getName());
//	  propertyName match {
//	    case FAMIX.SENDER => { 
//	      val receiver = getProperties(FAMIX.CANDIDATES)
//	      receiver match {
//	        case Some(cl) => 
//	          cl.foreach((recv) => {
//	          recv.addProperty(FAMIX.INVOKEDBYMETHODS_PROP, propertyValue);
//	          propertyValue.addProperty(FAMIX.INVOKINGMETHODS_PROP, recv);
//	          })
//	          this.properties -= FAMIX.CANDIDATES
//	          
//	        case None => super.internalAddProperty(propertyName, propertyValue)
//	      }
//	    }
//	    
//	    case FAMIX.CANDIDATES => {
//	      val sender = properties.get(FAMIX.SENDER)
//	      sender match {
//	        case Some(cl) => cl.foreach((send) => {
//	          send.addProperty(FAMIX.INVOKINGMETHODS_PROP, propertyValue)
//	          propertyValue.addProperty(FAMIX.INVOKEDBYMETHODS_PROP, send)
//	        })
//	        case None => super.internalAddProperty(propertyName, propertyValue)
//	      }
//	      
//	      this.properties -= FAMIX.SENDER
//	    }
//	    case _ => super.internalAddProperty(propertyName, propertyValue)
//	  }
	}
}
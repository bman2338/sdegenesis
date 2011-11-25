package ch.usi.inf.genesis.model.core
import scala.collection.mutable.HashMap

import scala.collection.mutable.ListBuffer

import ch.usi.inf.genesis.model.core.famix.Property

abstract class ModelObject {
 val properties: HashMap[String,  ListBuffer[ModelObject]] = new HashMap()
 
   final def addProperty (propertyName: String, propertyValue: ModelObject) : Unit = {
	if (!checkProperty(propertyName,propertyValue))
     return;
	internalAddProperty(propertyName,propertyValue)
   }
 
   protected def internalAddProperty (propertyName: String, propertyValue: ModelObject) : Unit = {
    var list: Option[ListBuffer[ModelObject]] = properties.get(propertyName)
    list match {
    	case None => properties +=  propertyName -> ListBuffer(propertyValue)
    	case Some(list) => list += propertyValue
    }
   }
 
   def checkProperty (propertyName: String, propertyValue: ModelObject) : Boolean = {
    propertyValue match {
      case Property(n) => {
    	  propertyValue.properties.foreach((pair) =>
    	  	pair._2.foreach((element) =>
    	  	  if (element != null)
    	  		  internalAddProperty(propertyName,element)
    	  ))
    	  false
      }
      case _ => {
    	  true
      }
    } 
  }
 override def toString = {
	/*properties.foreach((pair) => { 
	  print((pair._1.toString) + "") 
	  pair._2.foreach ((el) => println(el))
	})*/
   	super.toString()
  }
}
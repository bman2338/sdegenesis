package ch.usi.inf.genesis.model.core
import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer
import ch.usi.inf.genesis.model.core.famix.Property
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import ch.usi.inf.genesis.model.navigation.ModelVisitor

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
      case Property() => {
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
 def accept(visitor: ModelVisitor) : NavigatorOption = {
   return visitor.visit(this);
 }
 
 
 def getName() = {
   val name = properties.get(FAMIX.NAME_PROP)
    name match {
     case Some(xs) if (xs.length > 0) => Some(xs.first); 
     case _ => None
   }
 }
 
 def getUniqueIdentifier (parentIdentifier : String) = {
   val name = properties.get(FAMIX.NAME_PROP)
   
   name match {
     case Some(xs) if (xs.length > 0) => Some(parentIdentifier + "." + xs.first); 
     case _ => None
   }
 }
 
}
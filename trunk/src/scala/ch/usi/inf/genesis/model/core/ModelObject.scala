package ch.usi.inf.genesis.model.core
import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer

abstract class ModelObject {
 val properties: HashMap[String,  ListBuffer[ModelObject]] = new HashMap()
 
   def addProperty (propertyName: String, propertyValue: ModelObject) = {
    var list: Option[ListBuffer[ModelObject]] = properties.get(propertyName)
    list match {
      case None => properties +=  propertyName -> ListBuffer(propertyValue)
      case Some(list) =>  list += propertyValue
    }
    
    ()
 
  }
}
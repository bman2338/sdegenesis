package ch.usi.inf.genesis.model.core

import scala.collection.mutable.HashMap
import scala.collection.mutable.ListBuffer
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import ch.usi.inf.genesis.model.navigation.ModelVisitor
import ch.usi.inf.genesis.model.core.famix.Property

abstract class ModelObject {
  val properties: HashMap[String, ListBuffer[ModelObject]] = new HashMap()
  val id = IdFactory.nextId()
  var uniqueId: Option[String] = None;

  final def addProperty(propertyName: String, propertyValue: ModelObject): Unit = {
    if (!checkProperty(propertyName, propertyValue))
      return;
    internalAddProperty(propertyName, propertyValue)
  }

  protected def internalAddProperty(propertyName: String, propertyValue: ModelObject): Unit = {
    var list: Option[ListBuffer[ModelObject]] = properties.get(propertyName)
    list match {
      case None => properties += propertyName -> ListBuffer(propertyValue)
      case Some(list) => list += propertyValue
    }
  }

  def checkProperty(propertyName: String, propertyValue: ModelObject): Boolean = {
    propertyValue match {
      case Property() => {
        propertyValue.properties.foreach((pair) =>
          pair._2.foreach((element) =>
            if (element != null)
              internalAddProperty(propertyName, element)
          ))
        false
      }
      case _ => {
        true
      }
    }
  }

  def accept(visitor: ModelVisitor): NavigatorOption = {
    return visitor.visit(this);
  }

  def getId(): Int = id

  def getName(): String = {
    val name = properties.get(FAMIX.NAME_PROP)
    name match {
      case Some(xs) if (xs.length > 0) => {
        xs.head.toString()
      }
      case _ => ""
    }
  }

  def getProperties(key: String) = properties.get(key);

  def getProperty(key: String): Option[ModelObject] = properties.get(key) match {
    case Some(xs) if !xs.isEmpty => Some(xs.head)
    case None => None
  }

  def getPropertiesOrAdd(key: String): ListBuffer[ModelObject] = properties.get(key) match {
    case Some(xs) => xs
    case None =>
      val list = new ListBuffer[ModelObject]
      properties += key -> list
      list
  }

  def getPropertyOrAdd(key: String, stub: ModelObject): ModelObject = {
    var list = getPropertiesOrAdd(key)
    if (list.isEmpty) {
      list += stub
      stub
    }
    else
      list.head
  }


  def getUniqueId(): Option[String] = {
    //    var name = getProperty(FAMIX.NAME_PROP)
    //    name match {
    //      case None => None
    //      case Some(n) => Some(n + "_" + id.toString)
    //    }

    uniqueId
  }

  def setUniqueId(uniqueId: String): Unit = {
    this.uniqueId = Some(uniqueId);
  }
}
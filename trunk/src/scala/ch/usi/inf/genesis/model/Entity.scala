package ch.usi.inf.genesis.model

import scala.collection.mutable.ListBuffer

import ch.usi.inf.genesis.model.core.ModelObject

abstract class Entity {
	var modelObject : ModelObject = null
    def resolve (pool: ListBuffer[Entity]);
}

abstract class Value extends Entity {
    def resolve (pool: ListBuffer[Entity]) = this
}

class BooleanValue(val boolean:Boolean) extends Value {
  override def toString = boolean.toString
}

class DoubleValue (val number:Double) extends Value {
  override def toString = number.toString
}

class IntValue (val number:Int) extends Value {
  override def toString = number.toString
}

class StringValue (val string:String) extends Value {
  override def toString = string
}

abstract class Reference extends Entity;

class StringReference(val id:String) extends Reference {
  override def toString = id
}

class IntReference(val id:Int) extends Reference {
  override def toString = id.toString
}

class Node extends Entity {
    def unapply(e:Entity) : Option[Node] = Some(this)
    val children:ListBuffer[Entity] = new ListBuffer[Entity]
    def addChild (entity:Entity) = children += entity 	
    def resolve (pool : ListBuffer[Entity]) : ListBuffer[ModelObject] = {
        val node = this
        val objects = new ListBuffer[ModelObject]
		children.foreach((child) => child match {
		  case node(el) => println("Node")
		}
    )
    objects
    }
}

class Attribute(val name:String) extends Node {
  override def toString = name
}

case class Element(elementName:String, val modelName:String, val id:Option[Int]) extends Attribute(elementName) {
  
}
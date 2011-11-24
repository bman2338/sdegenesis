package ch.usi.inf.genesis.model.core.famix

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.HashMap

import ch.usi.inf.genesis.model.core.ModelObject

class FamixObject extends ModelObject {
  val properties: HashMap[String,ModelObject] = new HashMap()
}
package ch.usi.inf.genesis.model.core.famix

import scala.collection.mutable.ListBuffer

import ch.usi.inf.genesis.model.core.ModelObject

class FamixObject(val sourceAnchor: Qualifier, 
				  val properties: ListBuffer[Property] = new ListBuffer()) extends ModelObject {
  
  def addProperty(property: Property) = properties += property
}
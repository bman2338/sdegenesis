package ch.usi.inf.genesis.model.core.famix

import scala.collection.mutable.Map
import scala.collection.mutable.HashMap



//abstract class Entity;



class ClassEntity(name: String, 
				 //Class Entity specific fields
				  var inheritance: InheritanceDefinitionRelation = null) 
	 			  extends Entity(name) {
  val attributes: Map[String, AttributeEntity] = new HashMap()
  val methods: Map[String, MethodEntity] = new HashMap()
}
	 			  
	 			  
	 			  
	 	
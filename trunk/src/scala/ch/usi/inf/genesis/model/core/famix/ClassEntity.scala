package ch.usi.inf.genesis.model.core.famix

import scala.collection.mutable.Map
import scala.collection.mutable.HashMap



//abstract class Entity;



class ClassEntity(sourceAnchor: Qualifier, 
				  name: String, 
				  uniqueName: String,
				 //Class Entity specific fields
				  var inheritance: InheritanceDefinitionRelation = null,
	 			  var attributes: Map[String, AttributeEntity] = new HashMap(),
	 			  var methods: Map[String, MethodEntity] = new HashMap() ) 
	 			  extends Entity(sourceAnchor, name, uniqueName) 
	 			  
	 			  
	 			  
	 	
package scala.ch.usi.inf.genesis.model.core
import scala.collection.mutable.Map
import scala.collection.mutable.HashMap



//abstract class Entity;



class ClassEntity(var superClasses: Map[String, InheritanceDefinitionEntity] = new HashMap(),
	 			  var attributes: Map[String, AttributeEntity] = new HashMap(),
	 			  var methods: Map[String, MethodEntity] = new HashMap() ) 
//	 			  extends Entity 
	 			  
	 			  
	 			  
	 	
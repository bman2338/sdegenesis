package ch.usi.inf.genesis.model.core.famix

import scala.collection.mutable.HashMap

class MethodEntity(sourceAnchor: Qualifier, 
				   name: String, 
				   uniqueName: String,
				   //
				   val belongsToClass: ClassEntity, 
				   val invocations: HashMap[String, InvocationRelation] = new HashMap()) extends Entity(sourceAnchor, name, uniqueName)  {

}
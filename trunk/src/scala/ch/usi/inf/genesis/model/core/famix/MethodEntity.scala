package ch.usi.inf.genesis.model.core.famix

import scala.collection.mutable.HashMap

class MethodEntity(name: String, 
				   //
				   val belongsToClass: ClassEntity) extends Entity(name)  {
  val invocations: HashMap[String, InvocationRelation] = new HashMap()

}
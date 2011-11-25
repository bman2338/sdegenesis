package ch.usi.inf.genesis.model.core.famix

import scala.collection.mutable.HashMap

class InvocationRelation(val source: Entity) extends OneToManyRelation {
  val candidates: HashMap[String, MethodEntity] = new HashMap()
}
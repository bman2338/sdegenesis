package scala.ch.usi.inf.genesis.model.core
import scala.collection.mutable.HashMap

class InvocationEntity(var candidates: HashMap[String, MethodEntity] = new HashMap())
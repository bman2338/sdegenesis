package ch.usi.inf.genesis.model.core.famix

import scala.collection.mutable.HashMap

class InvocationRelation(val invokedBy: MethodEntity,  
						 val candidates: HashMap[String, MethodEntity] = new HashMap()) extends OneToManyRelation
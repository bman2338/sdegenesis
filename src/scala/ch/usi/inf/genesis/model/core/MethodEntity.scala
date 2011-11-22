package scala.ch.usi.inf.genesis.model.core
import scala.collection.mutable.HashMap

class MethodEntity(var belongsToClass: ClassEntity, 
				   var invocations: HashMap[String, InvocationEntity] = new HashMap()) {

}
package ch.usi.inf.genesis.model.core.famix
import scala.collection.mutable.HashMap


class InheritanceDefinitionRelation(val superclasses: HashMap[String, ClassEntity] = new HashMap(), 
    								val subclass: ClassEntity) extends OneToManyRelation //For now one to one
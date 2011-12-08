package ch.usi.inf.genesis.model.core.famix

import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.core.FAMIX

case class InheritanceDefinitionRelation() extends FamixObject {
  override def internalAddProperty(propertyName: String, propertyValue: ModelObject) = {
    propertyName match {
      case FAMIX.SUPERCLASS => {
        val subclasses = properties.get(FAMIX.SUBCLASS)
        subclasses match {
          case Some(cl) =>
            cl.foreach((subclass) => {
              subclass.addProperty(FAMIX.SUBCLASS_PROP, propertyValue)
              propertyValue.addProperty(FAMIX.SUPERCLASS_PROP, subclass)
            })
          // this.properties -= FAMIX.SUPERCLASS_PROP
          case None => super.internalAddProperty(propertyName, propertyValue)
        }
      }
      case FAMIX.SUBCLASS => {
        val superclasses = properties.get(FAMIX.SUPERCLASS)
        superclasses match {
          case Some(cl) => cl.foreach((superclass) => {
            superclass.addProperty(FAMIX.SUPERCLASS_PROP, propertyValue)
            propertyValue.addProperty(FAMIX.SUBCLASS_PROP, superclass)
            //  this.properties -= FAMIX.SUBCLASS_PROP
          })
          case None => super.internalAddProperty(propertyName, propertyValue)
        }
      }
      case _ => super.internalAddProperty(propertyName, propertyValue)
    }
  }
}
package scala.ch.usi.inf.genesis.model.mutators

import ch.usi.inf.genesis.model.mutators.ModelMutator
import ch.usi.inf.genesis.model.navigation.NavigatorOption
import ch.usi.inf.genesis.model.core.famix._
import ch.usi.inf.genesis.model.core.{StringValue, FAMIX, ModelObject}
import ch.usi.inf.genesis.model.core.Metric
import ch.usi.inf.genesis.model.core.famix.PackageEntity

/**
 * @author Remo Lemma
 */


class TypeMutator extends ModelMutator {
  def visit(obj: ModelObject): NavigatorOption.NavigatorOption = {
    obj match {
      case ClassEntity() => obj.addProperty(FAMIX.ELEMENTTYPE,new StringValue(FAMIX.CLASS))
      case NamespaceEntity() => obj.addProperty(FAMIX.ELEMENTTYPE, new StringValue(FAMIX.NAMESPACE))
      case PackageEntity() =>  obj.addProperty(FAMIX.ELEMENTTYPE, new StringValue(FAMIX.PACKAGE))
      case MethodEntity() => obj.addProperty(FAMIX.ELEMENTTYPE, new StringValue(FAMIX.METHOD))
      case AttributeEntity() => obj.addProperty(FAMIX.ELEMENTTYPE,new StringValue(FAMIX.ATTRIBUTE))
      case _ =>
    }
    NavigatorOption.CONTINUE
  }
}
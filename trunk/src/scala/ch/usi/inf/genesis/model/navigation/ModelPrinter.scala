package ch.usi.inf.genesis.model.navigation

import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.navigation.NavigatorOption._


class ModelPrinter extends ModelVisitor {

  override def visit(obj: ModelObject): NavigatorOption = {

    obj.getUniqueId() match {
      case None => {
        return CONTINUE
      }
      case Some(name) => {
        println(name)
        return CONTINUE
      }
    }
    CONTINUE
  }

}
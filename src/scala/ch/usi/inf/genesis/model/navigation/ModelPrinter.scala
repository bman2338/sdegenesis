package ch.usi.inf.genesis.model.navigation

import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import ch.usi.inf.genesis.model.core.FAMIX


class ModelPrinter extends ModelVisitor {
	
  override def visit(obj: ModelObject): NavigatorOption = {

    val name = obj.getName();
    name match {
      case "" => { 
        return CONTINUE
        }
      case _ => {
    	println(name)
      return CONTINUE
    }
    }
    CONTINUE
    }

}
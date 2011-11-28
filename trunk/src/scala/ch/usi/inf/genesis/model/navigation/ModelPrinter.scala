package ch.usi.inf.genesis.model.navigation

import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import ch.usi.inf.genesis.model.core.FAMIX


class ModelPrinter extends ModelVisitor {
	
  def visit(obj: ModelObject): NavigatorOption = {

    obj.getName() match {
      case None => { 
        return CONTINUE
        }
      case Some(name) => {
    	//println(name)
      return CONTINUE
    }
    }
    CONTINUE
    }

}
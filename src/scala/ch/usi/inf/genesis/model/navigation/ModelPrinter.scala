package ch.usi.inf.genesis.model.navigation

import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import ch.usi.inf.genesis.model.core.FAMIX


class ModelPrinter extends ModelVisitor {
	
  def visit(obj: ModelObject): NavigatorOption = { 
    val list = obj.properties.get(FAMIX.NAME_PROP) match {
      case None => { 
        println("No Name Prop. Skipping subtree")
        return SKIP_SUBTREE 
        }
      case Some(list) => {
    	println(list.first)
      return CONTINUE
    }
    }
    CONTINUE
    }

}
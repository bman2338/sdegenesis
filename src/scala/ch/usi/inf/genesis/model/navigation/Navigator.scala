package ch.usi.inf.genesis.model.navigation

import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.core.Project
import ch.usi.inf.genesis.model.navigation.NavigatorOption._




abstract class Navigator {
  def walkModel(modelObject: ModelObject, visitor: ModelVisitor) : Unit = {
    walk(modelObject, visitor);
  }
  protected def walk(modelObject: ModelObject, visitor: ModelVisitor) : NavigatorOption;
	
}
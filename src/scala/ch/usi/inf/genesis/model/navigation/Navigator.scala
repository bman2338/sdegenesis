package ch.usi.inf.genesis.model.navigation

import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.core.Project
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import scala.collection.mutable.HashSet




abstract class Navigator {
  def walkModel(modelObject: ModelObject, visitor: ModelVisitor, selection: Option[HashSet[String]] = None) : Unit = {
    walk(modelObject, visitor, selection);
  }
  protected def walk(modelObject: ModelObject, visitor: ModelVisitor, selection: Option[HashSet[String]]) : NavigatorOption;
	
}
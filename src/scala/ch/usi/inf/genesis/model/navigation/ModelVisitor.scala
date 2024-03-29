package ch.usi.inf.genesis.model.navigation
import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import ch.usi.inf.genesis.model.core.famix.ClassEntity

abstract class ModelVisitor {
	def visit(obj: ModelObject) : NavigatorOption;
  def getSelection():  (ModelObject) => Boolean = (obj) => true
}
package ch.usi.inf.genesis.model.navigation

import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import scala.collection.mutable.HashSet
import ch.usi.inf.genesis.model.core.FAMIX._
import ch.usi.inf.genesis.model.core.{StringValue, BooleanValue, ModelObject, Project}


abstract class Navigator {
  /** 
   * @param modelObject the root of the navigation (it is always visited)
   * 
   */
  def walkModel(modelObject: ModelObject, visitor: ModelVisitor, selectionFunction: Option[ModelObject => Boolean] = None) : Unit = {
    walk(modelObject, visitor, selectionFunction);
  }
  protected def walk(modelObject: ModelObject, visitor: ModelVisitor, selectionFunction: Option[ModelObject => Boolean]) : NavigatorOption;


  protected def hasToIgnore(obj: ModelObject) : Boolean = {
    var ignore =  obj.getName().toString().startsWith(IGNORE_TYPE);
    var isStub = obj.getProperty(ISSTUB_PROP);
    isStub match {
      case Some(value:BooleanValue) => true
      case Some(value:StringValue) => value.value.equalsIgnoreCase("true")
      case _ => ignore
    }
  }
}
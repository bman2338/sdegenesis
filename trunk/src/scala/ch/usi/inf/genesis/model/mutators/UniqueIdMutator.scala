package ch.usi.inf.genesis.model.mutators

import ch.usi.inf.genesis.model.navigation.{NavigatorOption}
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import ch.usi.inf.genesis.model.core.{ModelType, Project, ModelObject}

/**
 * @author Patrick Zulian
 */

class UniqueIdMutator extends ModelMutator {
  val separator = ".";

  override def getSelection() = {
    (obj) => (!ModelType.isValue(obj))

  }

  def visit(obj: ModelObject): NavigatorOption.NavigatorOption = {
    obj match {
      case Project() => println("UniqueIdMutator.visit(): project has no name. set it before mutation"); return STOP;
      case _ =>
    }

    obj.properties.foreach((pair) => {
      // val key = pair._1;
      val value = pair._2;
      value.foreach((child) => {
        if (!ModelType.isValue(child)) {

          obj.getUniqueId() match {
            case Some(objId) => child.setUniqueId(objId + separator + child.getName());
            case None => println("UniqueIdMutator.visit():" + obj.getName() + " has no id");
          }
        }
      });
    });

    CONTINUE
  }
}
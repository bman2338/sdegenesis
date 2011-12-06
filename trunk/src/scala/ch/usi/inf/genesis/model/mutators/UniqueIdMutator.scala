package ch.usi.inf.genesis.model.mutators

import ch.usi.inf.genesis.model.navigation.NavigatorOption
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
      case Project() => {
        if (obj.getUniqueId() == None) {
          println("UniqueIdMutator.visit(): project has no name. set it before mutation");
          return STOP;
        }
      }
      case _ =>
    }
    var objId = "";
    obj.getUniqueId() match {
      case Some(uid) => objId = uid;
      case None =>
        println("UniqueIdMutator.visit():" + obj.getName() + " has no id");
        return SKIP_SUBTREE;
    }


    obj.properties.foreach((pair) => {
      // val key = pair._1;
      val value = pair._2;
      value.foreach((child) => {
        if (!ModelType.isValue(child)) {
          val childName = child.getName()
          if (childName.length() != 0)
            child.setUniqueId(objId + separator + child.getName());
        }
      });
    });

    CONTINUE
  }
}
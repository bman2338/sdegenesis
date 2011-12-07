package ch.usi.inf.genesis.model.mutators

import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import ch.usi.inf.genesis.model.core.famix.MethodEntity
import ch.usi.inf.genesis.model.core.FAMIX._

/**
 * @author Patrick Zulian
 */

class InvocationMutator extends ModelMutator {
  override def getSelection() = {
    (obj: ModelObject) => obj match {
      case MethodEntity() => true
      case _ => false
    }
  }

  def visit(obj: ModelObject): NavigatorOption = {

    obj.getProperties(SEND_INVOCATIONS_PROP) match {
      case Some(invocation) => {
        invocation.foreach(inv => {
          inv.getProperties(CANDIDATES) match {
            case None =>
            case Some(invokedList) =>
              invokedList.foreach(invoked => {
                if (invoked.getName() != "") {
                  obj.addProperty(INVOKINGMETHODS_PROP, invoked);
                }
              });
          }

        });


      }
      case None =>
    }
    SKIP_SUBTREE
  }
}
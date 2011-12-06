package ch.usi.inf.genesis.model.extractors

import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import ch.usi.inf.genesis.model.graph.{ModelEdge, ModelGraph}
import ch.usi.inf.genesis.model.navigation.{ModelVisitor, BreadthFirstNavigator}
import ch.usi.inf.genesis.model.core._

/**
 * Created by IntelliJ IDEA.
 * User: patrick
 * Date: 12/6/11
 * Time: 1:48 PM
 * To change this template use File | Settings | File Templates.
 */

class GraphExtractor extends ModelVisitor {
  val graph: ModelGraph = new ModelGraph();

  def getSelection(): (ModelObject => Boolean) = {
    ((element) => element match {
      case BooleanValue(_) | StringValue(_) | IntValue(_) | DoubleValue(_) => false
      case _ => true
    })
  }

  def visit(obj: ModelObject): NavigatorOption = {

    var identifier = ""

    obj.getUniqueId match {
      case None => return SKIP_SUBTREE
      case Some(id) => identifier = id
    }

    graph.addNode(identifier, obj)

    obj.properties foreach ((pair) => {
      val key = pair._1
      val value = pair._2
      value foreach ((element) => {
        element match {
          case BooleanValue(_) | StringValue(_) | IntValue(_) | DoubleValue(_) =>
          case _ => {
            element.getUniqueId match {
              case None =>
              case Some(elementId) => graph.addEdge(key, new ModelEdge(identifier, elementId))
            }
          }
        }
      })
    })
    CONTINUE
  }

  def extractGraph(model: ModelObject): ModelGraph = {
    val navigator = new BreadthFirstNavigator
    navigator.walkModel(model, this, Some(this.getSelection()))
    this.graph
  }
}
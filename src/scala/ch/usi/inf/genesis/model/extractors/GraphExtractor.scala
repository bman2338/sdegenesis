package ch.usi.inf.genesis.model.extractors

import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import ch.usi.inf.genesis.model.graph.{ModelEdge, ModelGraph}
import ch.usi.inf.genesis.model.navigation.{ModelVisitor, BreadthFirstNavigator}
import ch.usi.inf.genesis.model.core._

class GraphExtractor extends ModelVisitor {
  val graph: ModelGraph = new ModelGraph();

  def getSelection(): (ModelObject => Boolean) = {
    ((element) => {
      if (ModelType.isValue(element))
        false
      true
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
        if (!ModelType.isValue(element)) {
          element.getUniqueId match {
              case None =>
              case Some(elementId) => graph.addEdge(key, new ModelEdge(identifier, elementId))
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
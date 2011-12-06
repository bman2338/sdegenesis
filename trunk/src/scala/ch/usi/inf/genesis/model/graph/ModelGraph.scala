package ch.usi.inf.genesis.model.graph

import ch.usi.inf.genesis.model.core.ModelObject
import collection.mutable.{ListBuffer, HashMap}

/**
 * Created by IntelliJ IDEA.
 * User: patrick
 * Date: 12/6/11
 * Time: 1:19 PM
 * To change this template use File | Settings | File Templates.
 */

class ModelGraph() {
  val nodes: HashMap[String, ModelObject] = new HashMap()
  val edges: HashMap[String, ListBuffer[ModelEdge]] = new HashMap()

  def getNode(id: String): Option[ModelObject] = {
    nodes.get(id);
  }

  def addNode(id: String, node: ModelObject): Option[ModelObject] = {
    nodes.put(id, node);
  }

  def getEdgeList(relationName: String): Option[ListBuffer[ModelEdge]] = {
    edges.get(relationName);
  }

  def addEdge(relationName: String, edge: ModelEdge) = {
    getEdgeList(relationName) match {
      case Some(list) => list += edge;
      case None => val list = ListBuffer[ModelEdge](); list += edge; edges.put(relationName, list);
    }
  }

  override def toString = {
    var result: StringBuffer = new StringBuffer
    edges foreach ((pair) => {
      result.append(pair._1 + " => [")
      pair._2 foreach ((edge) =>
        result.append(edge.toString + ",")
        )
      result.deleteCharAt(result.length() - 1)
      result.append("]\n")
    })
    result.toString
  }
}
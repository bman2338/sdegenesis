package ch.usi.inf.genesis.model.graph

import ch.usi.inf.genesis.model.core.ModelObject
import collection.mutable.{ListBuffer, HashMap}
import scala.ch.usi.inf.genesis.model.graph.AdjacencyList

/**
 * @author Patrick Zulian
 */

class ModelGraph() {
  val nodes: HashMap[String, ModelObject] = new HashMap()
  val edges: HashMap[String, HashMap[String,AdjacencyList]] = new HashMap()

  def getNode(id: String): Option[ModelObject] = {
    nodes.get(id);
  }

  def addNode(id: String, node: ModelObject): Option[ModelObject] = {
    nodes.put(id, node);
  }

  def getRelationMap(relationName: String): HashMap[String, AdjacencyList] = {
    edges.get(relationName) match {
      case Some(map) => map
      case None =>
        val map = new HashMap[String, AdjacencyList]
        edges += relationName -> map
        map
    }
  }

  def addEdge(relationName: String, edge: ModelEdge) = {
    var map = getRelationMap(relationName)
    map.get(edge.from) match {
      case Some(adj) =>
        adj.addAdjacent(edge.to)
      case None =>
        val adj = new AdjacencyList(relationName,edge.from)
        adj.addAdjacent(edge.to)
        map += edge.from -> adj
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
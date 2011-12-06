package scala.ch.usi.inf.genesis.database

import ch.usi.inf.genesis.model.graph.ModelGraph
import com.mongodb.casbah.commons.{MongoDBList, MongoDBObject}
import ch.usi.inf.genesis.model.core._
import com.mongodb.casbah.{MongoDB, MongoConnection}

/**
 * @author Remo Lemma
 */


class MongoDB (val host:String, val port:Int, val dbName: String) extends Database {

  def convertToDBNode (graph:ModelGraph, node:ModelObject) : MongoDBObject = {
    val dbNode = MongoDBObject.newBuilder
    node.properties foreach ((pair) => {
      val key = pair._1
      val values = pair._2
      val listBuilder = MongoDBList.newBuilder
      if (ModelType.isValue(values.head)) {
        values foreach ((element) => {
          element match {
            case StringValue(value) => listBuilder += value
            case DoubleValue(value) => listBuilder += value
            case IntValue(value) => listBuilder += value
            case BooleanValue(value) => listBuilder += value
          }
        })
        dbNode += key -> listBuilder.result
      }
    })
    dbNode.result()
  }

  def save (graph:ModelGraph) : Boolean = {

    val connection = MongoConnection(host,port)
    val db: MongoDB = connection(dbName)

    val nodesList = MongoDBList.newBuilder
    val edgesList = MongoDBList.newBuilder

    graph.nodes foreach ((pair) => {
      val node = pair._2
      val dbNode = convertToDBNode(graph,node)
      nodesList += dbNode
    })
    graph.edges foreach ((pair) => {
      val relationList = MongoDBList.newBuilder
      val relationKey = pair._1
      val relations = pair._2
      relations foreach ((relation) => {
        var relationNode = MongoDBObject("from" -> convertToDBNode (graph,relation.from), "to" -> convertToDBNode (graph,relation.to))
        relationList += relationNode
      })
      edgesList += relationKey -> relationList
    })
    val graphDBObj = MongoDBObject("nodes" -> nodesList, "edges" -> edgesList)
    db += graphDBObj
  }

}
package scala.ch.usi.inf.genesis.database

import ch.usi.inf.genesis.model.graph.ModelGraph
import ch.usi.inf.genesis.model.core._

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoDB
import scala.ch.usi.inf.genesis.model.core.Metric

/**
 * @author Remo Lemma
 */


class MongoDBWrapper (val host:String, val port:Int, val dbName: String) extends Database {

  def convertToDBNode (node:ModelObject) : MongoDBObject = {
    val dbNode = MongoDBObject.newBuilder

    node.getUniqueId() match {
      case Some(id) => dbNode += "uniqueId" -> node.getUniqueId()
      case None =>
    }

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
            case Metric() => listBuilder += convertToDBNode (element)
            case _ =>
          }
        })
        dbNode += key -> listBuilder.result
      }
    })
    dbNode.result()
  }

  def save (graph:ModelGraph,identifier:String) : Boolean = {

    var db: MongoCollection = MongoConnection(host,port)(dbName)(identifier)

    val nodesList = MongoDBList.newBuilder
    val edgesList = MongoDBList.newBuilder

    graph.nodes foreach ((pair) => {
      val node = pair._2
      val dbNode = convertToDBNode(node)
      nodesList += dbNode
    })
    graph.edges foreach ((pair) => {
      val relationList = MongoDBList.newBuilder
      val relationKey = pair._1
      val relations = pair._2
      relations foreach ((relation) => {
        var relationNode = MongoDBObject("from" -> relation.from, "to" -> relation.to)
        relationList += relationNode
      })
      edgesList += relationKey -> relationList.result
    })
    val graphDBObj = MongoDBObject("nodes" -> nodesList.result, "edges" -> edgesList.result)
    db += graphDBObj
    false
  }

}
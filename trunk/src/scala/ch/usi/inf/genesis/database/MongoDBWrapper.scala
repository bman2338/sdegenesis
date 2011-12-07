package scala.ch.usi.inf.genesis.database

import ch.usi.inf.genesis.model.graph.ModelGraph
import ch.usi.inf.genesis.model.core._

import com.mongodb.casbah.Imports._
import com.mongodb.casbah.MongoDB
import scala.ch.usi.inf.genesis.model.core.Metric
import collection.mutable.{HashMap, ListBuffer}

/**
 * @author Remo Lemma
 */


class MongoDBWrapper(val host: String, val port: Int, val dbName: String) extends Database {

  def convertToDBNode(node: ModelObject): MongoDBObject = {
    val dbNode = MongoDBObject.newBuilder

    node.getUniqueId() match {
      case Some(id) => dbNode += "uniqueId" -> nodeAsHashcode(id)
      case None =>
    }

    val propsBuilder = MongoDBList.newBuilder
    val metricsBuilder = MongoDBList.newBuilder
    var propsAdded = false
    var metricsAdded = false
    node.properties foreach ((pair) => {
      val key = pair._1
      val values = pair._2
      val listBuilder = MongoDBList.newBuilder
      if (ModelType.isMetric(values.head)) {
        metricsAdded = true
        values foreach ((element) => {
          metricsBuilder += convertToDBNode(element)
        })
      }
      else {
        propsAdded = true
        values foreach ((element) => {
          element match {
            case StringValue(value) =>
              propsBuilder += MongoDBObject(key -> value)
            case DoubleValue(value) =>
              propsBuilder += MongoDBObject(key -> value)
            case IntValue(value) =>
              propsBuilder += MongoDBObject(key -> value)
            case BooleanValue(value) =>
              propsBuilder += MongoDBObject(key -> value)
            case _ =>
          }
        })
      }
    })

    if (propsAdded)
      dbNode += "properties" -> propsBuilder.result

    if (metricsAdded)
      dbNode += "metrics" -> metricsBuilder.result()
    dbNode.result()
  }

  def nodeAsHashcode (obj:String) = {
    obj.hashCode
  }

  def save(graph: ModelGraph, projectName: String, revision: Int): Unit = {

    var identifier = projectName + "_rev" + revision
    var db: MongoCollection = MongoConnection(host, port)(dbName)(identifier + "_nodes")

    val nodesList = MongoDBList.newBuilder
    val edgesList = MongoDBObject.newBuilder

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
        var key = relation._1
        var adj = relation._2
        val tosRelations = MongoDBList.newBuilder
        adj.targets foreach ((target) => {
          tosRelations += nodeAsHashcode(target)
        })
        var relationNode = MongoDBObject("from" -> nodeAsHashcode(key), "to" -> tosRelations.result)
        relationList += relationNode
      })
      edgesList += relationKey -> relationList.result
    })
    val graphDBObj = MongoDBObject("project" -> projectName, "revision" -> revision, "nodes" -> nodesList.result)

    db += graphDBObj

    db = MongoConnection(host, port)(dbName)(identifier + "_edges")
    val edgesDBObj = MongoDBObject("project" -> projectName, "revision" -> revision, "edges" -> edgesList.result)
    db += edgesDBObj
  }

}
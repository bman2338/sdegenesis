package scala.ch.usi.inf.genesis.database

import ch.usi.inf.genesis.model.graph.ModelGraph
import ch.usi.inf.genesis.model.core._

import com.mongodb.casbah.Imports._
import com.mongodb.DBCollection
import com.mongodb.casbah.commons.MongoDBObject

/**
 * @author Remo Lemma
 */


class MongoDBWrapper(val host: String, val port: Int, val dbName: String) extends Database {
  def PROJECT = "project";

  def PROPERTIES = "properties";

  def METRICS = "metrics";

  def FROM = "from";

  def TO = "to";

  def UNIQUEID = "uniqueId";

  def EDGES = "edges";

  def NODES = "nodes";

  def REVISION = "revision"

  def PROJECT_NAME = "name";

  def REVISIONS = "revisions";

  def PROJECTS = "projects"


  def updateRevisionRegistry(projectName: String, revision: Int) {

    val collectionName = PROJECTS;
    val registry = MongoConnection(host, port)(dbName)(collectionName);
    var proj = registry.findOne(MongoDBObject(PROJECT_NAME -> projectName));
    if (proj.isEmpty) {
      registry += MongoDBObject(PROJECT_NAME -> projectName, REVISIONS -> MongoDBList(revision));
    } else {
      val query = MongoDBObject(PROJECT_NAME -> projectName);
      val op: MongoDBObject = $push((REVISIONS, revision));
      val writeRes = registry.update(query, op);
      println(writeRes);
    }


  }


  def convertToDBNode(node: ModelObject): MongoDBObject = {
    val dbNode = MongoDBObject.newBuilder

    node.getUniqueId() match {
      case Some(id) => dbNode += UNIQUEID -> nodeAsHashcode(id)
      case None =>
    }

    val propsBuilder = MongoDBObject.newBuilder
    var metricsBuilder: MongoDBObject = null
    var propsAdded = false
    var metricsAdded = false
    node.properties foreach ((pair) => {
      val key = pair._1
      val values = pair._2
      if (ModelType.isMetric(values.head)) {
        metricsAdded = true
        values foreach ((element) => {
          metricsBuilder = convertToDBNode(element)
        })
      }
      else {
        propsAdded = true
        values foreach ((element) => {
          element match {
            case StringValue(value) =>
              propsBuilder += key -> value
            case DoubleValue(value) =>
              propsBuilder += key -> value
            case IntValue(value) =>
              propsBuilder += key -> value
            case BooleanValue(value) =>
              propsBuilder += key -> value
            case _ =>
          }
        })
      }
    })
    if (ModelType.isMetric(node)) {
      return propsBuilder.result()
    }

    if (propsAdded)
      dbNode += PROPERTIES -> propsBuilder.result

    if (metricsAdded)
      dbNode += METRICS -> metricsBuilder.result()
    dbNode.result()
  }

  def nodeAsHashcode(obj: String) = {
    obj.hashCode
  }

  def save(graph: ModelGraph, projectName: String, revision: Int) {

    updateRevisionRegistry(projectName, revision);

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
        var relationNode = MongoDBObject(FROM -> nodeAsHashcode(key), TO -> tosRelations.result)
        relationList += relationNode
      })
      edgesList += relationKey -> relationList.result
    })
    val graphDBObj = MongoDBObject(PROJECT -> projectName, REVISION -> revision, NODES -> nodesList.result)

    db += graphDBObj

    db = MongoConnection(host, port)(dbName)(identifier + "_edges")
    val edgesDBObj = MongoDBObject(PROJECT -> projectName, REVISION -> revision, EDGES -> edgesList.result)
    db += edgesDBObj
  }

}
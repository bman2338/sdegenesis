package ch.usi.inf.genesis.model.graph

import collection.mutable.HashMap

class ModelEdge(val from: String, val to: String) {
  val values: HashMap[String, String] = new HashMap[String, String]()

  override def toString = "{\"" + from + "\" : \"" + to + "\"}"
}
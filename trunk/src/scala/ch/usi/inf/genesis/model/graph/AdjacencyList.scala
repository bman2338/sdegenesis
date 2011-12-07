package scala.ch.usi.inf.genesis.model.graph

import collection.mutable.ListBuffer

/**
 * @author Remo Lemma
 */


class AdjacencyList(val relation: String,val from:String) {

  var targets = new ListBuffer[String]

  def addAdjacent (to:String) : Unit = {
    targets += to
  }

}
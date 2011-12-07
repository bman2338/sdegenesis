package scala.ch.usi.inf.genesis.database

import ch.usi.inf.genesis.model.graph.ModelGraph

/**
 * @author Remo Lemma
 */


trait Database {
  def save (graph: ModelGraph,projectName:String, revision: Int) : Boolean
}
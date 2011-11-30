package ch.usi.inf.genesis.model.extractors

class StringAnalysis(val str: String) extends Analysis {

  def toJSON(): String = { str }
  override def toString(): String = { toJSON() }
 }
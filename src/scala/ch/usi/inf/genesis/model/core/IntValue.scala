package ch.usi.inf.genesis.model.core

case class IntValue(val value: Int) extends Value {
  override def toString = "" + value
}
package ch.usi.inf.genesis.model.core

case class IntValue(var value: Int) extends Value {
  override def toString = "" + value
}
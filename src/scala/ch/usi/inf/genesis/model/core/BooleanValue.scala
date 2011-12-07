package ch.usi.inf.genesis.model.core

case class BooleanValue(var value: Boolean) extends Value {
  override def toString = "" + value
}
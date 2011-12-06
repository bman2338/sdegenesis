package ch.usi.inf.genesis.model.core

case class BooleanValue(val value: Boolean) extends Value {
  override def toString = "" + value
}
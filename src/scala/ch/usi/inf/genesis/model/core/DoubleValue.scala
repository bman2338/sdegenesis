package ch.usi.inf.genesis.model.core

case class DoubleValue(val value: Double) extends Value {
  override def toString = "" + value
}
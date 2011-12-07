package ch.usi.inf.genesis.model.core

case class DoubleValue(var value: Double) extends Value {
  override def toString = "" + value
}
package ch.usi.inf.genesis.model.core

case class StringValue(val value: String) extends Value {
  override def toString = value
}
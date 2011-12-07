package ch.usi.inf.genesis.model.core

case class StringValue(var value: String) extends Value {
  override def toString = value
}
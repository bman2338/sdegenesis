package ch.usi.inf.genesis.model.core.famix


case class LineEntity() extends Entity {

}

object LineEntityProperty extends Enumeration {
  type LineEntityProperty = String

  val NUMBER = "number"
  val AUTHOR = "author"
  val REVISION = "revision"
  val DATE = "date"

}
package ch.usi.inf.genesis.model.core.famix

case class DeveloperEntity() extends Entity{

}


object DeveloperEntityProperty extends Enumeration {
  type LineEntityProperty = String

  val NAME = "name"
  val OWNERSHIP = "ownership"

}
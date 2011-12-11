package ch.usi.inf.genesis.model.core.famix


case class FileEntity() extends Entity{

}


object FileEntityProperty extends Enumeration {
  type FileEntityProperty = String

  val NAME = "name"
  val LINES = "lines"
}
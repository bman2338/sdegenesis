package ch.usi.inf.genesis.model.core.famix

case class RevisionEntity() extends Entity{

}

object RevisionEntityProperty extends Enumeration{
  type RevisionEntityProperty = String

  val AUTHOR = "author"
  val DATE = "date"
  val NUMBER = "number"
  val ADDED_FILES = "addedFiles"
  val MODIFIED_FILES = "modifiedFiles"
  val DELETED_FILES = "deletedFiles"
  val HAS_MSE = "hasMse"
  val PROJECT = "project"
//  val LOGS = "logs"
}
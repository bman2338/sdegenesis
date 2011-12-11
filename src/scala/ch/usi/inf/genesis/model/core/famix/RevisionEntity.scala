package ch.usi.inf.genesis.model.core.famix

case class RevisionEntity() extends Entity{

}

object RevisionEntityField extends Enumeration{
  type RevisionEntityField = String

  val NUMBER = "number"
  val ADDED_FILES = "addedFiles"
  val MODIFIED_FILES = "modifiedFiles"
  val DELETED_FILES = "deletedFiles"
  val LOGS = "logs"
}
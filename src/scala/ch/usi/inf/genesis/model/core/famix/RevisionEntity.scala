package ch.usi.inf.genesis.model.core.famix

import ch.usi.inf.genesis.model.core.IntValue

case class RevisionEntity() extends Entity{

  def getRevisionNumber() : Int ={
    getProperty(RevisionEntityProperty.NUMBER) match {
      case Some(v : IntValue) => v.value
      case _ => -1
    }
  }

}

object RevisionEntityProperty extends Enumeration{
  type RevisionEntityProperty = String

  val AUTHOR = "author"
  val DATE = "date"
  val NUMBER = "number"
  val ADDED_FILES = "addedFiles"
  val ADDED_FILES_COUNT = "addedFilesCount"
  val MODIFIED_FILES = "modifiedFiles"
  val MODIFIED_FILES_COUNT = "modifiedFilesCount"
  val DELETED_FILES = "deletedFiles"
  val DELETED_FILES_COUNT = "deletedFilesCount"
  val HAS_MSE = "hasMse"
  val PROJECT = "project"
}
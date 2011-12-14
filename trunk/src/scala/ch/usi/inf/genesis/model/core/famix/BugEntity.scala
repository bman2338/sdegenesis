package ch.usi.inf.genesis.model.core.famix

import ch.usi.inf.genesis.model.core.{StringValue, IntValue}


case class BugEntity() extends Entity{

  def getBugId(): String = {
    getProperty(BugEntityProperty.ID) match{
      case Some(id : StringValue) => id.value
      case _ => ""
    }
  }
}



object BugEntityProperty extends Enumeration{
  type BugEntityProperty = String

  val ID = "id"
  val STATUS = "status"
  val RESOLUTION = "resolution"
  val PRIORITY = "priority"
  val SEVERITY = "severity"
  val SUMMARY = "summary"
  val CREATION_DATE = "creationDate"
  val UPDATE_DATE = "updateDate"
  val PROJECT = "project"
  val COMPONENTS = "components"
  val OS = "os"
  val PLATFORM = "platform"
  val VERSIONS = "versions"
  val ASSIGNEE = "assignee"
  val REPORTER = "reporter"
  val CC = "cc"
  val VOTES = "votes"
  val WATCHES = "watches"
  val HISTORY = "history"
}


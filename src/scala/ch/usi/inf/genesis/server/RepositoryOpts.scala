package scala.ch.usi.inf.genesis.server


object RepositoryOpts extends Enumeration{
  type RepositoryOpts = String

  val PROJECT_NAME = "projectName"
  val URL = "projectRepo"
  val TYPE = "repoType"
  val FIRST_REV = "from"
  val LAST_REV = "to"
  val REV_STEP = "step"
  val USERNAME = "repoUsername"
  val PASSWORD = "repoPass"

  //TODO Define and complete those commands values.

  val LANGUAGE = ""

}
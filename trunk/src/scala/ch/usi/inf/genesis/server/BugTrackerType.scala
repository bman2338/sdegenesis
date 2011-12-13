package scala.ch.usi.inf.genesis.server


object BugTrackerType extends Enumeration {
     type BugTracker = String
     val BUGZILLA = "bugzilla"
     val JIRA = "jira"
     val NONE = "none"
     val USERNAME = "btUsername"
     val PASSWORD = "btPass"
   }
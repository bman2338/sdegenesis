package scala.ch.usi.inf.genesis.server

object RepositoryType extends Enumeration {
     type RepositoryType = Value
     val SVN = "svn"
     val CVS = "cvs"
     val GIT = "git"
   }
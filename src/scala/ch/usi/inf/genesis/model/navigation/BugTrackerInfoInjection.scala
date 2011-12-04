package scala.ch.usi.inf.genesis.model.navigation

import ch.usi.inf.genesis.model.navigation.ModelVisitor
import ch.usi.inf.genesis.data.bugtracker.BugInfo
import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import ch.usi.inf.genesis.model.core.famix.RevisionEntity


class BugTrackerInfoInjection(bugList : List[BugInfo]) extends ModelVisitor{
  def visit(obj: ModelObject): NavigatorOption = {
    STOP
  }

  def selectionMethod():(ModelObject =>Boolean) = {
    (obj) => (obj.getProperty("revision") match {
      case Some(rev : RevisionEntity) => findCommentMatch() || findDateMatch()
      case _ => false
    })
  }

  private def findDateMatch():Boolean = { false }
  private def findCommentMatch(): Boolean = { false }

}
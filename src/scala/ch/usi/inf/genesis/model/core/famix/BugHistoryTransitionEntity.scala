package scala.ch.usi.inf.genesis.model.core.famix

import ch.usi.inf.genesis.model.core.famix.Entity

case class BugHistoryTransitionEntity() extends Entity {

}


object BugHistoryTransitionEntityProperty extends Enumeration{
  type BugHistoryTransitionEntityProperty = String

  val WHO = "who"
  val WHAT = "what"
  val WHEN = "when"
  val ADDED = "added"
  val REMOVED = "removed"
}
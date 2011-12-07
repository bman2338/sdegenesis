package scala.ch.usi.inf.genesis.data.bugtracker

import java.util.List
import java.net.URL
import collection.mutable.ListBuffer
import ch.usi.inf.genesis.model.core.famix.BugEntity

//trait BugTrackerCrawler(url : URL) {
//  def getBugList(): ListBuffer[BugEntity]  =
//
//  def getBugList(project: String, component: String): ListBuffer[BugEntity]
//
//  def getURL(): URL = url
//}

trait BugTrackerCrawler {
  def getBugList(): ListBuffer[BugEntity]
  def getBugList(project : String, component : String): ListBuffer[BugEntity]
  def getBugList(project : String): ListBuffer[BugEntity]
}


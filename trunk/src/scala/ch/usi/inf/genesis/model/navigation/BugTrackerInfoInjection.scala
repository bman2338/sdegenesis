package scala.ch.usi.inf.genesis.model.navigation

import ch.usi.inf.genesis.model.navigation.ModelVisitor
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import java.util.Date
import java.text.SimpleDateFormat
import collection.mutable.ListBuffer
import ch.usi.inf.genesis.model.core.famix.{BTDeveloperEntity, BugEntity, RevisionEntity}
import ch.usi.inf.genesis.model.core.{IntValue, StringValue, ModelObject}
import ch.usi.inf.genesis.data.bugtracker.{BugHistoryTransition, BugTrackerUser, BugInfo}
import scala.ch.usi.inf.genesis.model.core.famix.BugHistoryTransitionEntity

/**
 * @author Luca Ponzanelli
 * @param bugList the list of the bugs to be pushed in the model.
 *
 * Injects bug tracker information inside the original MSE meta-model.
 * Structure:
 *          Entity["bugs"] =>     BugEntity["id"]           =>  StringValue
 *                                BugEntity["status"]       =>  StringValue
 *                                BugEntity["resolution"]   =>  StringValue
 *                                BugEntity["priority"]     =>  StringValue
 *                                BugEntity["severity"]     =>  StringValue
 *                                BugEntity["summary"]      =>  StringValue
 *                                BugEntity["creationDate"] =>  StringValue
 *                                BugEntity["updateDate"]   =>  StringValue
 *                                BugEntity["project"]      =>  StringValue
 *                                BugEntity["components"]   =>  ListBuffer[StringValue]
 *                                BugEntity["os"]           =>  StringValue
 *                                BugEntity["platform"]     =>  StringValue
 *                                BugEntity["versions"]     =>  ListBuffer[StringValue]
 *                                BugEntity["assignee"]     =>  BTDeveloperEntity
 *                                BugEntity["reporter"]     =>  BTDeveloperEntity
 *                                BugEntity["cc"]           =>  ListBuffer[BTDeveloperEntity]
 *                                BugEntity["votes"]        =>  IntValue
 *                                BugEntity["watches"]      =>  IntValue
 *                                BugEntity["history"]      =>  ListBuffer[BugHistoryTransitionEntity]
 */
class BugTrackerInfoInjection(bugList : List[BugInfo]) extends ModelVisitor{

  def visit(obj: ModelObject): NavigatorOption = {
    obj.getProperty("revision") match {
      case Some(rev : RevisionEntity) =>

        rev.getProperty("comment") match{
          case Some(comment : StringValue) =>

            var involvedBugs = findCommentMatch(comment.value);
            obj.getProperty("date") match{
              case Some(date : StringValue) =>

                val formatter = new SimpleDateFormat
                involvedBugs ++= findDateMatch(formatter.parse(date.value))
                if(involvedBugs.size > 0){
                  //Create new Bug Entity
                  involvedBugs foreach{
                    (bug) => obj.addProperty("bugs", createBugEntity(bug))
                  }
                }
                CONTINUE
              case _ => CONTINUE
            }
          case _ => CONTINUE
        }
      case _ => CONTINUE
    }
  }

  def selectionMethod():(ModelObject =>Boolean) = {
    (obj) => (obj.getProperty("revision") match {
      case Some(rev : RevisionEntity) => true
      case _ => false
    })
  }

  private def createBugEntity(info : BugInfo) : BugEntity = {
    val bugEntity = new BugEntity
    bugEntity.addProperty("id", new StringValue(info.getId))
    bugEntity.addProperty("status", new StringValue(info.getStatus))
    bugEntity.addProperty("resolution", new StringValue(info.getResolution))
    bugEntity.addProperty("priority", new StringValue(info.getPriority))
    bugEntity.addProperty("severity", new StringValue(info.getSeverity))
    bugEntity.addProperty("summary", new StringValue(info.getSummary))
    bugEntity.addProperty("creationDate", new StringValue(info.getCreationDate.toString))
    bugEntity.addProperty("updateDate", new StringValue(info.getUpdateDate.toString))
    bugEntity.addProperty("project", new StringValue(info.getProject.toString))

    val componentsIt = info.getComponent.iterator()
    while(componentsIt.hasNext)
       bugEntity.addProperty("project", new StringValue(componentsIt.next))

    bugEntity.addProperty("os", new StringValue(info.getOperatingSys.toString))
    bugEntity.addProperty("platform", new StringValue(info.getOperatingSys.toString))

    val versionsIT = info.getVersions.iterator()
    while(versionsIT.hasNext)
       bugEntity.addProperty("versions", new StringValue(versionsIT.next))

    bugEntity.addProperty("assignee", createBTDeveloperEntity(info.getAssignee))
    bugEntity.addProperty("reporter", createBTDeveloperEntity(info.getReporter))

    val ccIt = info.getCcUsers.iterator()
    while(ccIt.hasNext)
       bugEntity.addProperty("cc", createBTDeveloperEntity(ccIt.next))

    bugEntity.addProperty("votes", new IntValue(info.getVotes))
    bugEntity.addProperty("watches", new IntValue(info.getWatches))

    val historyIt = info.getHistory.getEntries.keySet().iterator()
    while(historyIt.hasNext){
      val entity = new BugHistoryTransitionEntity
      val what = historyIt.next
      val transition = info.getHistory.getTransition(what)
      entity.addProperty("what", new StringValue(what))
      entity.addProperty("who", new StringValue(transition.getWho))
      entity.addProperty("when", new StringValue(transition.getWhen.toString))
      entity.addProperty("added", new StringValue(transition.getAdded))
      entity.addProperty("removed", new StringValue(transition.getRemoved))
      bugEntity.addProperty("history", entity)
    }
    bugEntity
  }

  private def createBTDeveloperEntity(user : BugTrackerUser) : BTDeveloperEntity = {
    val btDev = new BTDeveloperEntity
    btDev.addProperty("name", new StringValue(user.getName))
    btDev.addProperty("displayName", new StringValue(user.getDisplayName))
    btDev.addProperty("email", new StringValue(user.getEmail))
    btDev
  }

  private def findDateMatch(date : Date):ListBuffer[BugInfo] = {
    //TODO define a date match between revision information and bug information
    new ListBuffer[BugInfo]
  }
  private def findCommentMatch(comment : String): ListBuffer[BugInfo] = {
    //TODO define a match between bug and comment
    new ListBuffer[BugInfo]
  }

}
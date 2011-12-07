package ch.usi.inf.genesis.model.navigation

import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import java.util.Date
import java.text.SimpleDateFormat
import collection.mutable.ListBuffer
import ch.usi.inf.genesis.model.core.famix.{BugEntity, RevisionEntity}
import ch.usi.inf.genesis.model.core.{StringValue, ModelObject}
import ch.usi.inf.genesis.model.mutators.ModelMutator


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
class BugTrackerInfoMutator(bugList : List[BugEntity]) extends ModelMutator{

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
                  //Inject bugs in the matched revision
                  involvedBugs foreach(
                    (bug) => obj.addProperty("bugs", bug)
                  )
                }
                CONTINUE
              case _ => CONTINUE
            }
          case _ => CONTINUE
        }
      case _ => CONTINUE
    }
  }

  /**
   * @author Luca Ponzanelli
   * @return the function pointer for the selection method.
   *
   * It filters out all the ModelObjects that not have a reference to a RevisionEntity
   */
  override def getSelection() = {
    (obj) => (obj.getProperty("revision") match {
      case Some(rev : RevisionEntity) => true
      case _ => false
    })
  }

  private def findDateMatch(date : Date):ListBuffer[BugEntity] = {
    //TODO define a date match between revision information and bug information
    new ListBuffer[BugEntity]
  }
  private def findCommentMatch(comment : String): ListBuffer[BugEntity] = {
    //TODO define a match between bug and comment
    new ListBuffer[BugEntity]
  }

}
package scala.ch.usi.inf.genesis.data.bugtracker

import java.net.{URLEncoder, URL}
import collection.mutable.ListBuffer
import ch.usi.inf.genesis.model.core.famix.BugEntity
import java.io.{IOException, InputStreamReader, BufferedReader}
import ch.usi.inf.genesis.model.core.StringValue


/**
 * @param url the url to Bugzilla bug tracker
 * @throws MalformedURLException
 *
 * Example of url: https://issues.apache.org/bugzilla/
 */

class BugzillaCrawler(url : String) extends BugTrackerCrawler{
  val bugzillaURL = new URL(url);

  /**
   * @param product The id of the project's bugs to crawl
   * @param component The id of the component's bugs to crawl
   * @return The list of the bugs' id to be retrieved
   * @throws IOException In case it is impossible to establish a connection
   */
  private def retrieveBugList(product: String, component: String): ListBuffer[Int] = {
    val formattedProduct = URLEncoder.encode(product, "UTF-8")
    val formattedComponent = URLEncoder.encode(component, "UTF-8")
    val csvBugListParams = String.format("buglist.cgi?ctype=csv&query_format=advanced&order=bug_id&product=%s&component=%s", formattedProduct, formattedComponent)
    val url = bugzillaURL.toString
    val bugIdList = new ListBuffer[Int]
    val con = new URL(concatenateURLs(url, csvBugListParams)).openConnection
    val in = new BufferedReader(new InputStreamReader(con.getInputStream))
    var bugEntry = in.readLine

    while(bugEntry != null){
      val index: Int = bugEntry.indexOf(",")
      val str: String = bugEntry.substring(0, index)
      if (str.matches("[\\d]*"))
        bugIdList += Integer2int(Integer.parseInt(str))
      bugEntry = in.readLine
    }

    in.close()

    bugIdList
  }


  private def concatenateURLs(first: String, second: String): String = {
    if (first.lastIndexOf("/") != first.length - 1) return first + "/" + second
    first + second
  }


  /**
   * @return The full list of bugs for every project and every component.
   */
  override def getBugList(): ListBuffer[BugEntity] = {
    getBugList("", "")
  }

  /**
   * @param project The project's name
   * @param component The component's name
   * @return The list of the bugs concerning the project and the component indicated.
   */
  override def getBugList(project: String, component: String): ListBuffer[BugEntity] = {
    val bugList = new ListBuffer[BugEntity]
    val uriLength = 7168
    var param = ""
    var current = 0

    try {
      val bugIdList = retrieveBugList(project, component)

      while(current < bugIdList.size){
        var xmlParams = "show_bug.cgi?ctype=xml"
        do{
          param = "&id=" + bugIdList(current);
          xmlParams += param;
          current+=1
        }while(((xmlParams + param).length <= uriLength) && (current < bugIdList.size))

        val con = new URL(concatenateURLs(bugzillaURL.toString, xmlParams)).openConnection
        val bugs = BugzillaParser.parse(new InputStreamReader(con.getInputStream));

//        Retrieve Bugs' History
        bugs foreach{ (bug : BugEntity) =>
            bug.getProperty("id") match{
            case Some(sv : StringValue) =>
              val history = BugzillaParser.parseHistory(new URL(bugzillaURL.toString+"show_activity.cgi?id="+sv.value))
              history foreach(
                (transition) => bug.addProperty("history", transition)
              )
            case _ => println("NONE")
          }
        }
        bugList ++= bugs;
      }
    }
    catch {
      case e: IOException => {
        println(e.getMessage)
      }
    }

    bugList
  }
}
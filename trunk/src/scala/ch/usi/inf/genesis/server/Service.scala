package scala.ch.usi.inf.genesis.server





import java.net.ServerSocket
import collection.mutable.HashMap
import ch.usi.inf.genesis.data.repository.FamixLanguage
import ch.usi.inf.genesis.model.core.famix.RevisionEntity
import java.io.{File, BufferedReader, InputStream, InputStreamReader}
import ch.usi.inf.genesis.parser.mse.MSEParser
import ch.usi.inf.genesis.model.mutators.RevisionInfoMutator
import org.tmatesoft.svn.core.SVNException
import scala.ch.usi.inf.genesis.data.bugtracker.{BugzillaCrawler, BugTrackerCrawler}
import ch.usi.genesis.data.repository.{RepositoryCrawler, SVNCrawler, InFamixWrapper}
import ch.usi.inf.genesis.model.navigation.{ModelPrinter, BreadthFirstNavigator}
import java.util.logging.SimpleFormatter
import java.text.SimpleDateFormat
import java.util.Date


object Main {

  class ParseError extends RuntimeException


  def main(args: Array[String]) {
    new Service(6969).run();
  }


}


class Service(val port: Int) {

  def run() {
    try {
      println("Running...");
      val server = new ServerSocket(port);

      while (true) {
        val client = server.accept();
        println("Accepted connection from " + client.getInetAddress)
        val input = client.getInputStream();

        asMap(input) match {
          case None =>
          case Some(map) =>


            actors.Scheduler.execute({
              //TODO bugtracker
              var btPrjName = ""
              map.get("projectBugTrackerName") match{
                  case Some(s: String) => btPrjName = s
                  case _ =>
              }

              var bugTrackerURL = ""
              map.get("projectBugTracker") match{
                  case Some(s: String) => bugTrackerURL = s
                  case _ =>
              }
              println("BUG_TRACKER_URL: " + bugTrackerURL)

              var btCrawler : BugTrackerCrawler  = null
              map.get("bugTrackerType") match{
                case Some(typeName : String) =>
                  println("TYPE: " + typeName)
                  typeName match{
                    case BugTrackerType.BUGZILLA =>{
                      btCrawler = new BugzillaCrawler(bugTrackerURL)
                      println("Bugzilla Crawler Start...")
                      val list = btCrawler.getBugList(btPrjName)
                      println("Bugzilla Crawler Done.")
                      println("Total Bugs Grabbed: " + list.count((BugEntity) => true))
                    }
                    case BugTrackerType.JIRA =>
                    case BugTrackerType.NONE =>
                    case _ =>
                  }
                case _ =>
              }
            })

            actors.Scheduler.execute({
              //TODO run whole download from svn, mse .... etc
              val formatter = new SimpleDateFormat("yyyy-MM-dd_hh:mm:ss")
              try{

                var prjName = ""
                map.get("projectName") match{
                  case Some(s : String) => prjName = s.replaceAll("[\\s]", "_")
                  case _ =>
                }



                var repository = ""
                  map.get("projectRepo") match{
                  case Some(s: String) => repository = s.replaceAll("[\\s]", "_")
                  case _ =>
                }

                println("REPO:" + repository)
                println("PROJECT:" + prjName)

                val inFamix = new InFamixWrapper("./inFamix/MacOS", FamixLanguage.JAVA.getId)


                map.get("repoType") match{
                  case Some(repoType : String) => {
                    repoType match{
                      case RepositoryType.SVN => {
                         val crawler = new SVNCrawler(
                                    repository,
                                    prjName,
                                    "/Users/Ponzanelli/Documents/workspace/Genesis-Scala/"+prjName,
                                    "/Users/Ponzanelli/Documents/workspace/Genesis-Scala/mse",
                                    inFamix,
                                    null,
                                    ".java")
                          crawler.onSourceParsingCompleteDelegates+=onParsingCompleted
                          crawler.crawl(1, 100)
                      }
                      case RepositoryType.CVS =>
                      case RepositoryType.GIT =>
                      case _ =>
                    }

                  }

                  case _ =>
                }


              }catch{
                case e: SVNException => println(e)
                case anyEx : Exception => anyEx.printStackTrace()
              }
            });

        }
        client.close();

      }
      server.close();
    }
    catch {
      case ex: Exception => ex.printStackTrace();

    }

    println("Shutting down..")
  }

  def onParsingCompleted(revisionInfo: RevisionEntity, mseFile : File): Unit = {
	  import scala.io._

	  val source = Source.fromFile(mseFile)
    val content = source.mkString
    source.close()
	  val res = MSEParser.parse(content) match {
	    case Some(res) =>
	      val mutator =  new RevisionInfoMutator(revisionInfo)
	      mutator.mutate(res)
        (new BreadthFirstNavigator).walkModel(res,new ModelPrinter, Some(mutator.getSelection()))
	    case _ =>

	  }
	}


  def asMap(stream: InputStream): Option[HashMap[String, String]] = {
    val br: BufferedReader = new BufferedReader(new InputStreamReader(stream));
    val sb: StringBuilder = new StringBuilder();
    var line: String = "";
    val map = new HashMap[String, String]();

    while (true) {
      line = br.readLine();

      if (line == null) {
        return Some(map);
      }
      val args = line.split(">");
      if (args.length < 2) {
        println("msg: Bad format: " + line);
        return None;
      }

      //println(args.apply(0).trim() + ">" + args.apply(1).trim());
      map.put(args.apply(0).trim(), args.apply(1).trim());
    }
    Some(map);

  }


}
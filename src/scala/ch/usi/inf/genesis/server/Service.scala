package scala.ch.usi.inf.genesis.server





import java.net.ServerSocket
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
import ch.usi.inf.genesis.model.ModelGenerator
import ch.usi.inf.genesis.model.extractors.GraphExtractor
import scala.ch.usi.inf.genesis.database.MongoDBWrapper
import ch.usi.inf.genesis.model.core.{StringValue, IntValue}
import collection.mutable.{ListBuffer, HashMap}


object Main {

  class ParseError extends RuntimeException


  def main(args: Array[String]) {
    new Service(6969).run();
  }


}


class Service(val port: Int) {

  val inFamixPath = "inFamix"
  val defaultAnalysisFolder = "analysis_folder"

  def run() {
    try {
      println("Running...");
      val server = new ServerSocket(port);

      while (true) {
        val client = server.accept();
        println("Accepted connection from " + client.getInetAddress)
        val input = client.getInputStream;

        asMap(input) match {
          case None =>
          case Some(map) => {


            //Retrieve Repository-Parameters' Value
            val prjName = extractParameterValue(map, RepositoryOpts.PROJECT_NAME)
            val repository = extractParameterValue(map, RepositoryOpts.URL)
            val repoType = extractParameterValue(map, RepositoryOpts.TYPE)

            println("REPO:\t" + repository)
            println("PROJECT:\t" + prjName)
            println("TYPE:\t" + repoType)

            //Retrieve Bugtracker-Parameters' Value
            val btPrjName = extractParameterValue(map, BugTrackerOpts.PROJECT_NAME)
            val btURL = extractParameterValue(map, BugTrackerOpts.URL)
            val btType = extractParameterValue(map, BugTrackerOpts.TYPE)

            println("BUG_TRACKER_PROJECT_NAME:\t" + btPrjName)
            println("BUG_TRACKER_URL:\t" + btURL)
            println("BUG_TRACKER_TYPE:\t" + btType)


            //Create an Analysis Folder
            val analysisPath = createAnalysisFolder(prjName, inFamixPath)



            //TODO do not use actor if bug tracker is not needed (case NONE)
            actors.Scheduler.execute({
            var btCrawler : BugTrackerCrawler  = null
            btType match{
              case BugTrackerType.BUGZILLA =>{
                btCrawler = new BugzillaCrawler(btURL)
                println("Bugzilla Crawler Start...")
                val list = btCrawler.getBugList(btPrjName)
                println("Bugzilla Crawler Done.")
                println("Total Bugs Grabbed: " + list.count((BugEntity) => true))
              }
              case BugTrackerType.JIRA =>
              case BugTrackerType.NONE =>
              case _ =>
            }
            })


            actors.Scheduler.execute({
              //TODO run whole download from svn, mse .... etc
              try{

                val inFamix = new InFamixWrapper(
                  new File(analysisPath + File.separator + "/inFamix/MacOS").getCanonicalPath,
                            FamixLanguage.JAVA.getId)

                repoType match{
                  case RepositoryType.SVN => {
                     val crawler = new SVNCrawler(
                                repository,
                                prjName,
                                new File(analysisPath + File.separator + prjName).getCanonicalPath,
                                new File(analysisPath + File.separator + "mse").getCanonicalPath,
//                                "/Users/Ponzanelli/Documents/workspace/Genesis-Scala/"+prjName,
//                                "/Users/Ponzanelli/Documents/workspace/Genesis-Scala/mse",
                                inFamix,
                                null,
                                ".java")
                      crawler.onSourceParsingCompleteDelegates+=onParsingCompleted
                      crawler.crawl(11, 100)
                  }
                  case RepositoryType.CVS =>
                  case RepositoryType.GIT =>
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
      }
      server.close();
    }
    catch {
      case ex: Exception => ex.printStackTrace();

    }

    println("Shutting down..")
  }

  def onParsingCompleted(affectedRevisions: ListBuffer[RevisionEntity],
                         currentRevisionNumber : Int, projectName : String,
                         mseFile : File) {
	  import scala.io._

	  val source = Source.fromFile(mseFile)
    val mse = source.mkString
    source.close()

    val res = new ModelGenerator(projectName).generateFromString(mse)
    res match {
      case Some(node) => {
        var graph = new GraphExtractor().extractGraph(node)
        //println(graph);
        var mongo = new MongoDBWrapper("127.0.0.1", 4321, "genesis_db")
        mongo.save(graph, projectName, currentRevisionNumber)

        //new BreadthFirstNavigator().walkModel(node, new ModelPrinter());
      }
      case _ =>
    }
  }


  private def asMap(stream: InputStream): Option[HashMap[String, String]] = {
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


  private def createAnalysisFolder(projectName : String, inFamixPath : String) : String = {
    //Create Folder
    val analysisFolder = new File(defaultAnalysisFolder + File.separator + projectName)
    analysisFolder.mkdirs()

    //Copy inFamix
    Runtime.getRuntime.exec("cp -r " + inFamixPath + " " + analysisFolder.getCanonicalPath)

    //Create Mse folder
    new File(analysisFolder.getCanonicalPath + File.separator + "mse").mkdirs()

    analysisFolder.getCanonicalPath
  }

  private def extractParameterValue(map : HashMap[String, String],  key : String) : String ={
    var value = ""
    map.get(key) match{
      case Some(s : String) => value = s.replaceAll("[\\s]", "_")
      case _ =>
    }

    value
  }


}
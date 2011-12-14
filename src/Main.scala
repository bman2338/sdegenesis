import ch.usi.genesis.data.repository.{RepositoryUserAuth, SVNCrawler, InFamixWrapper}
import ch.usi.inf.genesis.data.repository.{FamixLanguage, IRepositoryCrawlerNotificationDelegate, RevisionInformation}
import ch.usi.inf.genesis.model.core.famix.{RevisionEntityProperty, ClassEntity, RevisionEntity}
import ch.usi.inf.genesis.model.core.{IntValue, StringValue, ModelObject}
import ch.usi.inf.genesis.model.extractors.GraphExtractor
import ch.usi.inf.genesis.model.ModelGenerator
import ch.usi.inf.genesis.model.mutators.{ClassLocMutator, MethodOwnershipMutator}
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import ch.usi.inf.genesis.model.navigation.{ModelVisitor, BreadthFirstNavigator}
import ch.usi.inf.genesis.parser.mse.MSEParser
import collection.mutable.ListBuffer
import io.Source
import java.io.File
import scala.ch.usi.inf.genesis.database.MongoDBWrapper
import scala.Int

class ClassLocChecker extends ModelVisitor{
  def visit(obj: ModelObject): NavigatorOption ={
      obj.getProperty("loc") match{
        case Some(v) => println(obj.getName()+"\tLOC: "+v)
        case _ =>
      }
      CONTINUE
  }
}


object  Main {

   var host = "127.0.0.1";
  var port = 8888;
  var db = "genesis_db";
private def onParsingCompleted(revision: RevisionEntity,
                                 projectName: String,
                                 projectPath: String,
                                 mseFile: File) {
    import scala.io._

    val source = Source.fromFile(mseFile)
    val mse = source.mkString
    source.close()

    val revNumber = revision.getRevisionNumber()


    val res = new ModelGenerator(projectName).generateFromString(mse)
    val locMutator = new ClassLocMutator(projectPath)
    val ownerMutator = new MethodOwnershipMutator(revision)
    res match {
      case Some(node) => {
        locMutator.mutate(node)
       // ownerMutator.mutate(node)
        val graph = new GraphExtractor().extractGraph(node)
        val mongo = new MongoDBWrapper(host, port, db);
        val revNumber = revision.getProperty(RevisionEntityProperty.NUMBER) match {
          case Some(n: IntValue) => n.value
          case _ => 0
        }
        mongo.save(graph, projectName, revNumber)
      }
      case _ =>
    }
  }


  private def onRepositoryCrawlingComplete(revisions: ListBuffer[RevisionEntity], prjName: String, prjPath: String) {
    revisions foreach((r) => println(r.properties))
    val mongo = new MongoDBWrapper(host, port, db);
    mongo.saveRepositoryHistory(revisions, prjName)
  }


  
  def main (args: Array[String]): Unit = {
    import scala.io._
    import org.tmatesoft.svn.core.SVNException

    if (args.length < 8) {
      println("Wrong number of arguments.");
      sys.exit(-1);
    }

    var projectName = args(0);
    var projectPath = args(1);
    var repository = args(2);
    var mses = args(3);
    var inFamixPath = args(4);

    object Int {
      def unapply (s:String) : Option[Int] = try {
        Some(s.toInt)
      }  catch {
        case _ => None
      }
    }

    var from = 0
    var to = 0
    var step = 0

    args(5) match {
      case Int(p) => from = p;
      case _ =>
    }

    args(6) match {
      case Int(p) => to = p;
      case _ =>
    }

    args(7) match {
      case Int(p) => step = p;
      case _ =>
    }

    var username: String = null;
    var password: String = null;

    if (args.length > 9) {
      username = args(8);
      password = args(9);
    }

    if (args.length > 10)
      host = args(10);

    if (args.length > 11) {
      args(11) match {
        case Int(p) => port = p;
        case _ =>
      }
    }
    if (args.length > 12)
      db = args(12)


    try{

      val inFamix = new InFamixWrapper(inFamixPath, FamixLanguage.JAVA.getId)

      var auth : RepositoryUserAuth = null
      if (username != null && password != null)
        auth = new RepositoryUserAuth(username,password)
      val crawler = new SVNCrawler(
      //"http://crypto-box.googlecode.com/svn/trunk/",
      //"https://sdegenesis.googlecode.com/svn/trunk/",
      repository,
      //"argouml",
      projectName,
      projectPath,
      mses,
      inFamix,
      auth,
      "\\.java")

      crawler.onSourceParsingCompleteDelegates+=onParsingCompleted
      crawler.onCrawlingCompleteDelegates += onRepositoryCrawlingComplete
      crawler.crawl(from,to, step)



    }catch{
      case e: SVNException => println(e)
      case anyEx : Exception => anyEx.printStackTrace()
    }
  }
}
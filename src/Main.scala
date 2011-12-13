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

class ClassLocChecker extends ModelVisitor{
  def visit(obj: ModelObject): NavigatorOption ={
      obj.getProperty("loc") match{
        case Some(v) => println(obj.getName()+"\tLOC: "+v)
        case _ =>
      }
      CONTINUE
  }
}


object Main {


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
        ownerMutator.mutate(node)
        val graph = new GraphExtractor().extractGraph(node)
        val mongo = new MongoDBWrapper("127.0.0.1", 4321, "genesis_db")
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
    val mongo = new MongoDBWrapper("127.0.0.1", 4321, "genesis_db")
    mongo.saveRepositoryHistory(revisions, prjName)
  }


  
  def main (args: Array[String]): Unit = {
    import scala.io._
    import org.tmatesoft.svn.core.SVNException

    try{

      val inFamix = new InFamixWrapper("./inFamix/MacOS", FamixLanguage.JAVA.getId)

      //val auth = new RepositoryUserAuth("luca.ponzanelli@gmail.com","CB7NW5gd2Bs7")
      val crawler = new SVNCrawler(
      //"http://crypto-box.googlecode.com/svn/trunk/",
      //"https://sdegenesis.googlecode.com/svn/trunk/",
      "http://argouml.tigris.org/svn/argouml/trunk/",
      //"argouml",
      "argouml",
      "/Users/Ponzanelli/Documents/workspace/Genesis-Scala/argouml",
      "/Users/Ponzanelli/Documents/workspace/Genesis-Scala/mse",
      inFamix,
      null,
      ".java")

      crawler.onSourceParsingCompleteDelegates+=onParsingCompleted
      crawler.onCrawlingCompleteDelegates += onRepositoryCrawlingComplete
      crawler.crawl(16400,17000, 100)



    }catch{
      case e: SVNException => println(e)
      case anyEx : Exception => anyEx.printStackTrace()
    }
  }
}
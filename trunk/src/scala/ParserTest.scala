import ch.usi.inf.genesis.model.ModelGenerator
import ch.usi.inf.genesis.model.extractors._
import scala.ch.usi.inf.genesis.database.MongoDBWrapper

object ParserTest {
  val dbHost = "localhost";
  val dbPort = 8888;

  class ParseError extends RuntimeException

  def main(args: Array[String]) {
    import java.io._
    import scala.io._

    case class Task(source: () => Source)
    case class Options(exps: List[Task], verbose: Boolean)

    var opts = args.toList match {
      case files@(x :: xs) => new Options(files.map(f => Task(() => Source.fromFile(f))), false)
      case Nil => new Options(List(), false)
    }
    for (a <- opts.exps) {
      a match {
        case Task(source) =>
          try {
            val in = source()
            val mse = try in.mkString finally in.close
            val res = new ModelGenerator("ProjectExample").generateFromString(mse)
            res match {
              case Some(node) => {
                var graph = new GraphExtractor().extractGraph(node)
                //println(graph);
                var mongo = new MongoDBWrapper(dbHost, dbPort, "genesis_db")
                mongo.save(graph, "ArgoUML", 12)

                //new BreadthFirstNavigator().walkModel(node, new ModelPrinter());
              }
              case None =>
            }
          }
          catch {
            case ex: IOException =>
              println(ex.getMessage)
              sys.exit(1)
          }
      }
    }
  }
}
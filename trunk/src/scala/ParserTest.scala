import ch.usi.inf.genesis.model.ModelGenerator
import ch.usi.inf.genesis.model.extractors._

object ParserTest {

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
            val res = new ModelGenerator("ProjectExample").generateFromString(mse) match {
              case Some(node) => {
                println(new GraphExtractor().extractGraph(node))
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
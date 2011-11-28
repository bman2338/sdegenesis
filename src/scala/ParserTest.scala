import ch.usi.inf.genesis.parser.mse.MSEParser
import ch.usi.inf.genesis.model.navigation.BreadthFirstNavigator
import ch.usi.inf.genesis.model.navigation.ModelPrinter
import ch.usi.inf.genesis.model.core.FAMIX._
import scala.collection.mutable.HashSet
import ch.usi.inf.genesis.model.navigation.DepthFirstNavigator

object ParserTest {

	class ParseError extends RuntimeException

	def main(args: Array[String]) {
		import java.io._
		import scala.io._

		case class Task(source: () => Source)
		case class Options(exps: List[Task], verbose: Boolean)	

		var opts = args.toList match {
			case files @ (x::xs) => new Options(files.map(f => Task(() => Source.fromFile(f))) , false)
			case Nil => new Options(List(),false)
		}
		for (a <- opts.exps) {
			a match {
				case Task(source) =>
				try {
					val in = source()
					val a = try in.mkString finally in.close
					val res = MSEParser.parse(a) match {
					  //Trying navigator with visitor interface
					  
					  case Some(res) =>  {
					  val selection = new HashSet[String]();
					  selection.add(CLASSES_PROP); //visit only class properties
					  //new BreadthFirstNavigator().walkModel(res, new ModelPrinter(), Some(selection))
					    new DepthFirstNavigator().walkModel(res, new ModelPrinter(), Some(selection))
					}
					  case None =>
					}
					//println(res.toString)
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
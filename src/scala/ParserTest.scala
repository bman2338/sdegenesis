import ch.usi.inf.genesis.parser.mse.MSEParser
import ch.usi.inf.genesis.model.navigation.BreadthFirstNavigator
import ch.usi.inf.genesis.model.navigation.ModelPrinter
import ch.usi.inf.genesis.model.core.FAMIX._
import scala.collection.mutable.HashSet
import ch.usi.inf.genesis.model.navigation.DepthFirstNavigator
import ch.usi.inf.genesis.model.extractors._
import ch.usi.inf.genesis.model.navigation.ModelSaver
import ch.usi.inf.genesis.model.core.famix.ClassEntity
import ch.usi.inf.genesis.model.core.famix.MethodEntity
import ch.usi.inf.genesis.model.core.FAMIX
import ch.usi.inf.genesis.model.core.StringValue

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
					    
					 res.addProperty(FAMIX.NAME_PROP,new StringValue("ArgoUML r666"));
//					 new BreadthFirstNavigator().walkModel(res, new ModelPrinter(), 
//				     Some((obj) => {
//					   obj match {
//					     case ClassEntity() => true
//					     case _ => false
//					   }}))
					  // println(new ClassMethodsExtractor().extract(res))
					  //println(new InheritanceExtractor().extract(res));
					 val saver = new  ModelSaver();
					 new BreadthFirstNavigator().walkModel(res, saver, Some(saver.getSelection()));
					 println(InvocationExtractorFactory.getSimpleInvocationExtractor().extract(res))
					 
					  
					  
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
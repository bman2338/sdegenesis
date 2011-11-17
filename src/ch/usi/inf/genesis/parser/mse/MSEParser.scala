import scala.util.parsing.combinator.lexical.StdLexical
import scala.util.parsing.combinator.syntactical.StdTokenParsers
import scala.util.parsing.combinator._
import scala.util.matching.Regex

object MSEParser {

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
					val res = Parser.parse(a)
					println(res)
				}
				catch {
					case ex: IOException => 
					println(ex.getMessage)
					sys.exit(1)
				}
			}
		}
	}


	object Parser extends RegexParsers {
	  override def skipWhitespace = false
	  
	  def root : Parser[Any] = (document?)
	  def document = open ~ (elementNode*) ~ close
	  def elementNode : Parser[Any] = open ~ elementName ~ (serial?) ~ (attributeNode*) ~ close
	  def serial = open ~ id ~ natural ~> close
	  def attributeNode = open ~ simpleName ~ (valueNode*) ~ close
	  def valueNode = primitive | reference | elementNode
	  def primitive = string | number | boolean
	  def boolean = booleanTrue | booleanFalse
	  def reference = integerReference | nameReference
	  def integerReference = open ~ ref ~ natural ~ close
	  def nameReference = open ~ ref ~ elementName ~ close
	  
	   def digit = "[0-9]".r
	   def letter = "[a-zA-Z_]".r
	   def comment = "\"[^\"]*\"".r
	   def space = "[\\s]".r 
	   def open = (space*) ~> "(" <~ (space*)
	   def close = (space*) ~> ")" <~ (space*)
	   def id = "id:" <~ (space*)
	   def ref = "ref:" <~ (space*)
	   def booleanTrue = "true"
	   def booleanFalse = "false"
	   
	   def elementName = letter ~ ((letter | digit)*) ~ ("." ~ letter ~ ((letter|digit)*)) <~ (space*) ^^ 
	   	{ case l ~ xs ~ ("." ~ l1 ~ ys) => l + xs.foldLeft("")((element,rest) => element+rest) + "." + l1 + ys.foldLeft("")((element,rest) => element+rest) }
	   def simpleName = letter ~ ((letter|digit)*) <~ (space*) ^^
		{ case l ~ xs => l + xs.foldLeft("")((element,rest) => element+rest) }
	   def natural = digit+
	   def suffix = "." ~ natural
	   def exponent = ("e"|"E") ~ (("-"|"+")?) ~ natural
	   def number = ("-"?) ~ natural ~ (suffix?) ~ (exponent?)
	   def string = "'[^']*'+".r

	   def parse(a: String) : String = {
		  val r = parseAll(root,a)
		  r.toString
	  }
	}
}
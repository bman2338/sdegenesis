import scala.util.parsing.combinator.lexical.StdLexical
import scala.util.parsing.combinator.syntactical.StdTokenParsers
import scala.util.parsing.combinator._
import scala.util.matching.Regex
import scala.collection.mutable.ListBuffer

abstract class Entity;

abstract class Value extends Entity;

case class BooleanValue(val boolean:Boolean) extends Value {
  override def toString = boolean.toString
}

case class DoubleValue (val number:Double) extends Value {
  override def toString = number.toString
}

case class IntValue (val number:Int) extends Value {
  override def toString = number.toString
}

case class StringValue (val string:String) extends Value {
  override def toString = string
}

abstract class Reference extends Entity;

case class StringReference(val id:String) extends Reference {
  override def toString = id
}

case class IntReference(val id:Int) extends Reference {
  override def toString = id.toString
}

class Node extends Entity {
    val children:ListBuffer[Entity] = new ListBuffer[Entity]
    def addChild (entity:Entity) = children += entity 	
}

class Attribute(val name:String) extends Node {
  override def toString = name
  def unapply (otherName:String) : Boolean = name.equals(otherName) 
}

case class Element(innerName:String, val id:Option[Int]) extends Attribute(innerName) {
  
}

/*case class ReferenceAttribute (val entity:Entity) extends Attribute() {
}*/

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
	  
	  def root : Parser[Option[Node]] = (document?)
	  def document = open ~> (elementNode*) <~ close ^^ { 
	    case nodes =>
	    	val node = new Node
	    	nodes.foreach((innerNode) => node.addChild(innerNode))
	    	node
	  }
	  def elementNode : Parser[Entity] = open ~> elementName ~ (serial?) ~ (attributeNode*) <~ close ^^ { 
	    case name ~ id ~ nodes => {
	      val element = new Element(name,id)
	      nodes.foreach((node) => element.addChild(node))
	      element
	    }
	  }
	  def serial = open ~> id ~> natural <~ close ^^ { case naturalValue => naturalValue.toInt }
	  def attributeNode = open ~> simpleName ~ (valueNode*) <~ close ^^ {
	    case name ~ valueNodes => {
	      val attribute = new Attribute(name)
	      valueNodes.foreach ((valueNode) => attribute.addChild(valueNode))
	      attribute
	    } 
	  }
	  def valueNode = primitive | reference | elementNode
	  def primitive = (space*) ~> (string | number | boolean) <~ (space*)
	  def boolean = (booleanTrue | booleanFalse) ^^ { case booleanValue => new BooleanValue(booleanValue.toBoolean) }
	  def reference = integerReference | nameReference
	  def integerReference = open ~> ref ~> natural <~ close ^^ { case integerNumber => new IntReference(integerNumber.toInt) }
	  def nameReference = open ~> ref ~> elementName <~ close ^^ { case nameStr => new StringReference(nameStr) }
	  
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
	   
	     
	   def nameIdentifier = ((letter|digit)*) ^^ { case xs => xs.foldLeft("")((element,rest) => element+rest) }
	   def elementName = letter ~ nameIdentifier ~ ("." ~ letter ~ nameIdentifier) <~ (space*) ^^ {
	     case l ~ nid ~ ("." ~ l1 ~ nid1) => l + nid + "." + l1 + nid1
	   }
	   def simpleName = letter ~ nameIdentifier <~ (space*) ^^ { case l ~ nid => l + nid }
	   def natural = (digit+) ^^ { case xs => xs.foldLeft("")((v,rest) => v+rest) }
	   def suffix = "." ~ natural ^^ { case dot ~ naturalNumber => dot + naturalNumber }
	   def exponent = ("e"|"E") ~ (("-"|"+")?) ~ natural ^^ {
	     case exp ~ sign ~ exponentNumber =>
	       var retNumber = exponentNumber
	       sign match {
	         case None =>
	         case _ => retNumber = sign + retNumber
	       }
	       exp + retNumber
	   }
	   def number = ("-"?) ~ natural ~ (suffix?) ~ (exponent?) ^^ {
	     case minus ~ numberElement ~ suffixOp ~ exponentNumber =>
	       var retNumber = numberElement;
	       var isDouble = false
	       suffixOp match {
	         case None =>
	         case _ =>
	           isDouble = true
	           retNumber += suffixOp
	       }
	       exponentNumber match {
	         case None =>
	         case _ =>
	           isDouble = true
	           retNumber += exponentNumber
	       }
	       minus match {
	         case None =>
	         case _ => retNumber = "-" + retNumber
	       }
	       if (isDouble)
	         new DoubleValue(retNumber.toDouble)
	       else
	         new IntValue(retNumber.toInt)
	   }
	   def string = "'[^']*'".r ^^ { str => new StringValue(str) }

	   def parse(a: String) : String = {
		  val r = parseAll(root,a)
		  r.toString
	  }
	}
}
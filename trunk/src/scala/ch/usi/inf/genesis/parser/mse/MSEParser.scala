	package ch.usi.inf.genesis.parser.mse

import scala.util.parsing.combinator.RegexParsers
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.HashMap
import scala.collection.Map

import ch.usi.inf.genesis.model.core._

/**
 * Singleton object that is used to parse MSE files
 * @author Remo Lemma & Patrick Zulian
 */
object MSEParser extends RegexParsers {

	abstract class Entity {
		var modelObject : ModelObject = null
		def resolve (pool: Map[Int,Entity]) : Option[ModelObject];
	}
	
	case class Value private () extends Entity {
	   def this (obj:ModelObject) = {
		   this()
		   this.modelObject = obj;
	   }
	   def resolve (pool: Map[Int,Entity]) = Some(modelObject)
	}

	abstract class Reference extends Entity;

	class StringReference(val id:String) extends Reference {
		override def toString = id
 	    def resolve (pool: Map[Int,Entity]) = {
		    println("String Reference not handled")
			None // We do not handle this right now
		}
	}

	class IntReference(val id:Int) extends Reference {
		override def toString = id.toString
		def resolve (pool: Map[Int,Entity]) : Option[ModelObject] = {
		  if (this.modelObject != null)
		    return Some(this.modelObject)
		  val poolItem = pool.get(id)
		  poolItem match {
		    case Some(obj:Entity) => {
		    	if (obj.modelObject == null)
		    		obj.resolve(pool)
		    	this.modelObject = obj.modelObject
		    	Some(obj.modelObject)
		    }
		    case _ => None
		  }
		}
	}

	class Node extends Entity {
		val children = new HashMap[String,Entity]

		def unapply(e:Entity) : Boolean = this == e
		def resolve (pool : Map[Int,Entity]) = {
		  	if (this.modelObject == null) {
		  		children.foreach((pair) => { 
				 	pair._2.resolve(pool)
				})
		  	}
		  	Some(this.modelObject)
		}
	}

	class Attribute(val name:String) extends Entity {
		override def toString = name
		val children = new ListBuffer[Entity]
		override def resolve (pool:Map[Int,Entity]) = {
			if (this.modelObject == null) {
		  		children.foreach((value) => { 
				 	value.resolve(pool)
				})
		  	}
		  	Some(this.modelObject)  
		}
	}

	class Element(val name:String, val modelName:String, val id:Option[Int]) extends Entity {
  		val children = new HashMap[String,Entity]
		override def resolve (pool:Map[Int,Entity]) = {
			children.foreach((pair) => { pair._2.resolve(pool) })
			name match {
			  case FAMIX.CLASS => {
			    //new ClassEntity()
			      null
			  }
			  case FAMIX.INVOCATION => {
			    // val invocation = new Invocation()
			    // val source = children.get(source); source match { case Some(x) => invocation.setSender(..) || source.addInvocation(invocation) }
			  }
			}
			null
		}
	}
  
	override def skipWhitespace = false
	val pool = new HashMap[Int,Entity];
	
	def root : Parser[Option[Node]] = (document?)
	def document = open ~> (elementNode*) <~ close ^^ { 
		case nodes =>
		val node = new Node
		nodes.foreach((innerNode) => node.children += innerNode.name -> innerNode) 
		  //node.addChild(innerNode))
		node
	}
	def elementNode : Parser[Element] = open ~> elementName ~ (serial?) ~ (attributeNode*) <~ close ^^ { 
		case (model,name) ~ id ~ nodes => {
			val element = new Element(name,model,id)			
			nodes.foreach((node) => element.children += node.name -> node)
			id match {
		  		case Some(integerId:Int) =>
		  		  pool += integerId -> element
		  		case _ =>
			}
			element
		}
	}
	def serial = open ~> id ~> natural <~ close ^^ { case naturalValue => naturalValue.toInt }
	def attributeNode = open ~> simpleName ~ (valueNode*) <~ close ^^ {
		case name ~ valueNodes => {
			val attribute = new Attribute(name)
			valueNodes.foreach ((valueNode) => attribute.children += valueNode)
			attribute
		} 
	}
	
	def valueNode = primitive | reference | elementNode
	def primitive = (space*) ~> (string | number | boolean) <~ (space*)
	def boolean = (booleanTrue | booleanFalse) ^^ { case booleanValue => new Value(new BooleanValue(booleanValue.toBoolean)) }
	def reference = integerReference | nameReference
	def integerReference = open ~> ref ~> natural <~ close ^^ { case integerNumber => new IntReference(integerNumber.toInt) }
	def nameReference = open ~> ref ~> elementName <~ close ^^ { case (model,name) => new StringReference(model + "." + name) }

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
		case l ~ nid ~ ("." ~ l1 ~ nid1) => (l + nid,l1 + nid1)
	}
	def simpleName = letter ~ nameIdentifier <~ (space*) ^^ { case l ~ nid => l + nid }
	def natural = (digit+) ^^ { case xs => xs.foldLeft("")((v,rest) => v+rest) }
	def suffix = "." ~> natural ^^ { case naturalNumber => "." + naturalNumber }
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
				new Value(new DoubleValue(retNumber.toDouble))
			else
				new Value(new IntValue(retNumber.toInt))
	}
	def string = "'[^']*'".r ^^ { str => new Value(new StringValue(str)) }

	def parse(a: String) : Option[Node] = {
			val r = parseAll(root,a)
					r.get match {
					case Some(model) => model.resolve(pool)
					case _ => None
			}
			None
	}
}
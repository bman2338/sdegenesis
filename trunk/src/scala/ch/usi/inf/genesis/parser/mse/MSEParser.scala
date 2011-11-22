package ch.usi.inf.genesis.parser.mse

import scala.util.parsing.combinator.RegexParsers
import scala.collection.mutable.ListBuffer

import ch.usi.inf.genesis.model._

/**
 * Singleton object that is used to parse MSE files
 * @author Remo Lemma & Patrick Zulian
 */
object MSEParser extends RegexParsers {
	override def skipWhitespace = false
	val pool = new ListBuffer[Entity];
			def root : Parser[Option[Node]] = (document?)
			def document = open ~> (elementNode*) <~ close ^^ { 
				case nodes =>
					val node = new Node
					nodes.foreach((innerNode) => node.addChild(innerNode))
					node
			}
			def elementNode : Parser[Entity] = open ~> elementName ~ (serial?) ~ (attributeNode*) <~ close ^^ { 
				case (model,name) ~ id ~ nodes => {
					val element = new Element(name,model,id)
					nodes.foreach((node) => element.addChild(node))
					id match {
					  case Some(integerId:Int) => pool.insert(integerId,element)
					  case _ =>
					}
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
					new DoubleValue(retNumber.toDouble)
				else
					new IntValue(retNumber.toInt)
			}
			def string = "'[^']*'".r ^^ { str => new StringValue(str) }

			def parse(a: String) : Option[Node] = {
				val r = parseAll(root,a)
				r.get match {
				  case Some(model) => model.resolve(pool)
				  case _ => None
				}
				None
			}
}
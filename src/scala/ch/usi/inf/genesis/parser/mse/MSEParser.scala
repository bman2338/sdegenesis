package ch.usi.inf.genesis.parser.mse

import scala.util.parsing.combinator.RegexParsers
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.HashMap
import scala.collection.Map
import ch.usi.inf.genesis.model.core._
import ch.usi.inf.genesis.model.core.famix._

/**
* Singleton object that is used to parse MSE files
* @author Remo Lemma & Patrick Zulian
*/
object MSEParser extends RegexParsers {

	abstract class MSEEntity {
		var modelObject : ModelObject = null
		def resolve (pool: Map[Int,MSEEntity]) : Option[ModelObject];
	}

	case class Value private () extends MSEEntity {
		def this (obj:ModelObject) = {
			this()
			this.modelObject = obj;
		}
		def resolve (pool: Map[Int,MSEEntity]) = Some(modelObject)
	}

	abstract class Reference extends MSEEntity;

	class StringReference(val id:String) extends Reference {
		override def toString = id
		def resolve (pool: Map[Int,MSEEntity]) = {
			println("String Reference not handled")
			None // We do not handle this right now
		}
	}

	class IntReference(val id:Int) extends Reference {
		override def toString = id.toString
		def resolve (pool: Map[Int,MSEEntity]) : Option[ModelObject] = {
			if (this.modelObject != null)
				return Some(this.modelObject)
			//this.modelObject = new FamixObject();
			val poolItem = pool.get(id)
			poolItem match {
				case Some(obj:MSEEntity) => {
					if (obj.modelObject == null)
						obj.resolve(pool)
					this.modelObject = obj.modelObject
					Some(obj.modelObject)
				}
				case _ => None
			}
		}
	}

	class Node extends MSEEntity {
		val children = new ListBuffer[MSEEntity]

		def unapply(e:MSEEntity) : Boolean = this == e
		def resolve (pool : Map[Int,MSEEntity]) = {
			if (this.modelObject == null) {
			    this.modelObject = new Project
				children.foreach((obj) => { 
					obj.resolve(pool)
					obj match {
					  case Element(name,_,_) => 
					    name match {
					       case FAMIX.NAMESPACE => 
					         this.modelObject.addProperty(FAMIX.NAMESPACES_PROP,obj.modelObject)
					       case _ =>
					    }
					  case _ =>
					}
				})
			}
			Some(this.modelObject)
		}
	}

	case class Attribute(val name:String) extends MSEEntity {
		override def toString = name
		val children = new ListBuffer[MSEEntity]
		override def resolve (pool:Map[Int,MSEEntity]) = {
			if (this.modelObject == null) {
	  			this.modelObject = new Property(name)
				children.foreach((value) => {
					value.resolve(pool) match {
					  case Some(prop) => this.modelObject.addProperty(FAMIX.VALUE_PROP,prop)
					  case None => println(name + " " + value)
					}
				})
			}
			Some(this.modelObject)  
		}
	}

	case class Element(val name:String, val modelName:String, val id:Option[Int]) extends MSEEntity {
		val children = new HashMap[String,MSEEntity]
		override def resolve (pool:Map[Int,MSEEntity]) : Option[ModelObject] = {
			if (modelObject != null)
				return Some(this.modelObject)
			var obj : Option[FamixObject] = None
			
			name match {
  			    case FAMIX.NAMESPACE => obj = Some(new NamespaceEntity(name))
  			    case FAMIX.PARAMETRIZABLECLASS => Some(new ClassEntity(name))
				case FAMIX.CLASS => {
				  obj = Some(new ClassEntity(name))	
				}
				case FAMIX.INHERITANCEDEFINITION => obj = Some(new InheritanceDefinitionRelation)
				case FAMIX.METHOD => obj = Some(new MethodEntity(name))

				case FAMIX.INVOCATION => {
					obj = Some(new InvocationRelation)
				}

				case FAMIX.ATTRIBUTE => {
					obj = Some(new AttributeEntity(name))
				}

				case FAMIX.ACCESS => {
					obj = Some(new AccessRelation)
				}

				case _ => {
					obj = Some(new FamixObject())
					//println("Element " + name + " No Concreate instance")
				}
			}

			obj match {
				case Some(obj) => 
				this.modelObject = obj
				children.foreach((pair) => { 
					val child: Option[ModelObject] = pair._2.resolve(pool) 
					child match {
						case Some(child) =>
						  this.modelObject.addProperty(pair._1, child)
						case None =>
					}

				})
				case None => 
			}

			Some(this.modelObject)
		}
	}

	override def skipWhitespace = false
	val pool = new HashMap[Int,MSEEntity];

	def root : Parser[Option[Node]] = (document?)
	def document = open ~> (elementNode*) <~ close ^^ { 
		case nodes =>
		val node = new Node
		nodes.foreach((innerNode) => node.children += innerNode) 
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

	def valueNode = reference | primitive | elementNode
	def primitive = (space*) ~> (string | number | boolean) <~ (space*)
	def boolean = (booleanTrue | booleanFalse) ^^ { case booleanValue => new Value(new BooleanValue(booleanValue.toBoolean)) }
	def reference = integerReference | nameReference
	def integerReference = open ~> ref ~> natural <~ close ^^ { 
	  case integerNumber =>
	   	//if (integerNumber.toInt == 16720)
	   	//	println("HATE")
	    new IntReference(integerNumber.toInt)
	}
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
		if (isDouble) {
			new Value(new DoubleValue(retNumber.toDouble))
		}
		else {
			new Value(new IntValue(retNumber.toInt))
		}
	}
	def string = "'[^']*'".r ^^ { str => new Value(new StringValue(str)) }

	def parse(a: String) : Option[ModelObject] = {
		val r = parseAll(root,a)
		r.get match {
			case Some(model) => model.resolve(pool)
			case _ => None
		}
	}
}
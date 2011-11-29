package ch.usi.inf.genesis.model.extractors

import ch.usi.inf.genesis.model.core.ModelObject
import scala.collection.mutable.HashSet
import ch.usi.inf.genesis.model.core.FAMIX._
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import ch.usi.inf.genesis.model.core.famix.ClassEntity
import ch.usi.inf.genesis.model.navigation.BreadthFirstNavigator
import scala.collection.mutable.HashMap
import ch.usi.inf.genesis.model.core.famix.ClassEntity

class InheritanceExtractor(val classes: HashSet[String]) extends Extractor {
	var str : String = "";
	var analysis: InheritanceAnalysis = null;


def getSelection(obj:ModelObject) : Boolean = {
  
		obj match {
		  case ClassEntity() => true
		  case _ => false
		}
}

def extract(model: ModelObject): Analysis = { 
		analysis = new InheritanceAnalysis();
		new BreadthFirstNavigator().walkModel(model, this, Some(getSelection));
		analysis.clean();
		return analysis;
}

def visit(obj: ModelObject): NavigatorOption = { 
		val name = obj.getName();
		name match {
			case "" =>
			case _ if(classes.contains(name))=> analysis.addClass(obj); 
		}		

		return analysis.opt();
}

}


class InheritanceAnalysis extends Analysis {
	val classes: HashMap[Int, ModelObject] = new HashMap();

val nameOpenStr = "{ \"name\": \"";
val nameCloseStr = "\"},\n";
val childrenOpenStr = "\"children\": [";
val childrenCloseStr = "]},\n";

override def toString() = { toJSON() }

def opt() : NavigatorOption = {
		return CONTINUE;
}

def toJSON() : String = {
		var str = "";
		if (classes.size == 1)
			str = "function data() { var json = ";			  
		else 
			str = "function data() { var json = { \"name\": \"" + "ROOT" + "\", \"children\": [\n";	
		classes.foreach(pair => {
			val clazz = pair._2;
			str += toJSON(clazz);
		})

		if (classes.size == 1) {
			str = str.substring(0,str.length()-2)
					str += "; return json; }";
		}
		else
			str += "]}; return json; }";
		str = str.replace("'", "");
		return str;
}

private def toJSON(modelObject: ModelObject) : String = {
		val name = modelObject.getName();
		var str = "";

		name match {
		case "" => return ""
		case _  => { 
			str += nameOpenStr + name;

			modelObject.properties.get(SUBCLASS_PROP) match {
			case None => str += nameCloseStr;
			case Some(subclasses) => {
				str +=   "\"," + childrenOpenStr;

				subclasses.foreach(subclass => {
					str += toJSON(subclass);
				});

				str += childrenCloseStr;
			}
			}
		}
		}
		return str;
}

/**
 * Removes all children classes from root
 */
def clean() = {
	classes.foreach(pair => {
		val clazz = pair._2;
		clazz.properties.get(SUBCLASS_PROP) match {
		case None =>
		case Some(subclasses) => {
			subclasses.foreach(subclass => {
				classes.remove(subclass.getId());
			});
		}
		}
	});
}

def addClass(clazz: ModelObject) = {
	classes.put(clazz.getId(), clazz);
}


}
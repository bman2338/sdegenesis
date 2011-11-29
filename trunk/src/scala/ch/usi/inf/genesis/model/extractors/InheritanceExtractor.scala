package ch.usi.inf.genesis.model.extractors

import ch.usi.inf.genesis.model.core.ModelObject
import scala.collection.mutable.HashSet
import ch.usi.inf.genesis.model.core.FAMIX._
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import ch.usi.inf.genesis.model.core.famix.ClassEntity
import ch.usi.inf.genesis.model.navigation.BreadthFirstNavigator
import scala.collection.mutable.HashMap

class InheritanceExtractor extends Extractor {
	var selection: HashSet[String] = null;
	var str : String = "";
	var analysis: InheritanceAnalysis = null;


def getSelection() : HashSet[String] = {
		if(selection == null) {
			selection = new HashSet();
			selection.add(CLASSES_PROP);
		}
		return selection;
}

def extract(model: ModelObject): Analysis = { 
		analysis = new InheritanceAnalysis();
		new BreadthFirstNavigator().walkModel(model, this, Some(getSelection()));
		//analysis.clean();
		return analysis;
}

def visit(obj: ModelObject): NavigatorOption = { 
		obj.getName() match {
		case "" =>
		case _ => analysis.addClass(obj);
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
  if(classes.size > 100)
    return STOP
    return CONTINUE
}

def toJSON() : String = {
		var str = "{ \"name\": \"" + "Object" + "\", \"children\": [\n";	
		classes.foreach(pair => {
			val clazz = pair._2;
			str += toJSON(clazz);
		})

		str += "]}";
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
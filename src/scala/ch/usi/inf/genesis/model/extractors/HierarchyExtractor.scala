package ch.usi.inf.genesis.model.extractors

import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.core.famix.MethodEntity
import ch.usi.inf.genesis.model.navigation.BreadthFirstNavigator
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import scala.collection.mutable.HashMap
import ch.usi.inf.genesis.model.core.FAMIX._
import scala.collection.mutable.HashSet

class HierarchyExtractor(val prop: FAMIX, 
    					 var getSelection: (ModelObject => Boolean),
    					 var getAnalysis: Option[(()=> AbstractHierarchyAnalysis)] = None) extends Extractor {

	var analysis: AbstractHierarchyAnalysis = null;
	
def extract(model: ModelObject): Analysis = { 
		getAnalysis match {
		  case None => analysis = new HierarchyAnalysis(prop);
		  case Some(getAnalysis) => analysis = getAnalysis();
		}
		analysis.title = model.getName()
		new BreadthFirstNavigator().walkModel(model, this, Some(getSelection));
		analysis.clean();
		return analysis;
}

def visit(obj: ModelObject): NavigatorOption = { 
		val name = obj.getName();
		name match {
			case "" =>
			case _  =>  analysis.addNode(obj);
			} 
	
		return analysis.opt();
}

}

abstract class AbstractHierarchyAnalysis extends Analysis {
  var title = ""
  def addNode(node: ModelObject) : Unit;
  def clean() : Unit;
  def opt() : NavigatorOption;
}


class HierarchyAnalysis(val prop: FAMIX,
						val optAuxAddNode : Option[(ModelObject, HierarchyAnalysis) => Unit] = None) extends AbstractHierarchyAnalysis {
	val nodes: HashMap[Int, ModelObject] = new HashMap();
	val nameOpenStr = "{ \"name\": \"";
	val nameCloseStr = "\"},\n";
	val childrenOpenStr = "\"children\": [";
	val childrenCloseStr = "]},\n";
	var auxAddNode : (ModelObject) => Unit = (node) => { nodes.put(node.getId(), node) };
	
	
	optAuxAddNode match {
	  case None => 
	  case Some(f) => auxAddNode = (node) => { f(node, this) };
	}
	

override def toString() = { toJSON() }

def opt() : NavigatorOption = {
		return CONTINUE;
}

def toJSON() : String = {
		var visited = new HashSet[Int]();
	
		var str = "";
		if (nodes.size == 1)
			str = "function " + title + "_data() { var json = ";			  
		else 
			str = "function " + title + "_data() { var json = { \"name\": \"" + title + "\", \"children\": [\n";	
		
		
		nodes.foreach(pair => {
			val node = pair._2;
			str += toJSON(node, visited);
			
		})

		if (nodes.size == 1) {
			str = str.substring(0,str.length()-2)
					str += "; return json; }";
		}
		else
			str += "]}; return json; }";
		str = str.replace("'", "");
		return str;
}

private def toJSON(modelObject: ModelObject, visited: HashSet[Int]) : String = {
		val name = modelObject.getName();
		var str = "";

		name match {
		case "" => visited.add(modelObject.getId()); return "";
		case _  => { 
			str += nameOpenStr + name;
			
			if(visited.contains(modelObject.getId())) {
			   str += nameCloseStr;
			   return str;
			} else {
			  visited.add(modelObject.getId());
			}


			modelObject.getProperties(prop) match {
			case None => str += nameCloseStr;
			case Some(children) => {
				str +=   "\"," + childrenOpenStr;

				children.foreach(child => {
					str += toJSON(child, visited);
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
	nodes.foreach(pair => {
		val node = pair._2;
		 node.properties.get(prop) match {
		case None =>
		case Some(children) => {
			children.foreach(child => {
				nodes.remove(child.getId());
			});
		}
		}
	});
}

def addNode(node: ModelObject) = {
	auxAddNode(node);
}

}
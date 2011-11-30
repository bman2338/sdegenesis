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
    					 var getAnalysis: Option[(()=> HierarchyAnalysis)] = None) extends Extractor {

	var str : String = "";
	var analysis: HierarchyAnalysis = null;
	

//def getSelection(obj:ModelObject) : Boolean = {
//  
//		obj match {
//		  case MethodEntity() => true
//		  case _ => false
//		}
//}

def extract(model: ModelObject): Analysis = { 
		getAnalysis match {
		  case None => analysis = new HierarchyAnalysis(prop);
		  case Some(getAnalysis) => analysis = getAnalysis();
		}
		
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


class HierarchyAnalysis(val prop: FAMIX,
						val optAuxAddNode : Option[(ModelObject, HierarchyAnalysis) => Unit] = None) extends Analysis {
	val nodes: HashMap[Int, ModelObject] = new HashMap();
	val nameOpenStr = "{ \"name\": \"";
	val nameCloseStr = "\"},\n";
	val childrenOpenStr = "\"children\": [";
	val childrenCloseStr = "]},\n";
	var auxAddNode : (ModelObject) => Unit = (node) => nodes.put(node.getId(), node);;
	
	
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
			str = "function data() { var json = ";			  
		else 
			str = "function data() { var json = { \"name\": \"" + "ROOT" + "\", \"children\": [\n";	
		
		
//		nodes.foreach(pair => {
//			val node = pair._2;
//			visited.add(node.getId());
//			
//		})
		
		
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
		case "" => return ""
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
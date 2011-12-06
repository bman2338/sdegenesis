package ch.usi.inf.genesis.model.extractors

import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.core.famix.MethodEntity
import ch.usi.inf.genesis.model.navigation.BreadthFirstNavigator
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import scala.collection.mutable.HashMap
import ch.usi.inf.genesis.model.core.FAMIX._
import scala.collection.mutable.HashSet


class HierarchyExtractor(val prop: FAMIX, 
    					 var selectionFun: (ModelObject => Boolean),
    					 var getAnalysis: Option[(()=> AbstractHierarchyAnalysis)] = None) extends Extractor {

	var analysis: AbstractHierarchyAnalysis = null;
	override def getSelection() = {
    selectionFun
  }

def extract(model: ModelObject): Analysis = { 
		getAnalysis match {
		  case None => analysis = new HierarchyAnalysis(prop);
		  case Some(getAnalysis) => analysis = getAnalysis();
		}
		analysis.title = model.getName()
		new BreadthFirstNavigator().walkModel(model, this, Some(getSelection()));
		analysis.clean();
		println("/* Hierarchycal analysis*/");
		return analysis;
}

def visit(obj: ModelObject): NavigatorOption = { 
	//	val name = obj.getName();
	//	name match {
		//	case "" => CONTINUE
			//case _  => 
			 analysis.addNode(obj);
			//} 
}

}

abstract class AbstractHierarchyAnalysis extends Analysis {
  var title = ""
  def addNode(node: ModelObject) : NavigatorOption;
  def clean() : Unit;
}


class HierarchyAnalysis(val prop: FAMIX,
						val optAuxAddNode : Option[(ModelObject, HierarchyAnalysis) => NavigatorOption] = None) extends AbstractHierarchyAnalysis {
	val nodes: HashMap[Int, ModelObject] = new HashMap();
	val nameOpenStr = "{ \"name\": \"";
	val nameCloseStr = "\"},\n";
	val childrenOpenStr = "\"children\": [";
	val childrenCloseStr = "]},\n";
	var auxAddNode : (ModelObject) => NavigatorOption = (node) => { nodes.put(node.getId(), node); SKIP_SUBTREE };
	
	
	optAuxAddNode match {
	  case None => 
	  case Some(f) => auxAddNode = (node) => { f(node, this) };
	}
	

override def toString() = { toJSON() }


def toJSON() : String = {
		var visited = new HashSet[Int]();
		val buffer = new StringBuffer;
		
		 
		if (nodes.size == 1)
			buffer.append("function " + title.replace(" ", "_") + "_data() { var json = ");			  
		else 
			buffer.append("function " + title.replace(" ", "_") + "_data() { var json = { \"name\": \"" + title + "\", \"children\": [\n");	
		
		nodes.foreach(pair => {
			val node = pair._2;
			toJSON(node, visited, buffer);
			visited.clear();
		})

		if (nodes.size == 1) {
			buffer.deleteCharAt(buffer.length()-1);
			buffer.append("; return json; }");
		}
		else
			buffer.append("]}; return json; }");
		return buffer.toString();
}

def attrToJSON(model: ModelObject, buffer: StringBuffer) : Unit = {
//	println("/* attrToJSON */");
	model.properties.foreach(pair => {
		val key = pair._1;
		val property = pair._2;
		if(key != prop && key != NAME_PROP) {
		
			if(property.length > 1) {
				// 		  buffer.append(": [")
				// property.foreach(value => {
				// 	buffer.append("{\"");
				// 	buffer.append(value.toString());
				// 	buffer.append("}\"");
				// })
				// buffer.append("],");
		}else {
			buffer.append(", ");
			buffer.append(key);
			buffer.append(": \"");
			buffer.append(property.head.toString());
			buffer.append("\", ");
		}
		
		}
	});
}

private def toJSON(modelObject: ModelObject, visited: HashSet[Int], buffer: StringBuffer) : Unit = {
  
		
		val name = modelObject.getName();
		
		name match {
		case "" => visited.add(modelObject.getId()); return;
		case _  => { 
			buffer.append(nameOpenStr + name);
			
			if(visited.contains(modelObject.getId())) {
			   buffer.append(nameCloseStr);
			   return;
			} else {
			  visited.add(modelObject.getId());
			}
			
			attrToJSON(modelObject, buffer);

			modelObject.getProperties(prop) match {
			case None => buffer.append(nameCloseStr);
			case Some(children) => {
				buffer.append( "\"," + childrenOpenStr);

				children.foreach(child => {
					 toJSON(child, visited, buffer);
				});

				buffer.append(childrenCloseStr);
			}
			}
		}
		}
}

/**
 * Removes all children classes from root
 */
def clean() : Unit = {
  
	val nodesCopy = nodes.clone();
	nodesCopy.foreach(pair => {
		val node = pair._2;
		
		 node.getProperties(prop) match {
		case None => nodes.remove(node.getId())
		case Some(children) => {
			if(children.isEmpty) {
			  nodes.remove(node.getId());
			} else
			children.foreach(child => {
				nodes.remove(child.getId());
			});
		}
		}
	});
}

def addNode(node: ModelObject) : NavigatorOption = {
	auxAddNode(node);
}

}
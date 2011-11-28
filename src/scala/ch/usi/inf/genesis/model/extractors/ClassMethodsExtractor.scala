package ch.usi.inf.genesis.model.extractors

import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.navigation.ModelVisitor
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import ch.usi.inf.genesis.model.navigation._
import scala.collection.mutable.HashSet
import ch.usi.inf.genesis.model.core.FAMIX._

class ClassMethodsExtractor extends ModelVisitor {
	var selection: HashSet[String] = null;
	var classes: HashSet[String] = null;
	var str : String = "";

def extract(model: ModelObject) : String = {
		str = "{ \"name\": \"" + model.getName() + "\", \"children\": [";
		new BreadthFirstNavigator().walkModel(model, this, Some(getSelection()));
		str += "]}";
		str = str.replace("'", "");
		str;
}

def getSelection() : HashSet[String] = {
		if(selection == null) {
			selection = new HashSet();
			selection.add(CLASSES_PROP);
			classes = new HashSet();
			classes.add("'BugzillaXMLParser'");
			classes.add("'BugTrackerCrawler'");
			classes.add("'JiraCrawler'");
			classes.add("'BugTrackerUser'");
			classes.add("'LanguageFactory'");
			classes.add("'Environment'");
			classes.add("'BugzillaCrawler'");
		}
		return selection;
}

def visit(obj: ModelObject): NavigatorOption = {
		if(!classes.contains(obj.getName()))
		  return CONTINUE;
  
		val name = obj.getName();
		
		val nameOpenStr = "{ \"name\": \"";
		val nameCloseStr = "\"},";
		val childrenOpenStr = "\"children\": [";
		val childrenCloseStr = "]},\n";
		
		name match {
		  case "" =>
		  case _  => { 
		    str += nameOpenStr + name + "\", " + childrenOpenStr;
		   
		    obj.properties.get(METHODS_PROP) match {
		      case None =>
		      case Some(methods) => {
		        methods.foreach(method => {
		          str += nameOpenStr + method.getName() + nameCloseStr;
		        });
		      }
		    }
		    str += childrenCloseStr;
		  }
		}
		
		CONTINUE;
}

}
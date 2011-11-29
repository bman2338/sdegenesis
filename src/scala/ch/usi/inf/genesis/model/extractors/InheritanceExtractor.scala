package ch.usi.inf.genesis.model.extractors

import ch.usi.inf.genesis.model.core.ModelObject
import scala.collection.mutable.HashSet
import ch.usi.inf.genesis.model.core.FAMIX._
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import ch.usi.inf.genesis.model.core.famix.ClassEntity
import ch.usi.inf.genesis.model.navigation.BreadthFirstNavigator

class InheritanceExtractor extends Extractor {
	var selection: HashSet[String] = null;
	var str : String = "";
	
	def getSelection() : HashSet[String] = {
		if(selection == null) {
			selection = new HashSet();
			selection.add(CLASSES_PROP);
		}
		return selection;
}
	
  def extract(model: ModelObject): Analysis = { 
    val analysis = new InheritanceAnalysis();
    new BreadthFirstNavigator().walkModel(model, this, Some(getSelection()));
    return analysis;
  }

  def visit(obj: ModelObject): NavigatorOption = { 
    
    return CONTINUE
  }

}


class InheritanceAnalysis extends Analysis {
  //val classes:  HashSet[Int, ModelObject] = new HashSet();
  def toJSON() : String = {
    ""
  }
  
  def addChildren(superclass: ModelObject, subclasses: List[ModelObject]) = {
    
  }
  
  
}
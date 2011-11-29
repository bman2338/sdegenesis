package ch.usi.inf.genesis.model.navigation

import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import ch.usi.inf.genesis.model.core.FAMIX._
import scala.collection.mutable.HashSet
import ch.usi.inf.genesis.model.core.famix.NamespaceEntity


class ModelSaver extends ModelVisitor {
	var projectName : String = "";
	
  def getSelection() : Option[HashSet[String]] = {
    val selection:  HashSet[String]  = new HashSet();
   
    selection.add(NAMESPACES_PROP);
    selection.add(CLASSES_PROP);
    selection.add(METHODS_PROP);
    selection.add(ATTRIBUTES_PROP);
    
    return Some(selection);
  }
  
  def visit(obj: ModelObject): NavigatorOption = {
		  obj match  {
		    case NamespaceEntity(ns) => {
		      var parentPackage = "";
		      var owner = "";
		      var rev = 0;
		      ns.properties.get(PARENT_PACKAGE) match {
		        case None =>
		        case Some(list) if(list.length > 0) => parentPackage = list.first.getName() 
		      }
		      //TODO owner
		      //TODO rev
		      
		      DatabaseInterface.addPackage(projectName, ns.getName(), owner, rev, parentPackage );
		    }
		    case _ =>
		  }
    return CONTINUE
  }

}
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
			//save packages
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
		
			//save classes
			case ClassEntity(ce) => {
				var belongsToPackage = "";
			    var owner = "";
			    var rev = 0;
				
				//package containing this class
				ce.properties.get(CONTAINER) match {
			        case None =>
			        case Some(list) if(list.length > 0) => belongsToPackage = list.first.getName() 
			    }
			
				//TODO owner
			    //TODO rev
				
				DatabaseInterface.addClass(projectName, belongsToPackage, ce.getName(), owner, revisionNumber)
			}
			
			//save methods
			case MethodEntity(me){
				var belongsToPackage = "";
			    var owner = "";
			    var rev = 0;
				var signature = "";
				var modifiers = "";
				var returnType = "";
				var className = "";
				
				
				
				DatabaseInterface.addMethod(projectName, className, me.getName(), signature, modifiers, returnType, owner, revisionNumber)
			}
			
			//save attributes
			case AttributeEntity(ae){
				var belongsToPackage = "";
			    var owner = "";
			    var rev = 0;
				var signature = "";
				var modifiers = "";
				var declaredType = "";
				var className = "";
				
				DatabaseInterface.addAttribute(projectName, className, ae.getName(), signature, modifiers, declaredType, revisionNumber)
			}
		    case _ => CONTINUE
		  }
    return CONTINUE
  }

}
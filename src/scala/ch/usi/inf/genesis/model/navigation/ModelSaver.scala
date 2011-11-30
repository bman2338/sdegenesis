package ch.usi.inf.genesis.model.navigation

import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import ch.usi.inf.genesis.model.core.FAMIX._
import scala.collection.mutable.HashSet
import ch.usi.inf.genesis.model.core.famix._


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
		  //project is already added to the database...
		  
//		  obj match  {
//		    
//			//save packages
//		    case NamespaceEntity() => {
//		      var parentPackage = "";
//		      var owner = "";
//		      var rev = 0;
//		      
//		      //package containing this package
//		      parentPackage = obj.getProperty(PARENT_PACKAGE);
//		      
//		      //get the revision number
//		      rev = obj.getProperty(REVISION_PROP);
//		      
//		      //get the owner
//		      owner = obj.getProperty(OWNER_PROP);
//		      
//		      //DatabaseInterface.addPackage(projectName, obj.getName(), owner, rev, parentPackage );
//		    }
//		
//			//save classes
//			case ClassEntity() => {
//				var belongsToPackage = "";
//			    var owner = "";
//			    var rev = 0;
//				
//				//package containing this class
//				belongsToPackage = obj.getProperty(CONTAINER);
//			
//				//get the revision number
//				rev = obj.getProperty(REVISION_PROP);
//		      
//				//get the owner
//				owner = obj.getProperty(OWNER_PROP);
//				
//				//DatabaseInterface.addClass(projectName, belongsToPackage, obj.getName(), owner, revisionNumber)
//			
//				//now save the inheritance
//				var superclasses = obj.getProperty(SUBCLASS_PROP); //on FAMIX.scala it's written "subclassOf"
//				
//				for(superclass <- superclasses){
//					//DatabaseInterface.addInheritance(projectName, superclass, obj.getName(), versionNumber)
//				}
//			}
//			
//			//save methods
//			case MethodEntity() => {
//				var belongsToPackage = "";
//			    var owner = "";
//			    var rev = 0;
//				var signature = "";
//				var modifiers = "";
//				var returnType = "";
//				var className = "";
//				
//				//get the revision number
//				rev = obj.getProperty(REVISION_PROP);
//		      
//				//get the owner
//				owner = obj.getProperty(OWNER_PROP);
//				
//				//get the signature
//				signature = obj.getProperty(SIGNATURE_PROP);
//				
//				//get the modifiers
//				modifiers = obj.getProperty(MODIFIERS_PROP);
//				
//				//get the return type
//				returnType = obj.getPropert(DECLARED_TYPE_PROP);
//				
//				//get the class name containing this method
//				className = obj.properties.get(PARENT_TYPE_PROP);
//				
//				//DatabaseInterface.addMethod(projectName, className, obj.getName(), signature, modifiers, returnType, owner, revisionNumber)
//			
//				//HOW TO ADD METHOD INVOKATIONS? WE ARE NOT SURE IF METHODS ARE ALREADY THERE
//				//IN THE DATABASE IN THE FIRST PLACE
//			}
//			
//			//save attributes
//			case AttributeEntity() => {
//				var belongsToPackage = "";
//			    var owner = "";
//			    var rev = 0;
//				var signature = "";
//				var modifiers = "";
//				var declaredType = "";
//				var className = "";
//				
//				//get the revision number
//				rev = obj.getProperty(REVISION_PROP);
//		      
//				//get the owner
//				owner = obj.getProperty(OWNER_PROP);
//				
//				//get the signature
//				signature = obj.getProperty(SIGNATURE_PROP);
//				
//				//get the modifiers
//				modifiers = obj.getProperty(MODIFIERS_PROP);
//				
//				//get the return type
//				returnType = obj.getPropert(DECLARED_TYPE_PROP);
//				
//				//get the class name containing this method
//				className = obj.properties.get(PARENT_TYPE_PROP);
//				
//				//DatabaseInterface.addAttribute(projectName, className, obj.getName(), signature, modifiers, declaredType, revisionNumber)
//			}
//			
//			//add a developer
//			case DeveloperEntity() => {
//			  //DatabaseInterface.addDeveloper(obj.getName());
//			}
//			
//			//add a bug tracker developer
//			case BTDeveloperEntity() => {
//			  var btdevEmail = "";
//			  
//			  //get the class name containing this method
//			  btdevEmail = obj.properties.get(BTDEVELOPER_EMAIL_PROP);
//			  
//			  //DatabaseInterface.addDeveloper(obj.getName(), btdevEmail);
//			}
//			
//			//add a bug
//			case BugEntity() => {
//			  var desc = "";
//			  var status = "";
//			  var assignee = "";
//			  
//			  //get the description of the bug
//			  desc = obj.properties.get(BUG_DESCRIPTION_PROP);
//			  
//			  //get the bug status
//			  status = obj.properties.get(BUG_STATUS_PROP);
//			  
//			  //get the current assignee
//			  assignee = obj.properties.get(BUG_ASSIGNEE_PROP);
//			  
//			  //DatabaseInterface.addBug(projectName, obj.getName(), desc, status, assignee)
//			  
//			  //add history for a bug
//			  var assignees = obj.properties.get(PREVIOUS_ASSIGNEES_PROP);
//			  
//			  for (i <- assignees){
//				  //DatabaseInterface.addBugTrackerHistory(projectName, obj.getName(), i.getName())
//			  }
//			   
//			}
//			
//			//add a revision
//			case RevisionEntity() => {
//			  var comment = "";
//			  var rev = 0;
//			  var dev = "";
//			  var date = "";
//			  
//			  //get the comment of the revision
//			  comment = obj.properties.get(REVISION_COMMENT_PROP);
//			  
//			  //get the developer that committed the revision
//			  dev = obj.properties.get(REVISION_DEVELOPER_PROP);
//			  
//			  //get the date of the commit
//			  date = obj.properties.get(REVISION_DATE_PROP);
//			  
//			  //get the revision number
//		      rev = obj.properties.get(REVISION_PROP);
//			  
//			  //DatabaseInterface.addRevision(projectName, comment, rev, dev, date)
//			}
//			
//			//add a class metric
//			case ClassMetricEntity() => {
//			  var ClassName = "";
//			  var metricName = "";
//			  var value = 0;
//			  var rev = 0;
//			  
//			  //get the revision number
//		      rev = obj.properties.get(REVISION_PROP);
//			  
//			  //DatabaseInterface.addClassMetric(projectName, ClassName, metricName, value, rev)
//			}
//			
//			//add a class metric
//			case MethodMetricEntity() => {
//			  var methodName = "";
//			  var metricName = "";
//			  var value = 0;
//			  var rev = 0;
//			  
//			  //get the revision number
//		      rev = obj.properties.get(REVISION_PROP);
//			  
//			  //DatabaseInterface.addClassMetric(projectName, methodName, metricName, value, rev)
//			}
//			
//		    case _ => CONTINUE
//		  }
    return CONTINUE
  }

}
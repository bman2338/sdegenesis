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
		  
		  obj match  {
		    
			//save packages
		    case NamespaceEntity() => {
		      var parentPackage = "";
		      var owner = "";
		      var rev = 0;
		      
		      //package containing this package
		      obj.properties.get(PARENT_PACKAGE) match {
		        case None =>
		        case Some(list) if(list.length > 0) => parentPackage = list.first.getName() 
		      }
		      
		      //get the revision number
		      obj.properties.get(REVISION_PROP) match {
		        case None =>
		        case Some(list) if(list.length > 0) => rev = list.first.getName()
		      }
		      
		      //get the owner
		      obj.properties.get(OWNER_PROP) match {
		        case None =>
		        case Some(list) if(list.length > 0) => owner = list.first.getName() 
		      }
		      
		      //DatabaseInterface.addPackage(projectName, obj.getName(), owner, rev, parentPackage );
		    }
		
			//save classes
			case ClassEntity() => {
				var belongsToPackage = "";
			    var owner = "";
			    var rev = 0;
				
				//package containing this class
				obj.properties.get(CONTAINER) match {
			        case None =>
			        case Some(list) if(list.length > 0) => belongsToPackage = list.first.getName() 
			    }
			
				//get the revision number
				obj.properties.get(REVISION_PROP) match {
		        	case None =>
		        	case Some(list) if(list.length > 0) => rev = list.first.getName() 
				}
		      
		      //get the owner
		      obj.properties.get(OWNER_PROP) match {
		        case None =>
		        case Some(list) if(list.length > 0) => owner = list.first.getName() 
		      }
				
				//DatabaseInterface.addClass(projectName, belongsToPackage, obj.getName(), owner, revisionNumber)
			
				//now save the inheritance
				obj.properties.get() match {
					case None => 
					case Some(list) => 
						
						//DatabaseInterface.addInheritance(projectName, obj.getName(), String subclass, versionNumber)
				}
			}
			
			//save methods
			case MethodEntity() => {
				var belongsToPackage = "";
			    var owner = "";
			    var rev = 0;
				var signature = "";
				var modifiers = "";
				var returnType = "";
				var className = "";
				
				//get the revision number
				obj.properties.get(REVISION_PROP) match {
		        	case None =>
		        	case Some(list) if(list.length > 0) => rev = list.first.getName() 
				}
		      
				//get the owner
				obj.properties.get(OWNER_PROP) match {
		        	case None =>
		        	case Some(list) if(list.length > 0) => owner = list.first.getName() 
				}
				
				//get the signature
				obj.properties.get(SIGNATURE_PROP) match {
			        case None =>
			        case Some(list) if(list.length > 0) => signature = list.first.getName() 
			    }
				
				//get the modifiers
				obj.properties.get(MODIFIERS_PROP) match {
			        case None =>
			        case Some(list) if(list.length > 0) => modifiers = list.first.getName() 
			    }
				
				//get the return type
				obj.properties.get(DECLARED_TYPE_PROP) match {
			        case None =>
			        case Some(list) if(list.length > 0) => returnType = list.first.getName() 
			    }
				
				//get the class name containing this method
				obj.properties.get(PARENT_TYPE_PROP) match {
			        case None =>
			        case Some(list) if(list.length > 0) => className = list.first.getName() 
			    }
				
				//DatabaseInterface.addMethod(projectName, className, obj.getName(), signature, modifiers, returnType, owner, revisionNumber)
			
				//HOW TO ADD METHOD INVOKATIONS? WE ARE NOT SURE IF METHODS ARE ALREADY THERE
				//IN THE DATABASE IN THE FIRST PLACE
			}
			
			//save attributes
			case AttributeEntity() => {
				var belongsToPackage = "";
			    var owner = "";
			    var rev = 0;
				var signature = "";
				var modifiers = "";
				var declaredType = "";
				var className = "";
				
				//get the revision number
				obj.properties.get(REVISION_PROP) match {
					case None =>
					case Some(list) if(list.length > 0) => rev = list.first.getName() 
				}
		      
				//get the owner
				obj.properties.get(OWNER_PROP) match {
		        	case None =>
		        	case Some(list) if(list.length > 0) => owner = list.first.getName() 
				}
				
				//get the signature
				obj.properties.get(SIGNATURE_PROP) match {
			        case None =>
			        case Some(list) if(list.length > 0) => signature = list.first.getName() 
			    }
				
				//get the modifiers
				obj.properties.get(MODIFIERS_PROP) match {
			        case None =>
			        case Some(list) if(list.length > 0) => modifiers = list.first.getName() 
			    }
				
				//get the return type
				obj.properties.get(DECLARED_TYPE_PROP) match {
			        case None =>
			        case Some(list) if(list.length > 0) => declaredType = list.first.getName() 
			    }
				
				//get the class name containing this method
				obj.properties.get(PARENT_TYPE_PROP) match {
			        case None =>
			        case Some(list) if(list.length > 0) => className = list.first.getName() 
			    }
				
				//DatabaseInterface.addAttribute(projectName, className, obj.getName(), signature, modifiers, declaredType, revisionNumber)
			}
			
			//add a developer
			case DeveloperEntity() => {
			  //DatabaseInterface.addDeveloper(obj.getName());
			}
			
			//add a bug tracker developer
			case BTDeveloperEntity() => {
			  var btdevEmail = "";
			  
			  //get the class name containing this method
			  obj.properties.get(BTDEVELOPER_EMAIL_PROP) match {
			      case None =>
			      case Some(list) if(list.length > 0) => btdevEmail = list.first.getName() 
			  }
			  
			  //DatabaseInterface.addDeveloper(obj.getName(), btdevEmail);
			}
			
			//add a bug
			case BugEntity() => {
			  var desc = "";
			  var status = "";
			  var assignee = "";
			  
			  //get the description of the bug
			  obj.properties.get(BUG_DESCRIPTION_PROP) match {
			      case None =>
			      case Some(list) if(list.length > 0) => desc = list.first.getName() 
			  }
			  
			  //get the bug status
			  obj.properties.get(BUG_STATUS_PROP) match {
			      case None =>
			      case Some(list) if(list.length > 0) => status = list.first.getName() 
			  }
			  
			  //get the current assignee
			  obj.properties.get(BUG_ASSIGNEE_PROP) match {
			      case None =>
			      case Some(list) if(list.length > 0) => assignee = list.first.getName() 
			  }
			  
			  //DatabaseInterface.addBug(projectName, obj.getName(), desc, status, assignee)
			  
			  //add history for a bug
			  var assignees = obj.properties.get(PREVIOUS_ASSIGNEES_PROP);
			  
			  for (i <- assignees){
				  //DatabaseInterface.addBugTrackerHistory(projectName, obj.getName(), i.getName())
			  }
			   
			}
			
			//add a revision
			case RevisionEntity() => {
			  var comment = "";
			  var rev = 0;
			  var dev = "";
			  var date = "";
			  
			  //get the comment of the revision
			  obj.properties.get(REVISION_COMMENT_PROP) match {
			      case None =>
			      case Some(list) if(list.length > 0) => comment = list.first.getName() 
			  }
			  
			  //get the developer that committed the revision
			  obj.properties.get(REVISION_DEVELOPER_PROP) match {
			      case None =>
			      case Some(list) if(list.length > 0) => dev = list.first.getName() 
			  }
			  
			  //get the date of the commit
			  obj.properties.get(REVISION_DATE_PROP) match {
			      case None =>
			      case Some(list) if(list.length > 0) => date = list.first.getName() 
			  }
			  
			  //get the revision number
		      obj.properties.get(REVISION_PROP) match {
		        case None =>
		        case Some(list) if(list.length > 0) => rev = list.first.getName() 
		      }
			  
			  //DatabaseInterface.addRevision(projectName, comment, rev, dev, date)
			}
			
			//add a class metric
			case ClassMetricEntity() => {
			  var ClassName = "";
			  var metricName = "";
			  var value = 0;
			  var rev = 0;
			  
			  //get the revision number
		      obj.properties.get(REVISION_PROP) match {
		        case None =>
		        case Some(list) if(list.length > 0) => rev = list.first.getName() 
		      }
			  
			  //DatabaseInterface.addClassMetric(projectName, ClassName, metricName, value, rev)
			}
			
			//add a class metric
			case MethodMetricEntity() => {
			  var methodName = "";
			  var metricName = "";
			  var value = 0;
			  var rev = 0;
			  
			  //get the revision number
		      obj.properties.get(REVISION_PROP) match {
		        case None =>
		        case Some(list) if(list.length > 0) => rev = list.first.getName() 
		      }
			  
			  //DatabaseInterface.addClassMetric(projectName, methodName, metricName, value, rev)
			}
			
		    case _ => CONTINUE
		  }
    return CONTINUE
  }

}
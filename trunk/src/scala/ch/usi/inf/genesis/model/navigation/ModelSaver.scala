package ch.usi.inf.genesis.model.navigation

import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import ch.usi.inf.genesis.model.core.FAMIX._
import scala.collection.mutable.HashSet
import ch.usi.inf.genesis.model.core.famix._


class ModelSaver extends ModelVisitor {
	var projectName : String = "";
	
//  def getSelection() : Option[HashSet[String]] = {
//    val selection:  HashSet[String]  = new HashSet();
//   
//    selection.add(NAMESPACES_PROP);
//    selection.add(CLASSES_PROP);
//    selection.add(METHODS_PROP);
//    selection.add(ATTRIBUTES_PROP);
//    
//    return Some(selection);
//  }

  def visit(obj: ModelObject): NavigatorOption = {
		  //project is already added to the database...
		  
//		  obj match  {
//		    
//			//save packages
//		    case NamespaceEntity() => {
//		      var parentPackage = obj.getProperty(PARENT_PACKAGE);
//		      var owner = obj.getProperty(OWNER_PROP);
//		      var rev = obj.getProperty(REVISION_PROP);
//		      
//		      if(parentPackage == None){
//		        parentPackage = "";
//		      }
//		      
//		      if(owner == None){
//		        owner = "";
//		      }
//		      
//		      if(rev == None){
//		        rev = "0";
//		      }
//		      
//		      //DatabaseInterface.addPackage(projectName, obj.getName(), owner, Integer.parseInt(rev.toString()), parentPackage );
//		    }
//		
//			//save classes
//			case ClassEntity() => {
//				var belongsToPackage = obj.getProperty(PARENT_PACKAGE);
//				var owner = obj.getProperty(OWNER_PROP);
//				var rev = obj.getProperty(REVISION_PROP);
//		      
//				if(belongsToPackage == None){
//					belongsToPackage = "";
//				}
//		      
//				if(owner == None){
//					owner = "";
//				}
//		      
//				if(rev == None){
//					rev = "0";
//				}
//				
//				//DatabaseInterface.addClass(projectName, belongsToPackage, obj.getName(), owner, Integer.parseInt(rev.toString()))
//			
//				//now save the inheritance
//				var superclasses = obj.getProperties(SUBCLASS_PROP); //on FAMIX.scala it's written "subclassOf"
//				superclasses match {
//				  case Some(x) => 
//				    for(superclass <- x){
//						//DatabaseInterface.addInheritance(projectName, superclass, obj.getName(), Integer.parseInt(rev.toString()))
//					}
//				  case _ =>
//				}
//			}
//			
//			//save methods
//			case MethodEntity() => {
//				var belongsToPackage = obj.getProperty(PARENT_PACKAGE);
//			    var owner = obj.getProperty(OWNER_PROP);
//			    var rev = obj.getProperty(REVISION_PROP);
//				var signature = obj.getProperty(SIGNATURE_PROP);
//				var modifiers = obj.getProperties(MODIFIERS_PROP);
//				var returnType = obj.getProperty(DECLARED_TYPE_PROP);
//				var className = obj.getProperty(PARENT_TYPE_PROP);
//				
//				if(belongsToPackage == None){
//					belongsToPackage = "";
//				}
//		      
//				if(owner == None){
//					owner = "";
//				}
//		      
//				if(rev == None){
//					rev = "0";
//				}
//				
//				if(signature == None){
//					signature = "";
//				}
//				
//				if(returnType == None){
//				  returnType = "";
//				}
//				
//				if(className == None){
//				  className = "";
//				}
//				
//				if(modifiers == None){
//				  //DatabaseInterface.addMethod(projectName, className, obj.getName(), signature, "", returnType, owner, Integer.parseInt(rev.toString()))
//				} else {
//				  var modif = "";
//				  for(i <- modifiers){
//				    modif += i + " ";
//				  }
//				  
//				  //DatabaseInterface.addMethod(projectName, className, obj.getName(), signature, modif, returnType, owner, Integer.parseInt(rev.toString()))
//				}
//			
//				//HOW TO ADD METHOD INVOKATIONS? WE ARE NOT SURE IF METHODS ARE ALREADY THERE
//				//IN THE DATABASE IN THE FIRST PLACE
//			}
//			
//			//save attributes
//			case AttributeEntity() => {
//				var belongsToPackage = "";
//			    var owner = obj.getProperty(OWNER_PROP);
//			    var rev = obj.getProperty(REVISION_PROP);
//				var signature = obj.getProperty(SIGNATURE_PROP);
//				var modifiers = obj.getProperties(MODIFIERS_PROP);
//				var declaredType = obj.getProperty(DECLARED_TYPE_PROP);
//				var className = obj.getProperty(PARENT_TYPE_PROP);
//				
//				if(belongsToPackage == None){
//					belongsToPackage = "";
//				}
//		      
//				if(owner == None){
//					owner = "";
//				}
//		      
//				if(rev == None){
//					rev = "0";
//				}
//				
//				if(signature == None){
//					signature = "";
//				}
//				
//				if(declaredType == None){
//				  declaredType = "";
//				}
//				
//				if(className == None){
//				  className = "";
//				}
//				
//				if(modifiers == None){
//				  //DatabaseInterface.addMethod(projectName, className, obj.getName(), signature, "", returnType, owner, Integer.parseInt(rev.toString()))
//				} else {
//				  var modif = "";
//				  for(i <- modifiers){
//				    modif += i + " ";
//				  }
//				  
//				  //DatabaseInterface.addMethod(projectName, className, obj.getName(), signature, modif, returnType, owner, Integer.parseInt(rev.toString()))
//				}
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
//			  var btdevEmail = obj.getProperty(BTDEVELOPER_EMAIL_PROP);
//			  
//			  if(btdevEmail == None){
//			    btdevEmail = "";
//			  }
//			  
//			  //DatabaseInterface.addDeveloper(obj.getName(), btdevEmail);
//			}
//			
//			//add a bug
//			case BugEntity() => {
//			  var desc =  obj.getProperty(BUG_DESCRIPTION_PROP);
//			  var status = obj.getProperty(BUG_STATUS_PROP);
//			  var assignee = obj.getProperty(BUG_ASSIGNEE_PROP);
//			  
//			  if(desc == None){
//			    desc = "";
//			  }
//			  
//			  if(status == None){
//			    status = "";
//			  }
//			  
//			  if(assignee == None){
//			    assignee = "";
//			  }
//			  
//			  //DatabaseInterface.addBug(projectName, obj.getName(), desc, status, assignee)
//			  
//			  //add history for a bug
//			  var assignees = obj.properties.get(PREVIOUS_ASSIGNEES_PROP);
//			  assignees match {
//				case Some(x) => for (i <- x){
//				  	//DatabaseInterface.addBugTrackerHistory(projectName, obj.getName(), i.getName())
//				}
//				case _ =>
//			  }
//			   
//			}
//			
//			//add a revision
//			case RevisionEntity() => {
//			  var comment = obj.getProperty(REVISION_COMMENT_PROP);
//			  var rev = obj.getProperty(REVISION_PROP);
//			  var dev = obj.getProperty(REVISION_DEVELOPER_PROP);
//			  var date = obj.getProperty(REVISION_DATE_PROP);
//			  
//			  if(comment == None){
//			    comment = "";
//			  }
//			  
//			  if(dev == None){
//			    dev = "";
//			  }
//			  
//			  if(rev == None){
//			    rev = "0";
//			  }
//			  
//			  if(date == None){
//			    date = "";
//			  }
//			  
//			  //DatabaseInterface.addRevision(projectName, comment, Integer.parseInt(rev.toString()), dev, date)
//			}
//			
//			//add a class metric
//			case ClassMetricEntity() => {
//			  var ClassName = "";
//			  var metricName = "";
//			  var value = 0;
//			  var rev = obj.getProperty(REVISION_PROP);
//			  
//			  if(rev == None){
//			    rev = "0";
//			  }
//			  
//			  //DatabaseInterface.addClassMetric(projectName, ClassName, metricName, value, Integer.parseInt(rev.toString()))
//			}
//			
//			//add a class metric
//			case MethodMetricEntity() => {
//			  var methodName = "";
//			  var metricName = "";
//			  var value = 0;
//			  var rev = obj.getProperty(REVISION_PROP);
//			  
//			  if(rev == None){
//			    rev = "0";
//			  }
//			  
//			  //DatabaseInterface.addClassMetric(projectName, methodName, metricName, value, Integer.parseInt(rev.toString()))
//			}
//			
//		    case _ => CONTINUE
//		  }
    return CONTINUE
  }

}
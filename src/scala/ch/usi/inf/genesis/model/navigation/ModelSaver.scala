package ch.usi.inf.genesis.model.navigation

import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import ch.usi.inf.genesis.model.core.FAMIX._
import scala.collection.mutable.HashSet
import ch.usi.inf.genesis.model.core.famix._
import ch.usi.inf.genesis.model.core.StringValue
import ch.usi.inf.databaseinterface.DatabaseInterface



class ModelSaver extends ModelVisitor {
	var projectName : String = "ArgoUML";
	
  def getSelection(): (ModelObject => Boolean)  = {
  	  ((obj) => obj match {
  	    case NamespaceEntity() => true
  	    case _ => false
  	  });
  }

  def visit(obj: ModelObject): NavigatorOption = {
		  //project is already added to the database...
		  obj match  {
		    
			//save packages
		    case NamespaceEntity() => {
		      var parentPackage = obj.getProperty(PARENT_PACKAGE);
		      var owner = obj.getProperty(OWNER_PROP);
		      var rev = obj.getProperty(REVISION_PROP);
		      
		      if(parentPackage == None){
		        parentPackage = Some(new StringValue(""));
		      }
		      
		      if(owner == None){
		        owner = Some(new StringValue("Dev1"));
		      }
		      
		      if(rev == None){
		        rev = Some(new StringValue("0"));
		      }
		      
		      rev match{
		      	case Some(s : StringValue) => parentPackage match{
		      	  	case Some(pp: StringValue) => owner match{
		      	  	  case Some(own: StringValue) =>
		      	  	    DatabaseInterface.addPackage(projectName, obj.getName().replace('\'', ' '), own.value, Integer.parseInt(s.value), pp.value);
		      	  	  case _ =>
		      	  }
		      	  	case _ =>
		      		}
		      	case _ =>
		      }
		    }
		
			//save classes
			case ClassEntity() => {
				var belongsToPackage = obj.getProperty(PARENT_PACKAGE);
				var owner = obj.getProperty(OWNER_PROP);
				var rev = obj.getProperty(REVISION_PROP);
		      
				if(belongsToPackage == None){
					belongsToPackage = Some(new StringValue(""));
				}
		      
				if(owner == None){
					owner = Some(new StringValue(""));
				}
		      
				if(rev == None){
					rev = Some(new StringValue("0"));
				}
				
				rev match{
		      		case Some(s : StringValue) =>
		      			DatabaseInterface.addClass(projectName, belongsToPackage.toString(), obj.getName(), owner.toString(), Integer.parseInt(s.value))
		      		case _ =>
				}
				//now save the inheritance
				var superclasses = obj.getProperties(SUBCLASS_PROP); //on FAMIX.scala it's written "subclassOf"
				superclasses match {
				  case Some(x) => 
				    for(superclass <- x){
				    	rev match{
				    	  case Some(s: StringValue) =>
				    	    DatabaseInterface.addInheritance(projectName, superclass.toString(), obj.getName(), Integer.parseInt(s.value))
				    	  case _ =>
				    	}
					}
				  case _ =>
				}
			}
			
			//save methods
			case MethodEntity() => {
				var belongsToPackage = obj.getProperty(PARENT_PACKAGE);
			    var owner = obj.getProperty(OWNER_PROP);
			    var rev = obj.getProperty(REVISION_PROP);
				var signature = obj.getProperty(SIGNATURE_PROP);
				var modifiers = obj.getProperties(MODIFIERS_PROP);
				var returnType = obj.getProperty(DECLARED_TYPE_PROP);
				var className = obj.getProperty(PARENT_TYPE_PROP);
				
				if(belongsToPackage == None){
					belongsToPackage = Some(new StringValue(""));
				}
		      
				if(owner == None){
					owner = Some(new StringValue(""));
				}
		      
				if(rev == None){
					rev = Some(new StringValue("0"));
				}
				
				if(signature == None){
					signature = Some(new StringValue(""));
				}
				
				if(returnType == None){
				  returnType = Some(new StringValue(""));
				}
				
				if(className == None){
				  className = Some(new StringValue(""));
				}
				
				if(modifiers == None){
				  rev match{
				  	case Some(s: StringValue) =>
				  		DatabaseInterface.addMethod(projectName, className.toString(), obj.getName(), signature.toString(), "", returnType.toString(), owner.toString(), Integer.parseInt(s.value))
				  	case _ =>
				  }
				} else {
				  var modif = "";
				  for(i <- modifiers){
				    modif += i + " ";
				  }
				  
				  rev match{
				  	case Some(s: StringValue) =>
				  		DatabaseInterface.addMethod(projectName, className.toString(), obj.getName(), signature.toString(), modif, returnType.toString(), owner.toString(), Integer.parseInt(s.value))
				  	case _ =>
				  }
				  
				}
			
				//HOW TO ADD METHOD INVOKATIONS? WE ARE NOT SURE IF METHODS ARE ALREADY THERE
				//IN THE DATABASE IN THE FIRST PLACE
			}
			
			//save attributes
			case AttributeEntity() => {
				var belongsToPackage = "";
			    var owner = obj.getProperty(OWNER_PROP);
			    var rev = obj.getProperty(REVISION_PROP);
				var signature = obj.getProperty(SIGNATURE_PROP);
				var modifiers = obj.getProperties(MODIFIERS_PROP);
				var declaredType = obj.getProperty(DECLARED_TYPE_PROP);
				var className = obj.getProperty(PARENT_TYPE_PROP);
				
				if(belongsToPackage == None){
					belongsToPackage = "";
				}
		      
				if(owner == None){
					owner = Some(new StringValue(""));
				}
		      
				if(rev == None){
					rev = Some(new StringValue("0"));
				}
				
				if(signature == None){
					signature = Some(new StringValue(""));
				}
				
				if(declaredType == None){
				  declaredType = Some(new StringValue(""));
				}
				
				if(className == None){
				  className = Some(new StringValue(""));
				}
				
				if(modifiers == None){
				  DatabaseInterface.addMethod(projectName, className.toString(), obj.getName(), signature.toString(), "", declaredType.toString(), owner.toString(), Integer.parseInt(rev.toString()))
				} else {
				  var modif = "";
				  for(i <- modifiers){
				    modif += i + " ";
				  }
				  
				  rev match{
				  	case Some(s: StringValue) =>
				  		DatabaseInterface.addMethod(projectName, className.toString(), obj.getName(), signature.toString(), modif, declaredType.toString(), owner.toString(), Integer.parseInt(s.value))
				  	case _ =>
				  }
				  
				}
				
				rev match{
				  	case Some(s: StringValue) =>
				  		DatabaseInterface.addAttribute(projectName, className.toString(), obj.getName(), signature.toString(), modifiers.toString(), declaredType.toString(), Integer.parseInt(s.value))
				  	case _ =>
				}
			}
			
			//add a developer
			case DeveloperEntity() => {
			  DatabaseInterface.addDeveloper(obj.getName());
			}
			
			//add a bug tracker developer
			case BTDeveloperEntity() => {
			  var btdevEmail = obj.getProperty(BTDEVELOPER_EMAIL_PROP);
			  
			  if(btdevEmail == None){
			    btdevEmail = Some(new StringValue(""));
			  }
			  
			  DatabaseInterface.addBugTrackerDeveloper(obj.getName(), btdevEmail.toString());
			}
			
			//add a bug
			case BugEntity() => {
			  var desc =  obj.getProperty(BUG_DESCRIPTION_PROP);
			  var status = obj.getProperty(BUG_STATUS_PROP);
			  var assignee = obj.getProperty(BUG_ASSIGNEE_PROP);
			  
			  if(desc == None){
			    desc = Some(new StringValue(""));
			  }
			  
			  if(status == None){
			    status = Some(new StringValue(""));
			  }
			  
			  if(assignee == None){
			    assignee = Some(new StringValue(""));
			  }
			  
			  DatabaseInterface.addBug(projectName, obj.getName(), desc.toString(), status.toString(), assignee.toString())
			  
			  //add history for a bug
			  var assignees = obj.properties.get(PREVIOUS_ASSIGNEES_PROP);
			  assignees match {
				case Some(x) => for (i <- x){
				  	DatabaseInterface.addBugTrackerHistory(projectName, obj.getName(), i.getName())
				}
				case _ =>
			  }
			   
			}
			
			//add a revision
			case RevisionEntity() => {
			  var comment = obj.getProperty(REVISION_COMMENT_PROP);
			  var rev = obj.getProperty(REVISION_PROP);
			  var dev = obj.getProperty(REVISION_DEVELOPER_PROP);
			  var date = obj.getProperty(REVISION_DATE_PROP);
			  
			  if(comment == None){
			    comment = Some(new StringValue(""));
			  }
			  
			  if(dev == None){
			    dev = Some(new StringValue(""));
			  }
			  
			  if(rev == None){
			    rev = Some(new StringValue("0"));
			  }
			  
			  if(date == None){
			    date = Some(new StringValue(""));
			  }
			  
			  rev match{
				  	case Some(s: StringValue) =>
				  		DatabaseInterface.addRevision(projectName, comment.toString(), Integer.parseInt(s.value), dev.toString(), date.toString())
				  	case _ =>
			  }
			}
			
			//FOR NOW THE FOLLOWING TWO CLASSES ARE LIKE THIS, THEY HAVE TO BE FIXED AS SOON AS WE HAVE METRICS TO FIT WITH!!
			
			//add a class metric
			case ClassMetricEntity() => {
			  var ClassName = "";
			  var metricName = "";
			  var value = 0;
			  var rev = obj.getProperty(REVISION_PROP);
			  
			  if(rev == None){
			    rev = Some(new StringValue("0"));
			  }
			  
			  rev match{
			  	case Some(s: StringValue) =>
			  		DatabaseInterface.addClassMetric(projectName, ClassName, metricName, value, Integer.parseInt(s.value))
				case _ =>
			  }
			}
			
			//add a class metric
			case MethodMetricEntity() => {
			  var methodName = "";
			  var metricName = "";
			  var value = 0;
			  var rev = obj.getProperty(REVISION_PROP);
			  
			  if(rev == None){
			    rev = Some(new StringValue(""));
			  }
			  
			  rev match{
			  	case Some(s: StringValue) =>
			  		DatabaseInterface.addClassMetric(projectName, methodName, metricName, value, Integer.parseInt(s.value))
				case _ =>
			  }
			}
			
		    case _ => CONTINUE
		  }
    return CONTINUE
  }

}
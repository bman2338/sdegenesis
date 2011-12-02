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
		      		case Some(s : StringValue) => belongsToPackage match{
		      	  		case Some(btp: StringValue) => owner match{
		      	  			case Some(own: StringValue) =>
		      	  				DatabaseInterface.addClass(projectName, btp.value, obj.getName().replace('\'', ' '), own.value, Integer.parseInt(s.value))
		      	  			case _ =>
		      	  		}
		      	  		case _ =>
		      		}
		      		case _ =>
				}
				//now save the inheritance
				var superclasses = obj.getProperties(SUBCLASS_PROP); //on FAMIX.scala it's written "subclassOf"
				superclasses match {
				  case Some(x) => 
				    for(superclass <- x){
				    	rev match{
				    	  case Some(s: StringValue) => belongsToPackage match{
		      	  			case Some(spr: StringValue) => 
		      	  				DatabaseInterface.addInheritance(projectName, spr.value, obj.getName().replace('\'', ' '), Integer.parseInt(s.value))
		      	  			case _ =>
				    	  }
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
				
				  rev match{
				  	case Some(r: StringValue) => owner match{
				  	  case Some(own: StringValue) => signature match {
				  	    case Some(sig: StringValue) => returnType match {
				  	      case Some(rtyp: StringValue) => className match {
				  	        case Some(cname: StringValue) => modifiers match {
				  	          //if none, modifiers given as ""
				  	          case None => DatabaseInterface.addMethod(projectName, cname.value, obj.getName().replace('\'', ' '), sig.value, "", rtyp.value, own.value, Integer.parseInt(r.value))
				  	          case _ =>
				  	            //otherwise if it contains something, build a string and pass it
				  	            var modif = "";
				  	            for(i <- modifiers){
				  	            	modif += i + " ";
				  	            }
				  	            DatabaseInterface.addMethod(projectName, cname.value, obj.getName().replace('\'', ' '), sig.value, modif, rtyp.value, own.value, Integer.parseInt(r.value))
				  	        }
				  	        case _ =>
				  	      }
				  	      case _ =>
				  	    }
				  	    case _ =>
				  	  }
				  	  case _ =>
				  	}
				  	case _ =>
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
				
				rev match{
				  	case Some(r: StringValue) => owner match{
				  	  case Some(own: StringValue) => signature match {
				  	    case Some(sig: StringValue) => declaredType match {
				  	      case Some(dtyp: StringValue) => className match {
				  	        case Some(cname: StringValue) => modifiers match {
				  	          //if none, modifiers given as ""
				  	          case None => DatabaseInterface.addAttribute(projectName, cname.value, obj.getName().replace('\'', ' '), sig.value, "", dtyp.value, Integer.parseInt(r.value))
				  	          case _ =>
				  	            //otherwise if it contains something, build a string and pass it
				  	            var modif = "";
				  	            for(i <- modifiers){
				  	            	modif += i + " ";
				  	            }
				  	            DatabaseInterface.addAttribute(projectName, cname.value, obj.getName().replace('\'', ' '), sig.value, modif, dtyp.value, Integer.parseInt(r.value))
				  	        }
				  	        case _ =>
				  	      }
				  	      case _ =>
				  	    }
				  	    case _ =>
				  	  }
				  	  case _ =>
				  	}
				  	case _ =>
				  }
			}
			
			//add a developer
			case DeveloperEntity() => {
			  DatabaseInterface.addDeveloper(obj.getName().replace('\'', ' '));
			}
			
			//add a bug tracker developer
			case BTDeveloperEntity() => {
			  var btdevEmail = obj.getProperty(BTDEVELOPER_EMAIL_PROP);
			  
			  if(btdevEmail == None){
			    btdevEmail = Some(new StringValue(""));
			  }
			  btdevEmail match {
			    case Some(btde: StringValue) =>
			      DatabaseInterface.addBugTrackerDeveloper(obj.getName().replace('\'', ' '), btde.value);
			    case _ =>
			  }
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
			  
			  desc match{
			    case Some(d: StringValue) => status match{
			      case Some(stat: StringValue) => assignee match {
			        case Some(assign: StringValue) => 
			          DatabaseInterface.addBug(projectName, obj.getName().replace('\'', ' '), d.value, stat.value, assign.value)
			        case _ =>
			      }
			      case _ =>
			    }
			    case _ =>
			  }
			  
			  //add history for a bug
			  var assignees = obj.properties.get(PREVIOUS_ASSIGNEES_PROP);
			  assignees match {
				case Some(x) => for (i <- x){
				  	DatabaseInterface.addBugTrackerHistory(projectName, obj.getName().replace('\'', ' '), i.getName().replace('\'', ' '))
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
			  	case Some(s: StringValue) => comment match {
			  	  case Some(comm: StringValue) => dev match {
			  	    case Some(d: StringValue) => date match {
			  	      case Some(dat: StringValue) =>
			  	        DatabaseInterface.addRevision(projectName, comment.toString(), Integer.parseInt(s.value), d.value, dat.value)
			  	      case _ =>
			  	    }
			  	    case _ =>
			  	  }
			  	  case _ =>
			  	}
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
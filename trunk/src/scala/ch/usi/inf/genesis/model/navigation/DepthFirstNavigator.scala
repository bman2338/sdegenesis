package ch.usi.inf.genesis.model.navigation

import ch.usi.inf.genesis.model.core.FAMIX._
import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.core.Project
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import scala.collection.mutable.HashSet


//Do no use it (strange bug), does not visit all entitites
class DepthFirstNavigator extends Navigator {
	protected override def walk(modelObject: ModelObject, visitor: ModelVisitor, selection: Option[HashSet[String]]) : NavigatorOption = {
			val visited: HashSet[Int] = new HashSet();
			selection match {
				case Some(selection) => walkAux(modelObject, visitor, visited, true, selection);
				case None => walkAux(modelObject, visitor, visited);
			}
	}

	private def walkAux(modelObject: ModelObject, visitor: ModelVisitor, visited: HashSet[Int], visit: Boolean, selection: HashSet[String]) : NavigatorOption = {  
			visited.add(modelObject.getId());  

			var opt = CONTINUE;
			if(visit && !hasToIgnore(modelObject)) {
				opt = modelObject.accept(visitor);
			}

			opt match {
			case CONTINUE =>  {
				modelObject.properties.foreach((pair) => {
					val list = pair._2;
					val visitChild : Boolean = selection.contains(pair._1);
					
					
					
					list.foreach(child => {
					 val skip = visited.contains(child.getId());
					 if(!skip) {
						val opt = walkAux(child, visitor, visited, visitChild, selection);
						opt match {
							case CONTINUE => CONTINUE
							case STOP => return STOP
							case _ => CONTINUE
							}
						}
					}) 
				})
				return CONTINUE   
			}
			case SKIP_SUBTREE => return CONTINUE
			case STOP => return STOP
			
			}

			return CONTINUE
	}
	
	
	

	private def walkAux(modelObject: ModelObject, visitor: ModelVisitor, visited: HashSet[Int]) : NavigatorOption = {
			visited.add(modelObject.getId());  

			var opt = CONTINUE;
			if(!hasToIgnore(modelObject)) {
				opt = modelObject.accept(visitor);
			}

			//the model Object will call visit on the visitor 
			opt match {
			case CONTINUE =>  {
				modelObject.properties.foreach((pair) => {
					val list = pair._2;
					list.foreach(child => {
						if(!visited.contains(child.getId())) {
							walkAux(child, visitor, visited) match {
							case CONTINUE =>
							case STOP => return STOP
							}
						}
					}) 
				})
				return CONTINUE   
			}
			case SKIP_SUBTREE => return CONTINUE
			case STOP => return STOP

			}

			return CONTINUE
	}


}
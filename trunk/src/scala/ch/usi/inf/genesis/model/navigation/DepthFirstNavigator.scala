package ch.usi.inf.genesis.model.navigation

import ch.usi.inf.genesis.model.core.FAMIX._
import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.core.Project
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import scala.collection.mutable.HashSet


//Do no use it (strange bug), does not visit all entitites
class DepthFirstNavigator extends Navigator {
	protected override def walk(modelObject: ModelObject, visitor: ModelVisitor, selectionFunction: Option[ModelObject => Boolean]) : NavigatorOption = {
			val visited: HashSet[Int] = new HashSet();
			selectionFunction match {
				case Some(func) => walkAux(modelObject, visitor, visited, true, func);
				case None => walkAux(modelObject, visitor, visited, true, ((obj) => true));
			}
	}

	private def walkAux(modelObject: ModelObject, visitor: ModelVisitor, visited: HashSet[Int], visit: Boolean, selectionFunction: ModelObject => Boolean) : NavigatorOption = {  
			visited.add(modelObject.getId());  

			var opt = CONTINUE;
			if(visit && !hasToIgnore(modelObject)) {
				opt = modelObject.accept(visitor);
			}

			opt match {
			case CONTINUE =>  {
			  	val visitChild : Boolean = selectionFunction(modelObject);
				modelObject.properties.foreach((pair) => {
					val list = pair._2;

					list.foreach(child => {
					 val skip = visited.contains(child.getId());
					 if(!skip) {
						val opt = walkAux(child, visitor, visited, visitChild, selectionFunction);
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
}
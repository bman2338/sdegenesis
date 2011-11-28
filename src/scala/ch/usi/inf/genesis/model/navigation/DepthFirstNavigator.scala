package ch.usi.inf.genesis.model.navigation

import ch.usi.inf.genesis.model.core.FAMIX._
import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.core.Project
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import scala.collection.mutable.HashSet

class DepthFirstNavigator extends Navigator {
	protected override def walk(modelObject: ModelObject, visitor: ModelVisitor, selection: Option[HashSet[String]]) : NavigatorOption = {
			val visited: HashSet[Int] = new HashSet();
	selection match {
	case Some(selection) => walkAux(modelObject, visitor, visited, false, selection);
	case None => walkAux(modelObject, visitor, visited);
	}
	}

	private def walkAux(modelObject: ModelObject, visitor: ModelVisitor, visited: HashSet[Int], visit: Boolean, selection: HashSet[String]) : NavigatorOption = {
			visited.add(modelObject.getId());  
			//the model Object will call visit on the visitor 

			var opt = CONTINUE;
			if(visit && !IGNORE_TYPE.equals(modelObject.getName())) {
				opt = modelObject.accept(visitor);
			}

			opt match {
			case CONTINUE =>  {
				modelObject.properties.foreach((pair) => {
					val list = pair._2;
					val visitChild = selection.contains(pair._1);
					list.foreach(child => {
						if(!visited.contains(child.getId())) {
							walkAux(child, visitor, visited, visitChild, selection) match {
							case CONTINUE => CONTINUE
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

			CONTINUE
	}

	private def walkAux(modelObject: ModelObject, visitor: ModelVisitor, visited: HashSet[Int]) : NavigatorOption = {
			visited.add(modelObject.getId());  

			var opt = CONTINUE;
			if(!IGNORE_TYPE.equals(modelObject.getName())) {
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
							case STOP => return CONTINUE
							}
						}
					}) 
				})
				return CONTINUE   
			}
			case SKIP_SUBTREE => return CONTINUE
			case STOP => return STOP

			}

			CONTINUE
	}


}
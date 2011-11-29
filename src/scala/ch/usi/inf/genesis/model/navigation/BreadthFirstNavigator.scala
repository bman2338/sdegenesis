package ch.usi.inf.genesis.model.navigation

import ch.usi.inf.genesis.model.core.FAMIX._
import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import scala.collection.mutable.Queue
import scala.collection.mutable.HashSet
import ch.usi.inf.genesis.model.core.IdFactory

class BreadthFirstNavigator extends Navigator {

	protected override def walk(modelObject: ModelObject, visitor: ModelVisitor, selectionFunction: Option[ModelObject => Boolean]): NavigatorOption = {
			val visited: HashSet[Int] = new HashSet();
			selectionFunction match {
				case Some(func) => walkAux(modelObject, visitor, visited, func)
				case None => walkAux(modelObject, visitor, visited, ((obj) => true))
			}
	}

	private def walkAux(modelObject: ModelObject, visitor: ModelVisitor, visited: HashSet[Int], selectionFunction: ModelObject => Boolean) : NavigatorOption = 
	{

			visited.add(modelObject.getId());

			var opt = CONTINUE;
			if(!hasToIgnore(modelObject)) {
				opt = modelObject.accept(visitor);
			}

			opt match {
			case STOP => return STOP;
			case SKIP_SUBTREE => return STOP;
			case CONTINUE =>
			}

			val queue = new Queue[ModelObject]();
			queue.enqueue(modelObject);

			while (!queue.isEmpty) {
				val current = queue.dequeue();
				var skip = false;
				if(!selectionFunction(current)) 
					  skip = true;
				current.properties.foreach(pair => {
					val list = pair._2;
					list.foreach(child => {
						if(!visited.contains(child.getId())) {
						  visited.add(child.getId());
							
						  if(skip) {
								queue.enqueue(child);
							} else {
								opt = CONTINUE;
								if(!hasToIgnore(child)) {
									opt = child.accept(visitor);
								}	
								
								opt match {
								case CONTINUE => queue.enqueue(child);
								case STOP => return STOP;
								case SKIP_SUBTREE =>
								}
							} 
						}
					})
				})
			}
			STOP
		}
}
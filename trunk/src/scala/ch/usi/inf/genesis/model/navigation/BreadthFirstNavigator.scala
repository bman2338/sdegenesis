package ch.usi.inf.genesis.model.navigation

import ch.usi.inf.genesis.model.core.FAMIX._
import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import scala.collection.mutable.Queue
import scala.collection.mutable.HashSet
import ch.usi.inf.genesis.model.core.IdFactory

class BreadthFirstNavigator extends Navigator {

	protected override def walk(modelObject: ModelObject, visitor: ModelVisitor, selection: Option[HashSet[String]]): NavigatorOption = {
			val visited: HashSet[Int] = new HashSet();
			selection match {
				case Some(selection) => walkAux(modelObject, visitor, visited, selection)
				case None => walkAux(modelObject, visitor, visited)
			}
	}

	private def walkAux(modelObject: ModelObject, visitor: ModelVisitor, visited: HashSet[Int], selection: HashSet[String]) : NavigatorOption = 
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
				current.properties.foreach(pair => {
					var skip = false;
					if(!selection.contains(pair._1)) 
					  skip = true;

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

	private def walkAux(modelObject: ModelObject, visitor: ModelVisitor, visited: HashSet[Int]) : NavigatorOption = {

			visited.add(modelObject.getId());

			var opt = CONTINUE;
			if(!hasToIgnore(modelObject)) {
				opt = modelObject.accept(visitor);
			}

			opt match {
			case STOP => return STOP
			case SKIP_SUBTREE => return STOP
			case CONTINUE =>
			}

			val queue = new Queue[ModelObject]()
					queue.enqueue(modelObject);

			while (!queue.isEmpty) {
				val current = queue.dequeue();
				
				current.properties.foreach(pair => {
					val list = pair._2;
					list.foreach(child => {
						if(!visited.contains(child.getId())) {
							visited.add(current.getId());
							opt = CONTINUE;
							if(!hasToIgnore(child)) {
								opt = child.accept(visitor);
							}			
							opt  match {
							case CONTINUE => queue.enqueue(child);
							case STOP => return STOP;
							case SKIP_SUBTREE =>
							} 
						}
					})
				})
			}
			STOP
	}

}
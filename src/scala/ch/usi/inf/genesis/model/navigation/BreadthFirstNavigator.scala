package ch.usi.inf.genesis.model.navigation

import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import scala.collection.mutable.Queue
import scala.collection.mutable.HashSet
import ch.usi.inf.genesis.model.core.IdFactory

class BreadthFirstNavigator extends Navigator {

	protected def walk(modelObject: ModelObject, visitor: ModelVisitor, selection: Option[HashSet[String]]): NavigatorOption = {
		val visited: HashSet[Int] = new HashSet();
		selection match {
			case Some(selection) => walkAux(modelObject, visitor, visited, selection)
			case None => walkAux(modelObject, visitor, visited)
		}
	}

	private def walkAux(modelObject: ModelObject, visitor: ModelVisitor, visited: HashSet[Int], selection: HashSet[String]) : NavigatorOption = 
		{

			visited.add(modelObject.getId());
			modelObject.accept(visitor) match {
			case STOP => return STOP
			case SKIP_SUBTREE => return STOP
			case CONTINUE =>
			}

			val queue = new Queue[ModelObject]()
					queue.enqueue(modelObject);

			while (!queue.isEmpty) {
				val current = queue.dequeue();
				visited.add(current.getId());

				current.properties.foreach(pair => {
					var skip = false;
					if(!selection.contains(pair._1)) skip = true;


					val list = pair._2;
					list.foreach(child => {
						if(!visited.contains(child.getId())) {
							if(skip) {
								queue.enqueue(child)
							} else 
								child.accept(visitor) match {
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

	private def walkAux(modelObject: ModelObject, visitor: ModelVisitor, visited: HashSet[Int]) : NavigatorOption = {

			visited.add(modelObject.getId());
			modelObject.accept(visitor) match {
			case STOP => return STOP
			case SKIP_SUBTREE => return STOP
			case CONTINUE =>
			}

			val queue = new Queue[ModelObject]()
					queue.enqueue(modelObject);

			while (!queue.isEmpty) {
				val current = queue.dequeue();
				visited.add(current.getId());
				current.properties.foreach(pair => {
					val list = pair._2;
					list.foreach(child => {
						if(!visited.contains(child.getId())) {
							child.accept(visitor) match {
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
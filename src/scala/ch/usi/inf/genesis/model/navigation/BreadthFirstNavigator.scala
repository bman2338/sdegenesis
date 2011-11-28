package ch.usi.inf.genesis.model.navigation

import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import scala.collection.mutable.Queue
import scala.collection.mutable.HashSet

class BreadthFirstNavigator extends Navigator {

	protected def walk(modelObject: ModelObject, visitor: ModelVisitor, selection: Option[HashSet[String]]): NavigatorOption = {



			modelObject.accept(visitor) match {
			case STOP => return STOP
			case SKIP_SUBTREE => return STOP
			case CONTINUE =>
			}

			val queue = new Queue[ModelObject]()
					queue.enqueue(modelObject);

			while (!queue.isEmpty) {
				val current = queue.dequeue();
				current.properties.foreach(pair => {
					var skip = false;
					selection match {
					case Some(set) => if(!set.contains(pair._1)) skip = true;
					case None => 
					}

					val list = pair._2;
					list.foreach(child => {
						if(skip) {
							queue.enqueue(child)
						} else 
							child.accept(visitor) match {
							case CONTINUE => queue.enqueue(child);
							case STOP => return STOP;
							case SKIP_SUBTREE =>
						} 
					})
				})
			}
			STOP
	}
	
	
	def walkAux(modelObject: ModelObject, visitor: ModelVisitor) : NavigatorOption = {
	  STOP
	}

}
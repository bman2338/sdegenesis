package ch.usi.inf.genesis.model.mutators

import ch.usi.inf.genesis.model.core.ModelObject
import ch.usi.inf.genesis.model.navigation.BreadthFirstNavigator
import ch.usi.inf.genesis.model.navigation.ModelVisitor;
/**
 * @author Patrick Zulian
 */

abstract class ModelMutator extends ModelVisitor {

      def mutate(modelObject: ModelObject) : Unit = {
         new BreadthFirstNavigator().walkModel( modelObject, this, Some( getSelection()) );

      }



}

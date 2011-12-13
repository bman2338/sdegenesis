package ch.usi.inf.genesis.model

import ch.usi.inf.genesis.parser.mse.MSEParser
import core.famix.RevisionEntityProperty
import core.{ModelObject, StringValue, FAMIX, IntValue}
import collection.mutable.ListBuffer
import mutators.{InvocationMutator, ModelMutator, UniqueIdMutator}
import scala.ch.usi.inf.genesis.model.mutators.{BasicMetricsMutator, TypeMutator}

/**
 * @author Remo Lemma
 */


class ModelGenerator(val name: String) {

  var mutators = new ListBuffer[ModelMutator]

  // Default Mutators
  mutators += new UniqueIdMutator
  mutators += new TypeMutator
  mutators += new InvocationMutator
  mutators += new BasicMetricsMutator

  def generateFromFile(path: String) = {
    throw new RuntimeException("Please come back later, not available right now.")
  }

  def generateFromString(str: String): Option[ModelObject] = {
    var root = MSEParser.parse(str)
    root match {
      case Some(node) =>
        node.addProperty(FAMIX.NAME_PROP, new StringValue(name));
        //node.addProperty(RevisionEntityProperty.NUMBER, new IntValue(revision))
        node.setUniqueId(name);

        mutators foreach ((mutator) => mutator.mutate(node))

        Some(node)
      case None => None
    }
  }

}
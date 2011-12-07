package ch.usi.inf.genesis.model

import ch.usi.inf.genesis.parser.mse.MSEParser
import core.{ModelObject, StringValue, FAMIX}
import extractors.InvocationExtractorFactory
import mutators.{ModelMutator, UniqueIdMutator}
import collection.mutable.ListBuffer
import scala.ch.usi.inf.genesis.model.mutators.{BasicMetricsMutator, TypeMutator}

/**
 * @author Remo Lemma
 */


class ModelGenerator(val name:String) {

  var mutators = new ListBuffer[ModelMutator]

  // Default Mutators
  mutators += new UniqueIdMutator
  mutators += new TypeMutator
  mutators += new BasicMetricsMutator

  def generateFromFile (path:String) = {
    throw new RuntimeException("Please come back later, not available right now.")
  }

  def generateFromString (str:String) : Option[ModelObject] = {
    var root = MSEParser.parse(str)
    root match {
      case Some(node) =>
        node.addProperty(FAMIX.NAME_PROP, new StringValue(name));
        node.setUniqueId(name);

        mutators foreach ((mutator) => mutator.mutate(node))

        // TODO Convert to Mutator
        InvocationExtractorFactory.getSimpleInvocationExtractor().extract(node)
        Some(node)
      case None => None
    }
  }

}
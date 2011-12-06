package ch.usi.inf.genesis.model

import ch.usi.inf.genesis.parser.mse.MSEParser
import core.{ModelObject, StringValue, FAMIX}
import extractors.InvocationExtractorFactory
import mutators.UniqueIdMutator

/**
 * @author Remo Lemma
 */


class ModelGenerator(val name:String) {

  def generateFromFile (path:String) = {
    throw new RuntimeException("Please come back later, not available right now.")
  }

  def generateFromString (str:String) : Option[ModelObject] = {
    var root = MSEParser.parse(str)
    root match {
      case Some(node) =>
        node.addProperty(FAMIX.NAME_PROP, new StringValue(name));
        node.setUniqueId(name);
        new UniqueIdMutator().mutate(node);
        InvocationExtractorFactory.getSimpleInvocationExtractor().extract(node)
        Some(node)
      case None => None
    }
  }

}
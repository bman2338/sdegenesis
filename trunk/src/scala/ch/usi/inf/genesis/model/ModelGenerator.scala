package ch.usi.inf.genesis.model

import ch.usi.inf.genesis.parser.mse.MSEParser
import core.{StringValue, FAMIX}
import extractors.InvocationExtractorFactory

/**
 * @author Remo Lemma
 */


class ModelGenerator(val name:String) {

  def generateFromFile (path:String) = {
    throw RuntimeException("Please come back later, not available right now.")
  }

  def generateFromString (str:String) = {
    var root = MSEParser.parse(str)
    root match {
      case Some(node) =>
        node.addProperty(FAMIX.NAME_PROP, new StringValue(name));
        InvocationExtractorFactory.getSimpleInvocationExtractor().extract(root)
        node
      case None => None
    }
  }

}
package ch.usi.inf.genesis.model.core

//import ch.usi.inf.genesis.model.core.{DoubleValue, IntValue, StringValue, BooleanValue}

/**
 * @author Remo Lemma
 */

object ModelType {

  def isValue (obj: ModelObject) : Boolean = {
    obj match {
      case BooleanValue(_) | StringValue(_) | IntValue(_) | DoubleValue(_) => true
      case _ => false
    }
  }

}
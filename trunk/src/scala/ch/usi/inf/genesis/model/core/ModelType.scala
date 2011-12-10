package ch.usi.inf.genesis.model.core

import scala.ch.usi.inf.genesis.model.core.Metric

/**
 * @author Remo Lemma
 */

object ModelType {

  def isValue (obj: ModelObject) : Boolean = {
    obj match {
      case BooleanValue(_) | StringValue(_) | IntValue(_) | DoubleValue(_) | Metric() => true
      case _ => false
    }
  }

  def isMetric (obj: ModelObject) : Boolean = {
    obj match {
      case Metric() => true
      case _ => false
    }
  }


  def isIgnoredType(obj: ModelObject) : Boolean = {
      obj.getName().toString().startsWith(FAMIX.IGNORE_TYPE);
  }

  def isStub(obj: ModelObject) : Boolean = {
     val isStub = obj.getProperty(FAMIX.ISSTUB_PROP);
     isStub match {
      case Some(value:BooleanValue) => true
      case Some(value:StringValue) => value.value.equalsIgnoreCase("true")
      case _ => false
    }
  }
}
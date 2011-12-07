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
}
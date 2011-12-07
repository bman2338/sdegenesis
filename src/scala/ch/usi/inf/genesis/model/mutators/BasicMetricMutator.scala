package scala.ch.usi.inf.genesis.model.mutators

import ch.usi.inf.genesis.model.mutators.ModelMutator
import collection.mutable.ListBuffer
import ch.usi.inf.genesis.model.navigation.{BreadthFirstNavigator, NavigatorOption}
import ch.usi.inf.genesis.model.core.famix.{ClassEntity, MethodEntity, NamespaceEntity}
import scala.ch.usi.inf.genesis.model.core.Metric
import ch.usi.inf.genesis.model.core.{IntValue, FAMIX, Value, ModelObject}

/**
 * @author Remo Lemma
 */


class BasicMetricsMutator extends ModelMutator {

  override def mutate(modelObject: ModelObject): Unit = {
    val navigator = new BreadthFirstNavigator
    navigator.walkModel(modelObject, this, Some(((element: ModelObject) => element match {
      case MethodEntity() => true
      case _ => false
    })))
    navigator.walkModel(modelObject, this, Some(((element: ModelObject) => element match {
      case ClassEntity() => true
      case _ => false
    })))
    navigator.walkModel(modelObject, this, Some(((element: ModelObject) => element match {
      case NamespaceEntity() => true
      case _ => false
    })))
  }

  def sizeCalculator(obj: ModelObject, metric: ModelObject): Unit = {
    var metricName = ""
    var propName = ""
    obj match {
      case NamespaceEntity() =>
        metricName = "numberOfClasses"
        propName = FAMIX.CLASSES_PROP
      case ClassEntity() =>
        metricName = "numberOfMethods"
        propName = FAMIX.METHODS_PROP
      case _ => return
    }
    obj.getProperties(propName) match {
      case Some(list) => metric.addProperty(metricName, new IntValue(list.length));
      case None =>
    }
  }

  def inheritanceSubclassesDepthCalculator(obj: ModelObject, metric: ModelObject): Unit = {

    def walkAux(obj: ModelObject, counter: Int): Unit = {
      var nodeValue = counter
      var metric = obj.getPropertyOrAdd(FAMIX.METRICS_PROP, new Metric())
      if (counter > 0) {
        metric.getPropertyOrAdd("subclassesMaxDepth", new IntValue(counter)) match {
          case intValue@IntValue(value) if (counter > value) =>
            intValue.value = counter
            nodeValue = counter
          case _ =>
        }
      }
      obj.getProperties(FAMIX.SUPERCLASS_PROP) match {
        case None =>
        case Some(list) => list foreach ((superclass) => {
          walkAux(superclass, nodeValue + 1);
        })
      }
    }

    obj.getProperties(FAMIX.SUBCLASS_PROP) match {
      case None =>
        walkAux(obj,0)
      case _ =>
    }
  }

  private def all = true

  private def onlyClasses(obj: ModelObject) = {
    obj match {
      case ClassEntity() => true
      case _ => false
    }
  }

  private def noMethods(obj: ModelObject) = {
    obj match {
      case MethodEntity() => false
      case _ => true
    }
  }

  var metrics = new ListBuffer[MetricFunction]
  metrics += new MetricFunction(noMethods, sizeCalculator)
  metrics += new MetricFunction(onlyClasses, inheritanceSubclassesDepthCalculator)

  def visit(obj: ModelObject): NavigatorOption.NavigatorOption = {

    var metric: ModelObject = obj.getPropertyOrAdd(FAMIX.METRICS_PROP, new Metric())

    metrics foreach (function => {
      if (function.selector(obj))
        function.executer(obj, metric)
    })

    if (metric.properties.isEmpty)
      obj.properties.remove(FAMIX.METRICS_PROP)

    NavigatorOption.SKIP_SUBTREE
  }

  class MetricFunction(val selector: (ModelObject => Boolean), val executer: ((ModelObject, ModelObject) => Unit))

}
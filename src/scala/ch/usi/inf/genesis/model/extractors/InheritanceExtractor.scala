package ch.usi.inf.genesis.model.extractors

import ch.usi.inf.genesis.model.core.ModelObject
import scala.collection.mutable.HashSet
import ch.usi.inf.genesis.model.core.FAMIX._
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import ch.usi.inf.genesis.model.navigation.BreadthFirstNavigator
import scala.collection.mutable.HashMap
import ch.usi.inf.genesis.model.core.famix.ClassEntity

class InheritanceExtractor(val classes: Option[HashSet[String]] = None) extends Extractor {
  var str: String = "";
  var analysis: InheritanceAnalysis = null;


  override def getSelection() = {
    (obj) =>
      obj match {
        case ClassEntity() => true
        case _ => false
      }
  }

  def extract(model: ModelObject): Analysis = {
    analysis = new InheritanceAnalysis();
    analysis.title = model.getName()
    new BreadthFirstNavigator().walkModel(model, this, Some(getSelection()));
    analysis.clean();
    return analysis;
  }

  def visit(obj: ModelObject): NavigatorOption = {
    val name = obj.getName();
    name match {
      case "" =>
      case _ => classes match {
        case Some(classes) if (classes.contains(name)) => analysis.addClass(obj);
        case None => analysis.addClass(obj);
      }
    }

    return analysis.opt();
  }

}


class InheritanceAnalysis extends Analysis {
  val classes: HashMap[Int, ModelObject] = new HashMap();
  var title = "";
  val nameOpenStr = "{ \"name\": \"";
  val nameCloseStr = "\"},\n";
  val childrenOpenStr = "\"children\": [";
  val childrenCloseStr = "]},\n";

  override def toString() = {
    toJSON()
  }

  def opt(): NavigatorOption = {
    return CONTINUE;
  }

  def toJSON(): String = {
    var str = "";
    if (classes.size == 1)
      str = "function " + title + "_data() { var json = ";
    else
      str = "function " + title + "_data() { var json = { \"name\": \"" + title + "\", \"children\": [\n";
    classes.foreach(pair => {
      val clazz = pair._2;
      str += toJSON(clazz);
    })

    if (classes.size == 1) {
      str = str.substring(0, str.length() - 1)
      str += "; return json; }";
    }
    else
      str += "]}; return json; }";
    //str = str.replace("'", "");
    return str;
  }

  private def toJSON(modelObject: ModelObject): String = {
    val name = modelObject.getName();
    var str = "";

    name match {
      case "" => return ""
      case _ => {
        str += nameOpenStr + name;

        modelObject.getProperties(SUPERCLASS_PROP) match {
          case None => str += nameCloseStr;
          case Some(subclasses) => {
            str += "\"," + childrenOpenStr;

            subclasses.foreach(subclass => {
              str += toJSON(subclass);
            });

            str += childrenCloseStr;
          }
        }
      }
    }
    return str;
  }

  /**
   * Removes all children classes from root
   */
  def clean() = {
    classes.foreach(pair => {
      val clazz = pair._2;
      clazz.properties.get(SUPERCLASS_PROP) match {
        case None =>
        case Some(subclasses) => {
          subclasses.foreach(subclass => {
            classes.remove(subclass.getId());
          });
        }
      }
    });
  }

  def addClass(clazz: ModelObject) = {
    classes.put(clazz.getId(), clazz);
  }


}
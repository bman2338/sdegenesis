package ch.usi.inf.genesis.model.mutators

import ch.usi.inf.genesis.model.core.famix.ClassEntity
import scala.io._
import java.io.File
import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import ch.usi.inf.genesis.model.core.{FAMIX, IntValue, StringValue, ModelObject}
import ch.usi.inf.genesis.model.core.Metric


/**
 * @author Luca Ponzanelli
 * @param projectPath The path to the repository from which files can be loaded.
 *
 * It calculates the LOC for an entire class and adds the LOC property to the class Entity.
 */
class ClassLocMutator(projectPath : String) extends ModelMutator {
  override def visit(obj : ModelObject): NavigatorOption = {
    obj.getProperty(FAMIX.SOURCE_ANCHOR) match {
				case Some(fileAnchor : ModelObject) =>
          fileAnchor.getProperty(FAMIX.SOURCE_FILE_NAME) match {
            case Some(fileName : StringValue) =>
            fileAnchor.getProperty(FAMIX.SOURCE_START_LINE) match{
                case Some(startLine : IntValue) =>
                  fileAnchor.getProperty(FAMIX.SOURCE_END_LINE) match{
                    case Some(endLine : IntValue) =>
                      val loc = getLOC(fileName.value, startLine.value, endLine.value)

                      val metric = obj.getPropertyOrAdd(FAMIX.METRICS_PROP, new Metric())
                      metric.addProperty("loc", new IntValue(loc))
                      STOP
                    case _ => CONTINUE
                  }
                case _ => CONTINUE
            }
            case _ => CONTINUE
          }
        case _ =>  CONTINUE
    }
  }

  /**
   * Just selects Class Entity as entities to analyze
   */
  override def getSelection() : (ModelObject) => Boolean ={
    ((obj) => obj match {
      case ClassEntity() => true
      case _ => false
    })
  }

  /**
   *   Read an entire file from the repository and strips out JAVA/C like comments
   *   to calculate the right LOC.
   */
  private def getLOC(fileName : String, startLine : Int,  endLine : Int) : Int = {
    val file = new File(projectPath + File.separator + fileName)

    val source = Source.fromFile(file)
		var content = source.mkString
    source.close()
		var loc = 0

    //remove C/C++/JAVA comments
    content = content.replaceAll("(/\\*([^*]|[\\r\\n]|(\\*+([^*/]|[\\r\\n])))*\\*+/)|(//.*)","")
    val lines : Array[String] = content.split("[\n|\r\n]")
    var lineCount = 1;
    lines foreach( (l) => {
      if(lineCount <= endLine && lineCount >= startLine && l.length() > 0) loc+=1

      lineCount+=1
    })

    loc
  }
}
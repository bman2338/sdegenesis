package ch.usi.inf.genesis.model.mutators

import ch.usi.inf.genesis.model.core.famix.ClassEntity
import scala.io._
import java.io.File
import ch.usi.inf.genesis.model.core.{IntValue, StringValue, ModelObject}
import ch.usi.inf.genesis.model.navigation.NavigatorOption._

class ClassLocMutator(projectPath : String) extends ModelMutator {
  override def visit(obj : ModelObject): NavigatorOption = {
    obj.getProperty("sourceAnchor") match {
				case Some(fileAnchor : ModelObject) =>
          fileAnchor.getProperty("fileName") match {
            case Some(fileName : StringValue) =>
            fileAnchor.getProperty("startLine") match{
                case Some(startLine : IntValue) =>
                  fileAnchor.getProperty("endLine") match{
                    case Some(endLine : IntValue) =>
                      val loc = getLOC(fileName.value, startLine.value, endLine.value)
                      fileAnchor.addProperty("loc", new IntValue(loc))
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

  override def getSelection() : (ModelObject) => Boolean ={
    ((obj) => obj match {
      case ClassEntity() => true
      case _ => false
    })
  }

  //TODO build your own comment stripper
  def getLOC(fileName : String, startLine : Int,  endLine : Int) : Int = {
    val file = new File(projectPath + File.separator + fileName)

    val source = Source.fromFile(file)
		val content = source.mkString
    source.close()
		var loc = 0

    //remove comments
    content.replaceAll("/\\*(?:.|[\\n\\r])*?\\*/","")
    val lines : Array[String] = content.split("[\n|\r\n]")
    lines foreach( (l) => {
      if (l.length() > 0) loc+=1
    })

    println(fileName + "\tLOC: "+loc)
    loc
  }
}
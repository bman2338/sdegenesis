package ch.usi.inf.genesis.model.mutators

import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import ch.usi.inf.genesis.model.core.famix._
import ch.usi.inf.genesis.model.core._
import java.io.File
import collection.mutable.{HashMap, ListBuffer}
import java.text.SimpleDateFormat
import java.util.{Locale, Date}


/**
 * @author Luca Ponzanelli
 * @param revisions the information concerning the revisions' information to be pushed
 *
 * It calculates a list of candidate owners for a Method.
 *
 */

class MethodOwnershipMutator(revision: RevisionEntity) extends ModelMutator {

  override def visit(obj: ModelObject): NavigatorOption = {
    getFileAnchorInformation(obj) match {
      case Some((fileName: String, startLine: Int, endLine: Int)) =>
        getFileEntity(fileName, obj) match {
          case Some(fileEntity) =>
            fileEntity.getProperties(FileEntityProperty.LINES) match {
              case Some(lines: ListBuffer[ModelObject]) =>
//                println("METHOD: " + obj.getName())
//                println("START: " + startLine + "\t" + "END: " + endLine)
//                println("FILE: " + fileName)
                var owners = calculateOwnershipOnLines(lines, startLine, endLine)
                //Add owners to MethodEntity
                owners foreach((o) => {
                  obj.addProperty(FAMIX.OWNER_PROP, o)
                })
              case _ =>
            }
          case _ =>
        }
      case _ => CONTINUE
    }
    CONTINUE
  }

  /**
   * @author Luca Ponzanelli
   * @return A function pointer to the selection method.
   *
   * It selects just the Method Entities.
   */
  override def getSelection(): (ModelObject => Boolean) = {
    ((obj) => obj match {
      case MethodEntity() => true
      case _ => false
    })
  }

  private def getFileEntity(fileName: String, obj: ModelObject): Option[ModelObject] = {
    val added: ListBuffer[ModelObject] = revision.getProperties(RevisionEntityProperty.ADDED_FILES) match {
      case Some(addedFiles: ListBuffer[ModelObject]) => addedFiles
      case _ => new ListBuffer[ModelObject]
    }

    val modified: ListBuffer[ModelObject] = revision.getProperties(RevisionEntityProperty.MODIFIED_FILES) match {
      case Some(modifiedFiles: ListBuffer[ModelObject]) => modifiedFiles
      case _ => new ListBuffer[ModelObject]
    }

    (added ++ modified).foreach((file) => {
      file.getProperty(FileEntityProperty.NAME) match {
        case Some(name: StringValue) =>
          if (name.value.contains(fileName)) return Some(file)
        case _ =>
      }
    })

    None
  }


  private def getFileAnchorInformation(obj: ModelObject): Option[(String, Int, Int)] = {
    obj.getProperty(FAMIX.SOURCE_ANCHOR) match {
      case Some(fileAnchor: ModelObject) =>
        fileAnchor.getProperty(FAMIX.SOURCE_FILE_NAME) match {
          case Some(fileName: StringValue) =>
            fileAnchor.getProperty(FAMIX.SOURCE_START_LINE) match {
              case Some(startLine: IntValue) =>
                fileAnchor.getProperty(FAMIX.SOURCE_END_LINE) match {
                  case Some(endLine: IntValue) =>
                    Some(fileName.value, startLine.value, endLine.value)
                  case _ => None
                }
              case _ => None
            }
          case _ => None
        }
      case _ => None
    }
  }


  private def calculateOwnershipOnLines(lines: ListBuffer[ModelObject], startLine: Int, endLine: Int): ListBuffer[DeveloperEntity] = {
    val c = 25D
    val groupedByAuthor: Map[String, ListBuffer[ModelObject]] = lines.groupBy[String]((line) => {
      line.getProperty(LineEntityProperty.AUTHOR) match {
        case Some(author: StringValue) =>
          author.value
        case _ => "NO AUTHOR" //It should not happen...
      }
    })


    val formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy")

    val day = 1000.0 * 60.0 * 60.0 * 24.0
    val ranking = new HashMap[String, Double]

    groupedByAuthor foreach ((e) => {
      val (author, fileLines) = e
      val R = fileLines.foldLeft(0D)((tot: Double, l: ModelObject) => {
        l.getProperty(LineEntityProperty.NUMBER) match {
          case Some(lineNumber: IntValue) if (lineNumber.value <= endLine && lineNumber.value >= startLine) => {
            //Not a line to be considered
            l.getProperty(LineEntityProperty.DATE) match {
              case Some(dateStr: StringValue) =>
                revision.getProperty(RevisionEntityProperty.DATE) match {
                  case Some(revDate: StringValue) =>
                    val d1: Date = formatter.parse(dateStr.value);
                    val d2: Date = formatter.parse(revDate.value);

                    val time = (d2.getTime - d1.getTime) / day
                    tot + math.exp(-time / c)
                  case _ => tot
                }
              case _ => tot
            }
          }
          case _ => tot
        }
      })

      if(R > 0)
        ranking.put(author, R)
    })


    //Sort Descending by ownership value
    val sorted = ranking.toBuffer.sortWith((a, b) => {
      val (_, d1) = a
      val (_, d2) = b

      d1 > d2
    })

    //Convert to Developer Entities
    val devEntities = new ListBuffer[DeveloperEntity]

    sorted foreach ((e) => {
      val (name, ownership) = e
      val entity = new DeveloperEntity
      entity.addProperty(DeveloperEntityProperty.NAME, new StringValue(name))
      entity.addProperty(DeveloperEntityProperty.OWNERSHIP, new DoubleValue(ownership))
      entity.uniqueId = Some(name + ownership)
      devEntities += entity

    })

//    println("DEVELOPERS")
//    devEntities foreach ((dev) => println(dev.properties))
    devEntities
  }

}
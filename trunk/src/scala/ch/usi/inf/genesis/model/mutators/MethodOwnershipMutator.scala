package ch.usi.inf.genesis.model.mutators

import ch.usi.inf.genesis.model.navigation.NavigatorOption._
import ch.usi.inf.genesis.model.core.famix._
import ch.usi.inf.genesis.model.core._
import java.io.File
import collection.mutable.ListBuffer

/**
 * @author Luca Ponzanelli
 * @param revisions the information concerning the revisions' information to be pushed
 *
 * It calculates a list of candidate owners for a Method.
 *
 */

class MethodOwnershipMutator(revision: RevisionEntity) extends ModelMutator {

  override def visit(obj: ModelObject): NavigatorOption = {
    obj.getProperty(FAMIX.SOURCE_ANCHOR) match {
      case Some(fileAnchor: ModelObject) =>
        fileAnchor.getProperty(FAMIX.SOURCE_FILE_NAME) match {
          case Some(fileName: StringValue) =>
            fileAnchor.getProperty(FAMIX.SOURCE_START_LINE) match {
              case Some(startLine: IntValue) =>
                fileAnchor.getProperty(FAMIX.SOURCE_END_LINE) match {
                  case Some(endLine: IntValue) =>



                    CONTINUE
                  case _ => CONTINUE
                }
              case _ => CONTINUE
            }
          case _ => CONTINUE
        }
      case _ => CONTINUE
    }
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

  private def isFileAffected(fileName: String, fileList: ListBuffer[FileEntity]): Boolean = {
    fileList foreach ((file) => {
      file.getProperty(FileEntityProperty.NAME) match {
        case Some(name: StringValue) => if (name.value.contains(fileName)) return true
        case _ =>
      }
    })

    false
  }

}
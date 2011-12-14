package scala.ch.usi.inf.genesis.data.bugtracker

import java.io.IOException
import java.io.InputStreamReader
import java.net.MalformedURLException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.List
import javax.xml.namespace.QName
import javax.xml.stream.EventFilter
import javax.xml.stream.XMLEventReader
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamException
import javax.xml.stream.events.Attribute
import javax.xml.stream.events.EndElement
import javax.xml.stream.events.StartElement
import javax.xml.stream.events.XMLEvent
import net.htmlparser.jericho.Element
import net.htmlparser.jericho.MasonTagTypes
import net.htmlparser.jericho.MicrosoftConditionalCommentTagTypes
import net.htmlparser.jericho.PHPTagTypes
import net.htmlparser.jericho.Source
import collection.mutable.ListBuffer
import ch.usi.inf.genesis.model.core.StringValue
import ch.usi.inf.genesis.model.core.famix._
import scala.ch.usi.inf.genesis.model.core.famix.{BugHistoryTransitionEntityProperty, BugHistoryTransitionEntity}

object BugzillaParser {

  def parseHistory(historyUrl: URL): ListBuffer[Entity] = {
    val history = new ListBuffer[Entity]
    var lastWho = ""
    var lastWhen = ""

    try {
      MicrosoftConditionalCommentTagTypes.register()
      PHPTagTypes.register()
      PHPTagTypes.PHP_SHORT.deregister()
      MasonTagTypes.register()

      val source = new Source(historyUrl)
      var rowSpanValue = 0
      val bugzillaBody = source.getElementById("bugzilla-body")

      import scala.collection.JavaConversions._
      for (e <- bugzillaBody.getAllElements) {
        if (e.getName == "tr") {
          val children: List[Element] = e.getChildElements

          if (children.get(0).getName == "td") {
            val rowSpan = children.get(0).getAttributeValue("rowspan")
            val localRowSpanValue: Int = if (rowSpan == null) 0 else Integer.parseInt(rowSpan)
            val transition = new BugHistoryTransitionEntity

            if (rowSpanValue <= 0) {
              lastWho = children.get(0).getContent.toString.replaceAll("[\\s]*", "")
              lastWhen = children.get(1).getContent.toString.replaceAll("[\\s]*", "")
              transition.addProperty(BugHistoryTransitionEntityProperty.WHO,new StringValue(lastWho))
              transition.addProperty(BugHistoryTransitionEntityProperty.WHEN,new StringValue(lastWhen))
              transition.addProperty(BugHistoryTransitionEntityProperty.WHAT,new StringValue(children.get(2).getContent.toString.replaceAll("[\\s]*", "")))
              transition.addProperty(BugHistoryTransitionEntityProperty.ADDED,new StringValue(children.get(4).getContent.toString.replaceAll("[\\s]*", "")))
              transition.addProperty(BugHistoryTransitionEntityProperty.REMOVED,new StringValue(children.get(3).getContent.toString.replaceAll("[\\s]*", "")))

              history += transition

              if (localRowSpanValue != 0)
                rowSpanValue = localRowSpanValue - 1
            }
            else if(children.get(0).getName != "th"){
              transition.addProperty(BugHistoryTransitionEntityProperty.WHO,new StringValue(lastWho))
              transition.addProperty(BugHistoryTransitionEntityProperty.WHEN,new StringValue(lastWhen))
              transition.addProperty(BugHistoryTransitionEntityProperty.WHAT,new StringValue(children.get(0).getContent.toString.replaceAll("[\\s]*", "")))
              transition.addProperty(BugHistoryTransitionEntityProperty.ADDED,new StringValue(children.get(2).getContent.toString.replaceAll("[\\s]*", "")))
              transition.addProperty(BugHistoryTransitionEntityProperty.REMOVED,new StringValue(children.get(1).getContent.toString.replaceAll("[\\s]*", "")))

              history += transition
              rowSpanValue -= 1
            }
          }
        }
      }
    }
    catch {
      case e1: MalformedURLException => {
        e1.printStackTrace()
      }
      case e1: IOException => {
        e1.printStackTrace()
      }
    }

    history
  }

  def parse(xmlStream: InputStreamReader): ListBuffer[BugEntity] = {
    val bugList = new ListBuffer[BugEntity]
    var bugFound = true
    var depthLevel = 0

    try {
      System.setProperty("javax.xml.stream.XMLInputFactory", "com.bea.xml.stream.MXParserFactory")
      val factory: XMLInputFactory = XMLInputFactory.newInstance
      val eventReader: XMLEventReader = factory.createXMLEventReader(xmlStream)


      val filteredEventReader: XMLEventReader = factory.createFilteredReader(eventReader, new EventFilter {
        def accept(event: XMLEvent): Boolean = {
          (!event.isProcessingInstruction && !event.isStartDocument && !event.isEndDocument)
        }
      })

      var bug: BugEntity = null
      while (filteredEventReader.hasNext) {
        val e  = filteredEventReader.next.asInstanceOf[XMLEvent]
        if (e.isStartElement) {
          depthLevel += 1
          val element: StartElement = e.asStartElement
          if (element.getName == new QName("bug")) {
            val error: Attribute = element.getAttributeByName(new QName("error"))
            if (error == null) {
              bug = new BugEntity
              bugFound = true
            }
            else
              bugFound = false
          }
          else if ((element.getName == new QName("bug_id")) && bugFound) {
            val el = filteredEventReader.next.asInstanceOf[XMLEvent]
            bug.addProperty(BugEntityProperty.ID, new StringValue(el.asCharacters.getData))
          }
          else if (element.getName == new QName("creation_ts")) {
            val el = filteredEventReader.next.asInstanceOf[XMLEvent]
            bug.addProperty(BugEntityProperty.CREATION_DATE, new StringValue(el.asCharacters.getData))
          }
//          levelDepth = 3 ==> <bugzilla><bug>...<delta_ts>
//                                lv.1   lv.2     lv.3
          else if ((element.getName == new QName("delta_ts")) && depthLevel == 3) {
            val el = filteredEventReader.next.asInstanceOf[XMLEvent]
            bug.addProperty(BugEntityProperty.UPDATE_DATE, new StringValue(el.asCharacters.getData))
          }
          else if (element.getName == new QName("short_desc")) {
            val el = filteredEventReader.next.asInstanceOf[XMLEvent]
            bug.addProperty(BugEntityProperty.SUMMARY, new StringValue(el.asCharacters.getData))
          }          
          else if (element.getName == new QName("bug_status")) {
            val el = filteredEventReader.next.asInstanceOf[XMLEvent]
            bug.addProperty(BugEntityProperty.STATUS, new StringValue(el.asCharacters.getData))
          }
          else if (element.getName == new QName("resolution")) {
            val el = filteredEventReader.next.asInstanceOf[XMLEvent]
            bug.addProperty(BugEntityProperty.RESOLUTION, new StringValue(el.asCharacters.getData))
          }
          else if (element.getName == new QName("priority")) {
            val el = filteredEventReader.next.asInstanceOf[XMLEvent]
            bug.addProperty(BugEntityProperty.PRIORITY, new StringValue(el.asCharacters.getData))
          }
          else if (element.getName == new QName("bug_severity")) {
            val el = filteredEventReader.next.asInstanceOf[XMLEvent]
            bug.addProperty(BugEntityProperty.SEVERITY, new StringValue(el.asCharacters.getData))
          }
          else if (element.getName == new QName("reporter")) {
            val el = filteredEventReader.next.asInstanceOf[XMLEvent]
            val reporter = new BTDeveloperEntity
            val displayName: Attribute = element.getAttributeByName(new QName("name"))
            reporter.addProperty(BTDeveloperEntityProperty.DISPLAY_NAME, new StringValue(if (displayName == null) "" else displayName.getValue))
            reporter.addProperty(BTDeveloperEntityProperty.NAME, new StringValue(el.asCharacters.getData))
            bug.addProperty(BugEntityProperty.REPORTER, reporter)
          }
          else if (element.getName == new QName("assigned_to")) {
            val el = filteredEventReader.next.asInstanceOf[XMLEvent]
            val assignee = new BTDeveloperEntity
            val displayName: Attribute = element.getAttributeByName(new QName("name"))
            assignee.addProperty(BTDeveloperEntityProperty.DISPLAY_NAME, new StringValue(if (displayName == null) "" else displayName.getValue))
            assignee.addProperty(BTDeveloperEntityProperty.NAME, new StringValue(el.asCharacters.getData))
            bug.addProperty(BugEntityProperty.ASSIGNEE, assignee)
          }
          else if (element.getName == new QName("cc")) {
            val el: XMLEvent = filteredEventReader.next.asInstanceOf[XMLEvent]
            val cc = new BTDeveloperEntity
            cc.addProperty(BTDeveloperEntityProperty.NAME, new StringValue(el.asCharacters.getData))
            bug.addProperty(BugEntityProperty.CC,cc)
          }
          else if (element.getName == new QName("product")) {
            val el = filteredEventReader.next.asInstanceOf[XMLEvent]
            bug.addProperty(BugEntityProperty.PROJECT,new StringValue(el.asCharacters.getData))
          }
          else if (element.getName == new QName("component")) {
            val el = filteredEventReader.next.asInstanceOf[XMLEvent]
            bug.addProperty(BugEntityProperty.COMPONENTS,new StringValue(el.asCharacters.getData))
          }
          else if (element.getName == new QName("version")) {
            val el = filteredEventReader.next.asInstanceOf[XMLEvent]
            bug.addProperty(BugEntityProperty.VERSIONS,new StringValue(el.asCharacters.getData))
          }
          else if (element.getName == new QName("rep_platform")) {
            val el = filteredEventReader.next.asInstanceOf[XMLEvent]
            bug.addProperty(BugEntityProperty.PLATFORM,new StringValue(el.asCharacters.getData))
          }
          else if (element.getName == new QName("op_sys")) {
            val el = filteredEventReader.next.asInstanceOf[XMLEvent]
            bug.addProperty(BugEntityProperty.OS,new StringValue(el.asCharacters.getData))
          }
        }

        if (e.isEndElement) {
          depthLevel -= 1
          val element: EndElement = e.asEndElement
          if (element.getName == new QName("bug")) {
            if (bug != null) {
              bugList += bug
              bug = null
            }
          }
        }
      }
    }
    catch {
      case e: XMLStreamException => {
        e.printStackTrace()
      }
    }

    bugList
  }
}



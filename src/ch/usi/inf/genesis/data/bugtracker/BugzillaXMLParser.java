package ch.usi.inf.genesis.data.bugtracker;

import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

//import com.bea.xml.stream.events.StartElementEvent;

public class BugzillaXMLParser {

	public static List<BugInfo> parse(final InputStreamReader xmlStream){
		final List<BugInfo> bugList = new ArrayList<BugInfo>();
		final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		boolean isLongDesc = false;
		boolean bugFound = true;
		try{
			// Use  reference implementation
			System.setProperty(
					"javax.xml.stream.XMLInputFactory",
					"com.bea.xml.stream.MXParserFactory");

			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLEventReader eventReader = factory.createXMLEventReader(xmlStream);
			XMLEventReader filteredEventReader =
					factory.createFilteredReader(eventReader, new EventFilter() {
						public boolean accept(XMLEvent event) {
							// Exclude PIs, StartDocument and EndDocument
							return (!event.isProcessingInstruction() && 
									!event.isStartDocument() && 
									!event.isEndDocument());
						}
					});

			BugInfo bug = null;
			while (filteredEventReader.hasNext()) {
				final XMLEvent e = (XMLEvent) filteredEventReader.next();
				if(e.isStartElement()){

					StartElement element= e.asStartElement();

					if(element.getName().equals(new QName("bug"))){
						final Attribute error = element.getAttributeByName(new QName("error"));
						if(error == null){
							bug = new BugInfo();
							bugFound = true;
						}
						else
							bugFound = false;

					}
					else if(element.getName().equals(new QName("bug_id")) && bugFound){
						final XMLEvent el = (XMLEvent) filteredEventReader.next();
						Characters content = el.asCharacters();
						bug.setId(content == null? "" : content.getData());
					}
					else if(element.getName().equals(new QName("creation_ts"))){						
						try {
							final XMLEvent el = (XMLEvent) filteredEventReader.next();
							final Date date = formatter.parse(el.asCharacters().getData());
							bug.setCreationDate(date);
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
					}
					else if(element.getName().equals(new QName("delta_ts")) && !isLongDesc){
						try {
							final XMLEvent el = (XMLEvent) filteredEventReader.next();
							final Date date = formatter.parse(el.asCharacters().getData());
							bug.setUpdateDate(date);
						} catch (ParseException e1) {
							e1.printStackTrace();
						}
					}
					else if(element.getName().equals(new QName("short_desc"))){
						final XMLEvent el = (XMLEvent) filteredEventReader.next();
						bug.setSummary(el.asCharacters().getData());
					}
					else if(element.getName().equals(new QName("long_desc"))){
						isLongDesc = true;
					}
					else if(element.getName().equals(new QName("bug_status"))){
						final XMLEvent el = (XMLEvent) filteredEventReader.next();
						bug.setStatus(el.asCharacters().getData());
					}
					else if(element.getName().equals(new QName("resolution"))){
						final XMLEvent el = (XMLEvent) filteredEventReader.next();
						bug.setResolution(el.asCharacters().getData());
					}
					else if(element.getName().equals(new QName("priority"))){
						final XMLEvent el = (XMLEvent) filteredEventReader.next();
						bug.setPriority(el.asCharacters().getData());
					}
					else if(element.getName().equals(new QName("bug_severity"))){
						final XMLEvent el = (XMLEvent) filteredEventReader.next();
						bug.setSeverity(el.asCharacters().getData());
					}
					else if(element.getName().equals(new QName("reporter"))){
						final XMLEvent el = (XMLEvent) filteredEventReader.next();
						final BugTrackerUser reporter = new BugTrackerUser();
						final Attribute displayName = element.getAttributeByName(new QName("name"));
						reporter.setDisplayName(displayName == null? "" : displayName.getValue());
						reporter.setName(el.asCharacters().getData());
						bug.setReporter(reporter);
					}
					else if(element.getName().equals(new QName("assigned_to"))){
						final XMLEvent el = (XMLEvent) filteredEventReader.next();
						final BugTrackerUser assignee = new BugTrackerUser();
						final Attribute displayName = element.getAttributeByName(new QName("name"));
						assignee.setDisplayName(displayName == null? "" : displayName.getValue());
						assignee.setName(el.asCharacters().getData());
						bug.setAssignee(assignee);
					}
					else if(element.getName().equals(new QName("cc"))){
						final XMLEvent el = (XMLEvent) filteredEventReader.next();
						final BugTrackerUser assignee = new BugTrackerUser();
						assignee.setName(el.asCharacters().getData());
						bug.addCcUser(assignee);
					}
					
				}

				if(e.isEndElement()){
					EndElement element = e.asEndElement();
					if(element.getName().equals(new QName("bug"))){
						if(bug != null){
							bugList.add(bug);
							bug = null;
						}
					}
					else if(element.getName().equals(new QName("long_desc"))){
						isLongDesc = false;
					}
				}
			}

		}catch (XMLStreamException e) {
			e.printStackTrace();
		}
		return bugList;
	}
}

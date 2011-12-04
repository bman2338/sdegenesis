package ch.usi.inf.genesis.data.bugtracker;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
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

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.MasonTagTypes;
import net.htmlparser.jericho.MicrosoftConditionalCommentTagTypes;
import net.htmlparser.jericho.PHPTagTypes;
import net.htmlparser.jericho.Source;


public class BugzillaParser {

	public static BugHistory parseHistory(final URL historyUrl){
		final BugHistory history = new BugHistory();
        String lastWho = "";
        Date lastWhen = null;
		try {
			final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			MicrosoftConditionalCommentTagTypes.register();
			PHPTagTypes.register();
			PHPTagTypes.PHP_SHORT.deregister();
			MasonTagTypes.register();
			final Source source = new Source(historyUrl);

			int rowSpanValue = 0;
			final Element bugzilla_body = source.getElementById("bugzilla-body");
			for(final Element e : bugzilla_body.getAllElements()){

				if(e.getName().equals("tr")){
					final List<Element> childs = e.getChildElements();
					if(childs.get(0).getName().equals("th"))//TABLE's HEADERS
						continue;
					if(childs.get(0).getName().equals("td")){
						final String rowSpan = childs.get(0).getAttributeValue("rowspan");
						final int localRowSpanValue = rowSpan == null? 0 : Integer.parseInt(rowSpan);

						final BugHistoryTransition transition = new BugHistoryTransition();
						if(rowSpanValue <= 0){
							transition.setWho(childs.get(0).getContent().toString().replaceAll("[\\s]*",""));
							try{
								transition.setWhen(formatter.parse(childs.get(1).getContent().toString()));
							}catch (final ParseException pex) {
								transition.setWhen(new Date(0));
							}
							transition.setAdded(childs.get(3).getContent().toString().replaceAll("[\\s]*",""));
							transition.setRemoved(childs.get(4).getContent().toString());
                            lastWho = transition.getWho();
                            lastWhen = transition.getWhen();
                            history.addTransition(childs.get(2).getContent().toString().replaceAll("[\\s]*",""), transition);

							if(localRowSpanValue != 0)
								rowSpanValue = localRowSpanValue-1;
						}
						else{
                            final String what = childs.get(0).getContent().toString().replaceAll("[\\s]*", "");
                            transition.setWhen(lastWhen);
                            transition.setWho(lastWho);
							transition.setAdded(childs.get(1).getContent().toString().replaceAll("[\\s]*", ""));
							transition.setRemoved(childs.get(2).getContent().toString());
                            history.addTransition(what, transition);
							--rowSpanValue;
						}
					}
				}
			}
		} catch (final MalformedURLException e1) {
			e1.printStackTrace();
		} catch (final IOException e1) {
			e1.printStackTrace();
		}
		return history;
	}

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

			final XMLInputFactory factory = XMLInputFactory.newInstance();
			final XMLEventReader eventReader = factory.createXMLEventReader(xmlStream);
			final XMLEventReader filteredEventReader =
					factory.createFilteredReader(eventReader, new EventFilter() {
						public boolean accept(final XMLEvent event) {
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

					final StartElement element= e.asStartElement();

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
						final Characters content = el.asCharacters();
						bug.setId(content == null? "" : content.getData());
					}
					else if(element.getName().equals(new QName("creation_ts"))){						
						try {
							final XMLEvent el = (XMLEvent) filteredEventReader.next();
							final Date date = formatter.parse(el.asCharacters().getData());
							bug.setCreationDate(date);
						} catch (final ParseException e1) {
							e1.printStackTrace();
						}
					}
					else if(element.getName().equals(new QName("delta_ts")) && !isLongDesc){
						try {
							final XMLEvent el = (XMLEvent) filteredEventReader.next();
							final Date date = formatter.parse(el.asCharacters().getData());
							bug.setUpdateDate(date);
						} catch (final ParseException e1) {
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
					else if(element.getName().equals(new QName("product"))){
						final XMLEvent el = (XMLEvent) filteredEventReader.next();
						bug.setProject(el.asCharacters().getData());
					}
					else if(element.getName().equals(new QName("component"))){
						final XMLEvent el = (XMLEvent) filteredEventReader.next();
						bug.addComponent(el.asCharacters().getData());
					}
					else if(element.getName().equals(new QName("version"))){
						final XMLEvent el = (XMLEvent) filteredEventReader.next();
						bug.addVersion(el.asCharacters().getData());
					}
					else if(element.getName().equals(new QName("rep_platform"))){
						final XMLEvent el = (XMLEvent) filteredEventReader.next();
						bug.setPlatform(el.asCharacters().getData());
					}
					else if(element.getName().equals(new QName("op_sys"))){
						final XMLEvent el = (XMLEvent) filteredEventReader.next();
						bug.setOperatingSys(el.asCharacters().getData());
					}

				}

				if(e.isEndElement()){
					final EndElement element = e.asEndElement();
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

		}catch (final XMLStreamException e) {
			e.printStackTrace();
		}
		return bugList;
	}
}

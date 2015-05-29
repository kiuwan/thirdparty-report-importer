package com.kiuwan.importer.parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.kiuwan.importer.beans.File;
import com.kiuwan.importer.beans.Rule;
import com.kiuwan.importer.beans.Violation;

public class FxCopReportParser extends DefaultHandler implements ReportParser {
	
	Collection<Violation> defects = new ArrayList<Violation>();
	

	String analyzedFolder;
	
	Boolean bMessage = false;
	Boolean bIssue = false;
	
	private String ruleCode = "";
	String filePath;
	Integer line;
	
	
	
	private StringBuilder sourceCode = new StringBuilder();
	
	Map<String, Rule> rules = new HashMap<String, Rule>();
	
	
	
	public FxCopReportParser(String analyzedFolder) {
		this.analyzedFolder = analyzedFolder;
	}
	
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if("Message".equalsIgnoreCase(qName)){
			bMessage = true;
			ruleCode = attributes.getValue("CheckId");
			if (!rules.containsKey(ruleCode)) {
				rules.put(ruleCode, new Rule(ruleCode));
			}
		}
		else if ("Issue".equalsIgnoreCase(qName)){
			if (bMessage) {
				if (attributes.getValue("Path") != null && attributes.getValue("File") != null) {
					bIssue = true;	
					filePath = attributes.getValue("Path") + java.io.File.separator + attributes.getValue("File");
					filePath = filePath.replace(analyzedFolder, "");
					if (filePath.startsWith("\\")) {
						filePath = filePath.substring(1);
					}
					line = null;
					String lineAtt = attributes.getValue("Line");
					if (lineAtt != null) {
						try {
							line = Integer.parseInt(lineAtt);
						} catch (NumberFormatException e) {}
					}
				}
			}
		}
		
		
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
	
		if("Message".equalsIgnoreCase(qName)){
			bMessage = false;
		}
		else if ("Issue".equalsIgnoreCase(qName)) {
			if (bIssue) {
				File file = new File(line, filePath, sourceCode.toString());
				defects.add(new Violation(file, rules.get(ruleCode)));
				sourceCode.setLength(0);
				bIssue = false;
			}
		}
		
		
		
		
		
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (bIssue) {
			sourceCode.append(ch, start, length);
		}
	}
	

	@Override
	public Collection<Violation> getDefects() {
		return defects;
	}

	@Override
	public void parse(String inputFile) {
		
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(inputFile, this);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}

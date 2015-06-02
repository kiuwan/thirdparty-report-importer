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

public class FortifyReportParser extends DefaultHandler implements ReportParser {

	private final String RULECODE_PREXIX = "CUS.FORTIFY.";
	
	Collection<Violation> defects = new ArrayList<Violation>();
	
	Boolean bVulnerability = false;
	Boolean bClassInfo = false;
	Boolean bType = false;
	Boolean bSubtype = false;
	Boolean bSnippets = false;
	Boolean bSnippet = false;
	Boolean bSnippetText = false;
	
	private String ruleCode = null;
	private StringBuilder type = new StringBuilder();
	private StringBuilder subtype = new StringBuilder();
	private StringBuilder sourceCode = new StringBuilder();
	private File file;
	
	Map<String, Rule> rules = new HashMap<String, Rule>();
	Map<String, String> snippets = new HashMap<String, String>();
	String snippetId;
	
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

		if("Vulnerability".equalsIgnoreCase(qName)){
			bVulnerability = true;
		}
		else if ("ClassInfo".equalsIgnoreCase(qName)) {
			bClassInfo = true;
		}
		else if ("Type".equalsIgnoreCase(qName)) {
			if (bClassInfo) {
				bType = true;
			}
		}
		else if ("Subtype".equalsIgnoreCase(qName)) {
			if (bClassInfo) {
				bSubtype = true;
			}
		}
		else if ("SourceLocation".equalsIgnoreCase(qName)) {
			if (bVulnerability) {
				String filePath = attributes.getValue("path");
				Integer line = null;
				String lineAtt = attributes.getValue("line");
				if (lineAtt != null) {
					try {
						line = Integer.parseInt(lineAtt);
					} catch (NumberFormatException e) {}
				}
				String snippet = attributes.getValue("snippet");
				file = new File(line, filePath, snippet);
			}
		}
		else if ("Snippets".equalsIgnoreCase(qName)) {
			bSnippets = true;
		}
		else if ("Snippet".equalsIgnoreCase(qName)) {
			if (bSnippets) {
				snippetId = attributes.getValue("id");
				bSnippet = true;
			}
		}
		else if ("Text".equalsIgnoreCase(qName)) {
			if (bSnippets && bSnippet) {
				bSnippetText = true;
			}
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
	
		if("Vulnerability".equalsIgnoreCase(qName)){
			
			defects.add(new Violation(file, rules.get(ruleCode)));
			
			ruleCode = null;
			type.setLength(0);
			subtype.setLength(0);
			bVulnerability = false;
		}
		else if ("ClassInfo".equalsIgnoreCase(qName)) {
			if (bClassInfo) {
				ruleCode  = type.toString() + " " + subtype.toString();
				ruleCode = ruleCode.replaceAll("\\s+", "_");
				ruleCode = ruleCode.toLowerCase();
				
				ruleCode  = RULECODE_PREXIX + ruleCode;
				if (!rules.containsKey(ruleCode)) {
					rules.put(ruleCode, new Rule(ruleCode));
				}
			}
			bClassInfo = false;
		}
		else if ("Type".equalsIgnoreCase(qName)) {
			bType = false;
		}
		else if ("Subtype".equalsIgnoreCase(qName)) {
			bSubtype = false;
		}
		else if ("Snippets".equalsIgnoreCase(qName)) {
			bSnippets = false;
			updateSnippets();
		}
		else if ("Snippet".equalsIgnoreCase(qName)) {
			if (bSnippet) {
				bSnippet = false;
			}
		}
		else if ("Text".equalsIgnoreCase(qName)) {
			if (bSnippet && bSnippetText) {
				snippets.put(snippetId, sourceCode.toString());
				sourceCode.setLength(0);
				bSnippetText = false;
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		if (bType) {
			type.append(ch, start, length);
		} else if (bSubtype) {
			subtype.append(ch, start, length);
		} else if (bSnippetText) {
			sourceCode.append(ch, start, length);
		}
	}
	

	private void updateSnippets() {
		
		for (Violation defect: defects) {
			String snippetId = defect.getFile().getCode();
			if (snippets.containsKey(snippetId)) {
				defect.getFile().setCode(snippets.get(snippetId));
			}
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

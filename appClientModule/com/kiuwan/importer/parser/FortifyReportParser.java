package com.kiuwan.importer.parser;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.kiuwan.importer.beans.File;
import com.kiuwan.importer.beans.Rule;
import com.kiuwan.importer.beans.Violation;

public class FortifyReportParser extends ReportParser {
	
	Boolean bVulnerability = false;
	Boolean bRuleCode = false;
	Boolean bSnippets = false;
	Boolean bSnippet = false;
	Boolean bSnippetText = false;
	
	private StringBuilder ruleCode = new StringBuilder();
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
		else if ("ClassID".equalsIgnoreCase(qName)) {
			if (bVulnerability) {
				bRuleCode = true;
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
			
			defects.add(new Violation(file, rules.get(ruleCode.toString())));
			
			ruleCode.setLength(0);
			bVulnerability = false;
		}
		else if ("ClassID".equalsIgnoreCase(qName)) {
			if (bRuleCode) {
				if (!rules.containsKey(ruleCode)) {
					rules.put(ruleCode.toString(), new Rule(ruleCode.toString()));
				}
				bRuleCode = false;
			}
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
		if (bRuleCode) {
			ruleCode.append(ch, start, length);
		}
		else if (bSnippetText) {
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
	
}

package com.kiuwan.importer.beans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="violation")
public class Violation {

	File file;
	Rule rule;
	
	
	public Violation() {
	}
	
	public Violation(File file, Rule rule) {
		this.file = file;
		this.rule = rule;
	}

	public File getFile() {
		return file;
	}
	
	@XmlElement
	public void setFile(File file) {
		this.file = file;
	}
	
	public Rule getRule() {
		return rule;
	}
	
	@XmlElement
	public void setRule(Rule rule) {
		this.rule = rule;
	}
}

package com.kiuwan.importer.beans;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Rule {

	String code;
	
	public Rule() {
	}
	
	public Rule(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}
	
	@XmlAttribute
	public void setCode(String code) {
		this.code = code;
	}
}

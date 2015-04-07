package com.kiuwan.importer.beans;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class File {

	Integer line;
	String name;
	String code;
	
	
	public File() {
	}
	
	public File(Integer line, String name, String code) {
		this.line = line;
		this.name = name;
		this.code = code;
	}

	public Integer getLine() {
		return line;
	}
	
	@XmlAttribute
	public void setLine(Integer line) {
		this.line = line;
	}
	
	public String getName() {
		return name;
	}
	
	@XmlAttribute
	public void setName(String name) {
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	
	@XmlValue
	@XmlJavaTypeAdapter(AdapterXmlCDATA.class)
	public void setCode(String code) {
		this.code = code;
	}
}

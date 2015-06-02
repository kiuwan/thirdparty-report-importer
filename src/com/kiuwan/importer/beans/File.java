package com.kiuwan.importer.beans;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public class File {

	Integer line;
	String name;
	String code;
	Boolean hashed = false;
	
	
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
	
	public Boolean getHashed() {
		return hashed;
	}
	
	@XmlAttribute
	public void setHashed(Boolean hashed) {
		this.hashed = hashed;
	}
	
	public void hashSourceCode() throws NoSuchAlgorithmException {
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		messageDigest.update(getCode().getBytes());
		this.code = new BigInteger(1, messageDigest.digest()).toString(16);
		hashed = true;
	}
}

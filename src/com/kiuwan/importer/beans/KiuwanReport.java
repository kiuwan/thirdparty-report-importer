package com.kiuwan.importer.beans;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="kiuwan")
public class KiuwanReport {

	Collection<Violation> defects;
	
	public KiuwanReport() {
	}
	
	public Collection<Violation> getDefects() {
		return defects;
	}

	@XmlElementWrapper(name="defects")
	@XmlElement(name="violation")
	public void setDefects(Collection<Violation> defects) {
		this.defects = defects;
	}
}

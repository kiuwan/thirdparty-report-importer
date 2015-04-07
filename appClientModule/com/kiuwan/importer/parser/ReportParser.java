package com.kiuwan.importer.parser;

import java.util.ArrayList;
import java.util.Collection;

import org.xml.sax.helpers.DefaultHandler;

import com.kiuwan.importer.beans.Violation;

public abstract class ReportParser extends DefaultHandler {

	Collection<Violation> defects = new ArrayList<Violation>();
	
	
	public Collection<Violation> getDefects() {
		return defects;
	}
}

package com.kiuwan.importer.parser;

import java.util.Collection;

import com.kiuwan.importer.beans.Violation;

public interface ReportParser {

	public Collection<Violation> getDefects();
	public abstract void parse(String inputFile); 
}

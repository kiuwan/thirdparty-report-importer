package com.kiuwan.importer.parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.kiuwan.importer.beans.File;
import com.kiuwan.importer.beans.Rule;
import com.kiuwan.importer.beans.Violation;

public class BrakemanReportParser implements ReportParser {
	
	
	
	
	private final String RULECODE_PREXIX = "CUS.RUBY.BRAKEMAN.";
	
	Collection<Violation> defects = new ArrayList<Violation>();
	Map<String, Rule> rules = new HashMap<String, Rule>();
	
	private String analyzedFolder;
		
	public BrakemanReportParser(String analyzedFolder) {
		this.analyzedFolder = normalizeAnalyzedFolder(analyzedFolder);		
	}

	private String normalizeAnalyzedFolder(String analyzedFolder) {
		analyzedFolder = analyzedFolder.replace("\\", "/");
		
		if (!analyzedFolder.endsWith("/")) {
			analyzedFolder = analyzedFolder + "/";
		}
		
		return analyzedFolder;
	}
	
	@Override
	public Collection<Violation> getDefects() {
		return defects;
	}

	@Override
	public void parse(String inputFile) {
		
		JSONParser parser = new JSONParser();
		
		try {
			 
			Object obj = parser.parse(new FileReader(inputFile));
	 
			JSONObject jsonObject = (JSONObject) obj;
	 
			
			//TODO: only warnings are imported. Perhaps you need to parse ignored_warnings and errors also
			
			JSONArray warnings = (JSONArray) jsonObject.get("warnings");
			Iterator<JSONObject> warningsIt = warnings.iterator();
			while (warningsIt.hasNext()) {
				JSONObject warning = warningsIt.next();
				
				String warning_type = (String) warning.get("warning_type");
				String filename = (String) warning.get("file");
				Long line = (Long) warning.get("line");
				String code = (String) warning.get("code");
					
				createDefect(filename, warning_type, line, code);
			}
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}

	private void createDefect(String filePath, String warning, Long line, String code) {
		
		//System.out.println("createDefect(" + filePath + ", " + warning + ", " + line + ", " + code + ")");
		
		String ruleCode = warning.toLowerCase();
		ruleCode = ruleCode.replace(" ", "_");
		ruleCode = RULECODE_PREXIX + ruleCode;
		
		
		if (!rules.containsKey(ruleCode)) {
			rules.put(ruleCode, new Rule(ruleCode));
		}
		
		String relativeFilePath = filePath.replace(analyzedFolder, "");
		
		int lineNumber = 0;
		if (null != line) {
			lineNumber = line.intValue();
		}
		File file = new File(lineNumber, relativeFilePath, code);
		defects.add(new Violation(file, rules.get(ruleCode)));
		
	}
	
}

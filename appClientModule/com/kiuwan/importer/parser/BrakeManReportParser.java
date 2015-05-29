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

public class BrakeManReportParser implements ReportParser {
	
	
	Collection<Violation> defects = new ArrayList<Violation>();
	Map<String, Rule> rules = new HashMap<String, Rule>();
	

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

	private void createDefect(String filePath, String ruleCode, Long line, String code) {
		
		if (!rules.containsKey(ruleCode)) {
			rules.put(ruleCode, new Rule(ruleCode));
		}
		
		File file = new File(line.intValue(), filePath, code);
		defects.add(new Violation(file, rules.get(ruleCode)));
		
	}
	
}

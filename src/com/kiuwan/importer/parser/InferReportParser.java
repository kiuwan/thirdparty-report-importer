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

public class InferReportParser implements ReportParser {
	
	private final String RULECODE_PREXIX = "CUS.INFER.JAVA.";
	
	
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
	 
			JSONArray bugs = (JSONArray) obj;
	 
			Iterator<JSONObject> iterator = bugs.iterator();
			while (iterator.hasNext()) {
				JSONObject bug = iterator.next();
				String file = (String) bug.get("file");
				Long line = (Long) bug.get("line");
				String bugType = (String) bug.get("bug_type");
				String procedure = (String) bug.get("procedure");
				String qualifier = (String) bug.get("qualifier");
				String bugMessage = procedure + ": " + qualifier;
				// JSONArray bug_trace = (JSONArray) bug.get("bug_trace");
					
				createDefect(file, bugType, line, bugMessage);
			}
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}

	private void createDefect(String filePath, String defectName, Long line, String code) {
		
		String normalizedDefectName = defectName.toLowerCase();
		normalizedDefectName = normalizedDefectName.replace("/", ".");
		
		String ruleCode = RULECODE_PREXIX + normalizedDefectName;
		
		if (!rules.containsKey(ruleCode)) {
			rules.put(ruleCode, new Rule(ruleCode));
		}
		
		File file = new File(line.intValue(), filePath, code);
		defects.add(new Violation(file, rules.get(ruleCode)));
		
	}
	
}

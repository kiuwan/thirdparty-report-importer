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

public class RuboCopReportParser implements ReportParser {
	
	private final String RULECODE_PREXIX = "CUS.RUBY.RUBOCOP.";
	
	
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
	 
			JSONArray files = (JSONArray) jsonObject.get("files");
			Iterator<JSONObject> iterator = files.iterator();
			while (iterator.hasNext()) {
				JSONObject file = iterator.next();
				String filename = (String) file.get("path");
				JSONArray offenses = (JSONArray) file.get("offenses");
				Iterator<JSONObject> offensesIt = offenses.iterator();
				while (offensesIt.hasNext()) {
					JSONObject offense = offensesIt.next();
					String message = (String) offense.get("message");
					String cop_name = (String) offense.get("cop_name");
					JSONObject location = (JSONObject) offense.get("location");
					Long line = (Long) location.get("line");
					
					createDefect(filename, cop_name, line);
				}
			}
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}

	private void createDefect(String filePath, String copName, Long line) {
		
		String normalizedCopName = copName.toLowerCase();
		normalizedCopName = normalizedCopName.replace("/", ".");
		
		String ruleCode = RULECODE_PREXIX + normalizedCopName;
		
		if (!rules.containsKey(ruleCode)) {
			rules.put(ruleCode, new Rule(ruleCode));
		}
		
		File file = new File(line.intValue(), filePath, "");
		defects.add(new Violation(file, rules.get(ruleCode)));
		
	}
	
}

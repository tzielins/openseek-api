package org.fairdom.testhelpers;

import static org.junit.Assert.assertTrue;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONHelper {
	public static boolean isValidJSON(final String json) {
	  	   boolean valid = false;
	  	   try {
	  	      new JSONParser().parse(json);
	  	      valid=true;
	  	   } catch (ParseException jpe) {
	  	      jpe.printStackTrace();
	  	   } 
	  	   return valid;
	  }
	
	public static JSONObject processJSON(String json) throws ParseException {
		assertTrue(JSONHelper.isValidJSON(json));
		return (JSONObject)(new JSONParser().parse(json));		
	}
}

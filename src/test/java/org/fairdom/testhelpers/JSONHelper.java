package org.fairdom.testhelpers;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONHelper {
	public static boolean isValidJSON(final String json) {
	  	   boolean valid = false;
	  	   try {
	  	      final JsonParser parser = new ObjectMapper().getJsonFactory()
	  	            .createJsonParser(json);
	  	      while (parser.nextToken() != null) {
	  	      }
	  	      valid = true;
	  	   } catch (JsonParseException jpe) {
	  	      jpe.printStackTrace();
	  	   } catch (IOException ioe) {
	  	      ioe.printStackTrace();
	  	   }

	  	   return valid;
	  }
	
	public static JSONObject processJSON(String json) throws ParseException {
		assertTrue(JSONHelper.isValidJSON(json));
		return (JSONObject)(new JSONParser().parse(json));		
	}
}

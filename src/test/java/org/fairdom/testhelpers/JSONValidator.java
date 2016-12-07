package org.fairdom.testhelpers;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONValidator {
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
}

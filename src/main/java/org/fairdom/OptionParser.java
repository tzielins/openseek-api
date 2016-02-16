package org.fairdom;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by quyennguyen on 19/02/15.
 * Parse Java command line arguments
 * Argument structure with example value
 * -account:{"username":"test","password":"test"}
 * -endpoints:{"as":"http://as.example.com","dss":"http://dss.example.com","sessionToken":"somevalue"}
 * -query:{"entityType":"Experiment","property":"SEEK_STUDY_ID","propertyValue":"Study_1"}
 * -download:{"type":"file","permID":"ID100","source":"original/file","dest":"/home/test/file"}
 */
public class OptionParser {
	private JSONObject account = null;
	private JSONObject endpoints = null;
	private JSONObject query = null;
	private JSONObject download = null;	
	
	public OptionParser(String[] args) throws InvalidOptionException, ParseException {
      for (int i = 0; i < args.length; i++) {
          String arg = args[i];
          if (arg.equals("-account")) {
              i++;
              handleEmptyOptionValue(arg, args[i]);
              setAccount(args[i]);
          }
          else if (arg.equals("-endpoints")) {
              i++;
              handleEmptyOptionValue(arg, args[i]);
              setEndpoints(args[i]);
          }
          else if (arg.equals("-query")) {
              i++;
              handleEmptyOptionValue(arg, args[i]);
              setQuery(args[i]);
          }
          else if (arg.equals("-download")) {
              i++;
              handleEmptyOptionValue(arg, args[i]);
              setDownload(args[i]);
          }
          else {
              throw new InvalidOptionException("Unrecognised option: " + args[i]);
          }
      }
	}
		
    public JSONObject stringToJson(String str) throws ParseException {    	
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(str);
		JSONObject jsonObj = (JSONObject) obj;
		return jsonObj;         
    }
    
    private void setAccount(String account) throws ParseException {
    	JSONObject acc = stringToJson(account);
		this.account = acc;
	}

	public JSONObject getAccount() {
		return account;
	}
	
	private void setEndpoints(String endpoints) throws ParseException {
    	JSONObject ep = stringToJson(endpoints);
		this.endpoints = ep;
	}

	public JSONObject getEndpoints() {
		return endpoints;
	}
	
	private void setQuery(String query) throws ParseException {
    	JSONObject q = stringToJson(query);
		this.query = q;
	}

	public JSONObject getQuery() {
		return query;
	}
	
	private void setDownload(String download) throws ParseException {
    	JSONObject dl = stringToJson(download);
		this.download = dl;
	}

	public JSONObject getDownload() {
		return download;
	}

    private void handleEmptyOptionValue(String option, String value) throws InvalidOptionException {
        if (value.trim().isEmpty()){
            throw new InvalidOptionException("Empty value for: " + option);
        }
    }
}

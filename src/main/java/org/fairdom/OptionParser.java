package org.fairdom;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author Quyen Nugyen
 * @author Stuart Owen
 *
 * 
 *         Argument structure with example value
 *         -account:{"username":"test","password":"test"}
 *         -endpoints:{"as":"http://as.example.com","dss":"http://dss.example.com","sessionToken":"somevalue"}
 *         -query:{"entityType":"Experiment","queryType":"PROPERTY","property":"SEEK_STUDY_ID","propertyValue":"Study_1"}
 *         -download:{"type":"file","permID":"ID100","source":"original/file","dest":"/home/test/file"}
 */
public class OptionParser {
	private JSONObject account = null;
	private JSONObject download = null;
	private JSONObject endpoints = null;
	private JSONObject query = null;
	private Action action = null;

	public OptionParser(String[] args) throws InvalidOptionException, ParseException {
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.equals("-account")) {
				i++;
				handleEmptyOptionValue(arg, args[i]);
				setAccount(args[i]);
			} else if (arg.equals("-endpoints")) {
				i++;
				handleEmptyOptionValue(arg, args[i]);
				setEndpoints(args[i]);
			} else if (arg.equals("-query")) {
				i++;
				handleEmptyOptionValue(arg, args[i]);
				setQuery(args[i]);
			} else if (arg.equals("-download")) {
				i++;
				handleEmptyOptionValue(arg, args[i]);
				setDownload(args[i]);
			} else {
				throw new InvalidOptionException("Unrecognised option: " + args[i]);
			}
		}
		determineAction();
	}

	public List<String> constructAttributeValues(String attributeValues) {
		String[] values = attributeValues.split(",");
		List<String> sanitizedValues = new ArrayList<String>();
		for (String value : values) {
			value = value.trim();
			if (value.length() > 0) {
				sanitizedValues.add(value);
			}
		}
		if (attributeValues.isEmpty()) {
			sanitizedValues.add("");
		}
		return sanitizedValues;
	}

	public JSONObject getAccount() {
		return account;
	}

	public JSONObject getDownload() {
		return download;
	}

	public JSONObject getEndpoints() {
		return endpoints;
	}

	public JSONObject getQuery() {
		return query;
	}

	public JSONObject stringToJson(String str) throws ParseException {
		JSONParser parser = new JSONParser();
		String escapeStr = str.replace("%", "\"").replace("+", " ");
		return (JSONObject) parser.parse(escapeStr);
	}

	public Action getAction() {
		return action;
	}

	private void handleEmptyOptionValue(String option, String value) throws InvalidOptionException {
		if (value.trim().isEmpty()) {
			throw new InvalidOptionException("Empty value for: " + option);
		}
	}

	private void setAccount(String account) throws ParseException {
		JSONObject acc = stringToJson(account);
		this.account = acc;
	}

	private void setDownload(String download) throws ParseException {
		JSONObject dl = stringToJson(download);
		this.download = dl;
	}

	private void setEndpoints(String endpoints) throws ParseException {
		JSONObject ep = stringToJson(endpoints);
		this.endpoints = ep;
	}

	private void setQuery(String query) throws ParseException {
		JSONObject q = stringToJson(query);
		this.query = q;
	}

	private void determineAction() throws InvalidOptionException {
		if (getAccount() != null && getEndpoints() != null) {
			action = Action.LOGIN;
		} else if (getDownload() != null) {
			action = Action.DOWNLOAD;
		} else if (getEndpoints() != null && getQuery() != null) {
			if (getEndpoints().get("dss") != null) {
				action = Action.DS_QUERY;
			} else if (getEndpoints().get("as") != null) {
				action = Action.AS_QUERY;
			}
		}
		if (action == null) {
			throw new InvalidOptionException(
					"Unable to determine the action from the available options passed, options appear to be incomplete");
		}
	}
}

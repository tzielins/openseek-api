package org.fairdom;

import java.io.IOException;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 * The entry point, containing the main method
 * 
 * @author Stuart Owen
 *
 */
public class OpenSeekEntry {

	public static void main(String[] args) {
		new OpenSeekEntry(args).execute();
	}

	private String[] args;

	public OpenSeekEntry(String args[]) {
		this.args = args;
	}

	public void execute() {
		OptionParser options = null;
		try {
			options = new OptionParser(args);
		} catch (InvalidOptionException e) {
			System.err.println("Invalid option: " + e.getMessage());
			System.exit(-1);
		} catch (ParseException pe) {
			System.out.println("position: " + pe.getPosition());
			System.out.println(pe);
			exit(-1);
		}
		String json = "";
		try {
			switch (options.getAction()) {
			case LOGIN:
				json = doLogin(options);
				break;
			case AS_QUERY:
				json = doApplicationServerQuery(options);
				break;
			case DS_QUERY:
				json = doDataStoreQuery(options);
				break;
			case DOWNLOAD:
				json = doDownload(options);
				break;

			default:
				throw new InvalidOptionException("Unable to determine action");
			}
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
			exit(-1);
		}
		System.out.println(json);
		exit(0);
	}

	private String doApplicationServerQuery(OptionParser options) throws InvalidOptionException {
		JSONObject endpoints = options.getEndpoints();
		JSONObject query = options.getQuery();

		ApplicationServerQuery asQuery = new ApplicationServerQuery(endpoints.get("as").toString(),
				endpoints.get("sessionToken").toString());
		List<? extends Object> result;
		if (query.get("queryType").toString().equals(QueryType.PROPERTY.toString())) {
			result = asQuery.query(query.get("entityType").toString(), QueryType.PROPERTY,
					query.get("property").toString(), query.get("propertyValue").toString());
		} else {
			List<String> attributeValues = options.constructAttributeValues(query.get("attributeValue").toString());
			result = asQuery.query(query.get("entityType").toString(), QueryType.ATTRIBUTE,
					query.get("attribute").toString(), attributeValues);
		}
		return new JSONCreator(result).getJSON();
	}

	private String doDataStoreQuery(OptionParser options) throws InvalidOptionException {
		JSONObject endpoints = options.getEndpoints();
		JSONObject query = options.getQuery();
		DataStoreQuery dssQuery = new DataStoreQuery(endpoints.get("dss").toString(),
				endpoints.get("sessionToken").toString());
		List<? extends Object> result;
		if (query.get("queryType").toString().equals(QueryType.PROPERTY.toString())) {
			result = dssQuery.query(query.get("entityType").toString(), QueryType.PROPERTY,
					query.get("property").toString(), query.get("propertyValue").toString());
		} else {
			List<String> attributeValues = options.constructAttributeValues(query.get("attributeValue").toString());
			result = dssQuery.query(query.get("entityType").toString(), QueryType.ATTRIBUTE,
					query.get("attribute").toString(), attributeValues);
		}
		return new JSONCreator(result).getJSON();
	}

	private String doDownload(OptionParser options) throws IOException {
		JSONObject endpoints = options.getEndpoints();
		JSONObject download = options.getDownload();

		DataStoreDownload dssDownload = new DataStoreDownload(endpoints.get("dss").toString(),
				endpoints.get("sessionToken").toString());

		String downloadType = download.get("downloadType").toString();
		String permID = download.get("permID").toString();
		String source = download.get("source").toString();
		String dest = download.get("dest").toString();

		String downloadInfo = "";
		if (downloadType.equals("file")) {
			dssDownload.downloadSingleFile(permID, source, dest);
			downloadInfo = downloadInfo + "Download file " + permID + "#" + source + " into " + dest;
		} else if (downloadType.equals("folder")) {
			dssDownload.downloadFolder(permID, source, dest);
			downloadInfo = downloadInfo + "Download folder " + permID + "#" + source + " into " + dest;
		} else if (downloadType.equals("dataset")) {
			dssDownload.downloadDataSetFiles(permID, dest);
			downloadInfo = downloadInfo + "Download dataset files of " + permID + " into " + dest;
		} else {
			downloadInfo = downloadInfo + "Invalid download type, nothing to download";
		}
		return ("{\"download_info\":" + "\"" + downloadInfo + "\"" + "}");
	}

	private String doLogin(OptionParser options) throws Exception {

		JSONObject account = options.getAccount();
		JSONObject endpoints = options.getEndpoints();
		Authentication au = new Authentication(endpoints.get("as").toString(), account.get("username").toString(),
				account.get("password").toString());
		String sessionToken = au.sessionToken();
		return ("{\"token\":" + "\"" + sessionToken + "\"" + "}");
	}

	//
	//
	// try {
	// JSONObject endpoints = options.getEndpoints();
	// JSONObject query = options.getQuery();
	//
	// ApplicationServerQuery asQuery = new
	// ApplicationServerQuery(endpoints.get("as").toString(),
	// endpoints.get("sessionToken").toString());
	// List result;
	// if
	// (query.get("queryType").toString().equals(QueryType.PROPERTY.toString()))
	// {
	// result = asQuery.query(query.get("entityType").toString(),
	// QueryType.PROPERTY,
	// query.get("property").toString(), query.get("propertyValue").toString());
	// } else {
	// List<String> attributeValues =
	// options.constructAttributeValues(query.get("attributeValue").toString());
	// result = asQuery.query(query.get("entityType").toString(),
	// QueryType.ATTRIBUTE,
	// query.get("attribute").toString(), attributeValues);
	// }
	// String jsonResult = new JSONCreator(result).getJSON();
	// System.out.println(jsonResult);
	// } catch (Exception ex) {
	// System.err.println(ex.getMessage());
	// ex.printStackTrace();
	// System.exit(-1);
	// }
	// System.exit(0);
	// }

	protected void exit(int code) {
		System.exit(code);
	}

}

package org.fairdom;

import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.Experiment;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.SampleType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        final ObjectMapper mapper;

	public OpenSeekEntry(String args[]) {
		this.args = args;
                this.mapper = new ObjectMapper();
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

	protected String doApplicationServerQuery(OptionParser options) throws InvalidOptionException, AuthenticationException, JsonProcessingException {
		JSONObject endpoints = options.getEndpoints();
		JSONObject query = options.getQuery();

		ApplicationServerQuery asQuery = new ApplicationServerQuery(endpoints.get("as").toString(),
				endpoints.get("sessionToken").toString());
		List<? extends Object> result;
                
                QueryType queryType = QueryType.valueOf(query.get("queryType").toString());
                
                switch(queryType) {
                    case ALL:
                                    result = asQuery.allEntities(query.get("entityType").toString());
                                    
                                    if (query.get("entityType").equals("SampleType")) {
                                        return mapToJsonString("sampletypes",result);
                                    } 
                                    if (query.get("entityType").equals("DataSetType")) {
                                        return mapToJsonString("datasettypes",result);
                                    } 
                                    if (query.get("entityType").equals("ExperimentType")) {
                                        return mapToJsonString("experimenttypes",result);
                                    }                                    
                                    
                                    break;
                    case PROPERTY: result = asQuery.query(query.get("entityType").toString(), QueryType.PROPERTY,
					query.get("property").toString(), query.get("propertyValue").toString());
                                        break;
                    case ATTRIBUTE: 
                                    List<String> attributeValues = options.constructAttributeValues(query.get("attributeValue").toString());
                                    result = asQuery.query(query.get("entityType").toString(), QueryType.ATTRIBUTE,
					query.get("attribute").toString(), attributeValues);
                                    if (query.get("entityType").equals("SampleType")) {
                                        return mapToJsonString("sampletypes",result);
                                    }
                                    if (query.get("entityType").equals("DataSetType")) {
                                        return mapToJsonString("datasettypes",result);
                                    }
                                    if (query.get("entityType").equals("ExperimentType")) {
                                        return mapToJsonString("experimenttypes",result);
                                    }
                                    
                                    break;
                    case TYPE:
                                    if (query.get("entityType").equals("Sample")) {
                                        List<Sample> samples = asQuery.samplesByType(query);
                                        return new JSONCreator(samples).getJSON();
                                    } else if (query.get("entityType").equals("DataSet")) {
                                        List<DataSet> sets = asQuery.dataSetsByType(query);
                                        return new JSONCreator(sets).getJSON();
                                    } else if (query.get("entityType").equals("Experiment")) {
                                        List<Experiment> exps = asQuery.experimentsByType(query);
                                        return new JSONCreator(exps).getJSON();
                                    } else {
                                        throw new InvalidOptionException("Type query for unsupported type: "+query.get("entityType"));
                                    }
                        
                    case SEMANTIC:
                                    if (query.get("entityType").equals("SampleType")) {
                                        List<SampleType> types = asQuery.sampleTypesBySemantic(query);
                                        return mapToJsonString("sampletypes",types);
                                    } else {
                                        throw new InvalidOptionException("Semantic query for unsupported type: "+query.get("entityType"));
                                    }
                    default: throw new InvalidOptionException("Unrecognized query type: "+queryType);
                        
                }
                /*
		if (query.get("queryType").toString().equals(QueryType.PROPERTY.toString())) {
			result = asQuery.query(query.get("entityType").toString(), QueryType.PROPERTY,
					query.get("property").toString(), query.get("propertyValue").toString());
		} else {
			List<String> attributeValues = options.constructAttributeValues(query.get("attributeValue").toString());
			result = asQuery.query(query.get("entityType").toString(), QueryType.ATTRIBUTE,
					query.get("attribute").toString(), attributeValues);
		}*/
		return new JSONCreator(result).getJSON();
	}

	protected String doDataStoreQuery(OptionParser options) throws InvalidOptionException {
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

    protected String mapToJsonString(String listName, List<?> objects) throws JsonProcessingException {
        
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS"); 
        //necessary to make writer as df is not thread safe so cannot be set globabaly
        
        ObjectWriter writer = mapper.writer(df);
        Map<String,List<?>> map = new HashMap<>();
        map.put(listName,objects);
        return writer.writeValueAsString(map);
    }

}

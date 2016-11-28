package org.fairdom;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.DataSetFile;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.fetchoptions.DataSetFileFetchOptions;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.search.DataSetFileSearchCriteria;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.json.GenericObjectMapper;

/**
 * Created by quyennguyen on 13/02/15.
 */
public class DataStoreQuery extends DataStoreStream{
  
    public DataStoreQuery(String startEndpoint, String startSessionToken) {
		super(startEndpoint, startSessionToken);
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
        OptionParser options = null;
        try {
            options = new OptionParser(args);
        } catch (InvalidOptionException e) {
            System.err.println("Invalid option: " + e.getMessage());
            System.exit(-1);
		} catch (ParseException pe) {
			System.out.println("position: " + pe.getPosition());
			System.out.println(pe);
			System.exit(-1);
		}

        try {        	
        	JSONObject endpoints = options.getEndpoints();
        	JSONObject query = options.getQuery();           
            DataStoreQuery dssQuery = new DataStoreQuery(endpoints.get("dss").toString(), endpoints.get("sessionToken").toString());
        	List result;        	
        	if (query.get("queryType").toString().equals(QueryType.PROPERTY.toString())) {
            	result = dssQuery.query(query.get("entityType").toString(), QueryType.PROPERTY,query.get("property").toString(), query.get("propertyValue").toString());            	
            }
            else {
            	List<String> attributeValues = options.constructAttributeValues(query.get("attributeValue").toString()); 
            	result = dssQuery.query(query.get("entityType").toString(), QueryType.ATTRIBUTE,query.get("attribute").toString(), attributeValues);   
            }            	
            String jsonResult = dssQuery.jsonResult(result);
            System.out.println(jsonResult);
            
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            System.exit(-1);
        }
        System.exit(0);
    }  
	
	public List query(String type, QueryType queryType, String key, String value) throws InvalidOptionException {
        List result = null;
        if (queryType==QueryType.PROPERTY) {
        	if (type.equals("DataSetFile")){
                result = datasetFilesByProperty(key, value);            
            }else{
                throw new InvalidOptionException("Unrecognised type: " + type);
            }
        }
        else if (queryType==QueryType.ATTRIBUTE) {
        	if (type.equals("DataSetFile")) {
                result = datasetFilesByAttribute(key, value);
            }else {
                throw new InvalidOptionException("Unrecognised type: " + type);
            }
        }
        
        return result;
    }
    
    public List query(String type, QueryType queryType, String key, List<String> values) throws InvalidOptionException {
        List result = null;
        if (queryType==QueryType.ATTRIBUTE) {
        	if (type.equals("DataSetFile")) {
                result = datasetFilesByAttribute(key, values);
            }else {
                throw new InvalidOptionException("Unrecognised type: " + type);
            }
        }
        
        return result;
    }

    public String jsonResult(List result){
    	Map<String, Object> map = new HashMap<String, Object>();
    	for (Object item : result) {    		
    		if (item instanceof DataSetFile) {
    			if (!map.containsKey("datasetfiles")) {
    				map.put("datasetfiles", new ArrayList<Object>());
    			}
    			((List)map.get("datasetfiles")).add(jsonMap((DataSetFile)item));    			
    		}    		
    	}
        GenericObjectMapper mapper = new GenericObjectMapper();        
        StringWriter sw = new StringWriter();
        try {
            mapper.writeValue(sw, map);
        }catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return sw.toString();
    }  
    

    public String jsonResult(SearchResult result){
        GenericObjectMapper mapper = new GenericObjectMapper();
        StringWriter sw = new StringWriter();
        try {
            mapper.writeValue(sw, result);
        }catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return sw.toString();
    }
    
   public List <DataSetFile> datasetFilesByProperty(String property, String propertyValue){
    	 DataSetFileSearchCriteria criteria = new DataSetFileSearchCriteria();
    	 criteria.withDataSet().withProperty(property).thatContains(propertyValue);     	 
         
  	     SearchResult<DataSetFile> result = dss.searchFiles(sessionToken, criteria, new DataSetFileFetchOptions());
         List<DataSetFile> searchFiles = result.getObjects();
         return searchFiles;
    }

   public List<DataSetFile> datasetFilesByAttribute(String attribute, List<String> values) throws InvalidOptionException{  
       
     //FIXME: ability to search by OR operator, through set of PermID         
  	 //criteria.withOperator(SearchOperator.OR);       
     //for now loop through the permids  
	   List<DataSetFile> result = new ArrayList<DataSetFile>();
	   for (String value : values) {
		   DataSetFileSearchCriteria criteria = new DataSetFileSearchCriteria(); 
			criteria.withDataSet().withPermId().thatContains(value);
			
			SearchResult<DataSetFile> files_result = dss.searchFiles(sessionToken, criteria, new DataSetFileFetchOptions());
			result.addAll(files_result.getObjects());
			
   	   }       	
       return result;
       
   }

   public List<DataSetFile> datasetFilesByAttribute(String attribute, String value) throws InvalidOptionException{
       List<String> values = new ArrayList<String>(Arrays.asList(new String[]{value}));
       return datasetFilesByAttribute(attribute,values);
   }
         
   private Map<String,Object> jsonMap(DataSetFile datasetFile) {
   	Map<String,Object> map = new HashMap<String, Object>();
   	map.put("dataset", datasetFile.getDataSetPermId().getPermId());
   	map.put("filePermId", datasetFile.getPermId());
   	map.put("path", datasetFile.getPath());
   	map.put("isDirectory", datasetFile.isDirectory());
   	map.put("fileLength", datasetFile.getFileLength());
   	return map;
   }
}

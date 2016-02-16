package org.fairdom;

import java.io.StringWriter;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult;
import ch.ethz.sis.openbis.generic.dssapi.v3.IDataStoreServerApi;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.DataSetFile;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.search.DataSetFileSearchCriteria;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.json.GenericObjectMapper;

/**
 * Created by quyennguyen on 13/02/15.
 */
public class DataStoreQuery {    
    private IDataStoreServerApi dss;
    private String sessionToken;

    public DataStoreQuery(IDataStoreServerApi startDss, String startSessionToken ){        
        dss = startDss;
        sessionToken = startSessionToken;
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
        	JSONObject account = options.getAccount();
        	JSONObject endpoints = options.getEndpoints();
        	JSONObject query = options.getQuery();
            Authentication au = new Authentication(endpoints.get("as").toString(), endpoints.get("dss").toString(), account.get("username").toString(), account.get("password").toString());
            IDataStoreServerApi dss = au.dss();
            String sessionToken = au.sessionToken();
            
            DataStoreQuery dssQuery = new DataStoreQuery(dss, sessionToken);
            List <DataSetFile> dataSetFiles= dssQuery.dataSetFile(query.get("property").toString(), query.get("propertyValue").toString());  
            SearchResult<DataSetFile> result = new SearchResult<DataSetFile>(dataSetFiles, dataSetFiles.size());
            String jsonResult = dssQuery.jsonResult(result); 
            System.out.println(jsonResult);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            System.exit(-1);
        }
        System.exit(0);
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
    
   public List <DataSetFile> dataSetFile(String property, String propertyValue){
    	 DataSetFileSearchCriteria criteria = new DataSetFileSearchCriteria();
    	 criteria.withDataSet().withProperty(property).thatEquals(propertyValue);

         List<DataSetFile> searchFiles = dss.searchFiles(sessionToken, criteria);
         return searchFiles;
    }

}

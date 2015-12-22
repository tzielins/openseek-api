package org.fairdom;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import ch.ethz.sis.openbis.generic.dss.api.v3.IDataStoreServerApi;
import ch.ethz.sis.openbis.generic.dss.api.v3.dto.download.DataSetFileDownload;
import ch.ethz.sis.openbis.generic.dss.api.v3.dto.download.DataSetFileDownloadOptions;
import ch.ethz.sis.openbis.generic.dss.api.v3.dto.download.DataSetFileDownloadReader;
import ch.ethz.sis.openbis.generic.dss.api.v3.dto.entity.datasetfile.DataSetFile;
import ch.ethz.sis.openbis.generic.dss.api.v3.dto.id.datasetfile.DataSetFilePermId;
import ch.ethz.sis.openbis.generic.dss.api.v3.dto.search.DataSetFileSearchCriteria;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.search.SearchResult;
import ch.systemsx.cisd.openbis.generic.shared.api.v3.json.GenericObjectMapper;

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
        }

        try {
            Authentication au = new Authentication(options.getAsEndpoint(), options.getDssEndpoint(), options.getUsername(), options.getPassword());
            IDataStoreServerApi dss = au.dss();
            String sessionToken = au.sessionToken();
            
            DataStoreQuery dssQuery = new DataStoreQuery(dss, sessionToken);
            List <DataSetFile> dataSetFiles= dssQuery.dataSetFile(options.getProperty(), options.getPropertyValue());  
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
    
    public String downloadDataSetFile(List<DataSetFile> files) throws IOException{
    	 DataSetFileDownloadOptions options = new DataSetFileDownloadOptions();
         List<DataSetFilePermId> fileIds = new ArrayList<DataSetFilePermId> ();
         for (int i = 0; i < files.size(); i++) {
         	fileIds.add(files.get(i).getPermId());
         	}
         
 		 InputStream stream = dss.downloadFiles(sessionToken, fileIds, options);
         DataSetFileDownloadReader reader = new DataSetFileDownloadReader(stream);
        

         DataSetFileDownload download = null;
         String content = new String();
         while ((download = reader.read()) != null)         {                         
             content = IOUtils.toString(download.getInputStream());
         }
         return content;
   }

}

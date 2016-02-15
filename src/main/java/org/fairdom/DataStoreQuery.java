package org.fairdom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.IOUtils;

import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.id.DataSetPermId;
import ch.ethz.sis.openbis.generic.dssapi.v3.IDataStoreServerApi;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.DataSetFile;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.download.DataSetFileDownload;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.download.DataSetFileDownloadOptions;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.download.DataSetFileDownloadReader;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.DataSetFilePermId;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.IDataSetFileId;
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
        }

        try {
            Authentication au = new Authentication(options.getAsEndpoint(), options.getDssEndpoint(), options.getUsername(), options.getPassword());
            IDataStoreServerApi dss = au.dss();
            String sessionToken = au.sessionToken();
            
            DataStoreQuery dssQuery = new DataStoreQuery(dss, sessionToken);
            List <DataSetFile> dataSetFiles= dssQuery.dataSetFile(options.getProperty(), options.getPropertyValue());  
            SearchResult<DataSetFile> result = new SearchResult<DataSetFile>(dataSetFiles, dataSetFiles.size());
            String jsonResult = dssQuery.jsonResult(result);            
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

   
   public void downloadSingleFile(String permId, String source, String destination)throws IOException{
	   DataSetFileDownloadOptions options = new DataSetFileDownloadOptions();
	   options.setRecursive(false);
	   IDataSetFileId fileToDownload = new DataSetFilePermId(new DataSetPermId(permId), source);

	   InputStream stream = dss.downloadFiles(sessionToken, Arrays.asList(fileToDownload), options);
	   DataSetFileDownloadReader reader = new DataSetFileDownloadReader(stream);
	   DataSetFileDownload file = null;
	   file = reader.read();
	   InputStream inputStream = file.getInputStream();
	   	   
	   File outputFile = new File(destination);
	   OutputStream fileOutputStream = new FileOutputStream(outputFile);
       
	   IOUtils.copyLarge(inputStream, fileOutputStream);
	   fileOutputStream.close();	   
   }

}

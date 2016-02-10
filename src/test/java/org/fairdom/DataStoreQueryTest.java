package org.fairdom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult;
import ch.ethz.sis.openbis.generic.dssapi.v3.IDataStoreServerApi;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.DataSetFile;
/**
 * Created by quyennguyen on 13/02/15.
 */
public class DataStoreQueryTest {
    
    protected IDataStoreServerApi dss;
    protected String sessionToken;

    @Before
    public void setUp() throws AuthenticationException{
        Authentication au = new Authentication("https://openbis-api.fair-dom.org/openbis/openbis", 
        		"https://openbis-api.fair-dom.org/datastore_server", 
        		"apiuser",
        		"apiuser");
        dss = au.dss();
        sessionToken = au.sessionToken();
    }
    
	@Test
    public void getDataSetFileWithSeekDataFileID() throws Exception {
		DataStoreQuery query = new DataStoreQuery(dss, sessionToken);
        String property = "SEEK_DATAFILE_ID";
        String propertyValue = "DataFile_1";
        List <DataSetFile> files = query.dataSetFile(property, propertyValue);
        DataSetFile file = files.get(files.size() - 1);
        String perID = "20151217153943290-5#original/api-test";   
        long fileLength = 25;        
        assertEquals(perID, file.getPermId().toString());
        assertEquals(fileLength, file.getFileLength());
	}        

	@Test
    public void downloadDataSetFileWithSeekDataFileID() throws Exception {
		DataStoreQuery query = new DataStoreQuery(dss, sessionToken);
        String property = "SEEK_DATAFILE_ID";
        String propertyValue = "DataFile_1";
        List <DataSetFile> files = query.dataSetFile(property, propertyValue);        
        String content = query.downloadDataSetFile(files);
        assertEquals("Just for testing purpose", content.replaceAll("\\s+$", ""));
    }
	
    @Test
    public void jsonResultforDataSetFile() throws Exception {
    	DataStoreQuery query = new DataStoreQuery(dss, sessionToken);
        String property = "SEEK_DATAFILE_ID";
        String propertyValue = "DataFile_1";
        List <DataSetFile> files = query.dataSetFile(property, propertyValue);
        SearchResult<DataSetFile> result = new SearchResult<DataSetFile>(files, files.size());
        String jsonResult = query.jsonResult(result);
        assertTrue(jsonResult.matches("(.*)20151217153943290-5(.*)"));
    }  
}

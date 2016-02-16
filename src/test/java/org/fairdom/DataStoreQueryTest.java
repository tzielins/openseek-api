package org.fairdom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult;
import ch.ethz.sis.openbis.generic.dssapi.v3.IDataStoreServerApi;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.DataSetFile;
import ch.systemsx.cisd.common.parser.MemorySizeFormatter;

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
    public void downloadSingleFile() throws Exception {
		DataStoreQuery query = new DataStoreQuery(dss, sessionToken);
        String permId = "20151217153943290-5";
        String source = "original/api-test";
        String basePath = new File("").getAbsolutePath();
        String destination = basePath + "/src/test/java/resources/api-test";
        
        File file = new File(destination); 
        if (file.exists()){
        	file.delete();
        }     
        assertFalse(file.exists());
        
        query.downloadSingleFile(permId, source, destination);
        
        assertTrue(file.exists());        
        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        assertEquals(25, attr.size());

    }
	
	@Test
    public void downloadUtf8File() throws Exception {
		DataStoreQuery query = new DataStoreQuery(dss, sessionToken);
        String permId = "20160210130359377-22";
        String source = "original/utf8.txt";
        String basePath = new File("").getAbsolutePath();
        String destination = basePath + "/src/test/java/resources/utf8.txt";
        
        File file = new File(destination); 
        if (file.exists()){
        	file.delete();
        }     
        assertFalse(file.exists());
        
        query.downloadSingleFile(permId, source, destination);
        
        assertTrue(file.exists());        
        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        assertEquals(49, attr.size());        

    }
	
	@Test
    public void downloadChineseCharatersFile() throws Exception {
		DataStoreQuery query = new DataStoreQuery(dss, sessionToken);
        String permId = "20160212141703195-28";
        String source = "original/chinese";
        String basePath = new File("").getAbsolutePath();
        String destination = basePath + "/src/test/java/resources/chinese";
        
        File file = new File(destination); 
        if (file.exists()){
        	file.delete();
        }     
        assertFalse(file.exists());
        
        query.downloadSingleFile(permId, source, destination);
        
        assertTrue(file.exists());        
        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        assertEquals(44, attr.size());

    }
	
	@Test
    public void downloadWesternEncodeFile() throws Exception {
		DataStoreQuery query = new DataStoreQuery(dss, sessionToken);
        String permId = "20160212140647105-27";
        String source = "original/western.txt";
        String basePath = new File("").getAbsolutePath();
        String destination = basePath + "/src/test/java/resources/western.txt";
        
        File file = new File(destination); 
        if (file.exists()){
        	file.delete();
        }     
        assertFalse(file.exists());
        
        query.downloadSingleFile(permId, source, destination);
        
        assertTrue(file.exists());        
        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        assertEquals(44, attr.size());

    }
	
	@Test
    public void downloadImageFile() throws Exception {
		DataStoreQuery query = new DataStoreQuery(dss, sessionToken);
        String permId = "20160210130454955-23";
        String source = "original/autumn.jpg";
        String basePath = new File("").getAbsolutePath();
        String destination = basePath + "/src/test/java/resources/autumn.jpg";
        
        File file = new File(destination); 
        if (file.exists()){
        	file.delete();
        }     
        assertFalse(file.exists());
        
        query.downloadSingleFile(permId, source, destination);
        
        assertTrue(file.exists());        
        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        assertEquals("537k", MemorySizeFormatter.format(attr.size()));

    }

/*	
 * Comment this test out, to avoid time for downloading a big file
    @Test
    public void downloadSingleLargeFile() throws Exception {
		DataStoreQuery query = new DataStoreQuery(dss, sessionToken);
        String permId = "20160212120108123-26";
        String source = "original/SEEK-v0.23.0.ova";
        String basePath = new File("").getAbsolutePath();
        String destination = basePath + "/src/test/java/resources/SEEK-v0.23.0.ova";
        
        File file = new File(destination); 
        if (file.exists()){
        	file.delete();
        }     
        assertFalse(file.exists());
        
        query.downloadSingleFile(permId, source, destination);
        
        assertTrue(file.exists());        
        BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
        
        assertEquals("2.3gb", MemorySizeFormatter.format(attr.size()));

    }
	*/
	
	@Test
    public void downloadFolder() throws Exception {
		DataStoreQuery query = new DataStoreQuery(dss, sessionToken);
        String permId = "20160215111736723-31";
        String sourceRelativeFolder = "original/DEFAULT";
        String basePath = new File("").getAbsolutePath();
        String destinationFolder = basePath + "/src/test/java/resources/";
        
        
        File file = new File(destinationFolder + sourceRelativeFolder); 
        if (file.exists()){
        	FileUtils.deleteDirectory(file);
        }    
      
        query.downloadFolder(permId, sourceRelativeFolder, destinationFolder);
                       
        Path path = Paths.get(destinationFolder + sourceRelativeFolder);
        DirectoryStream<Path> stream = Files.newDirectoryStream(path);        
        List<String> filesInFolder = new ArrayList<String>();
        for (Path outputFile: stream) {
            filesInFolder.add(outputFile.getFileName().toString());
        }
        assertEquals("fairdom-logo-compact.svg", filesInFolder.get(0));
        assertEquals("Stanford_et_al-2015-Molecular_Systems_Biology.pdf", filesInFolder.get(1));
    }
	
	@Test
    public void downloadDataSetFiles() throws Exception {
		DataStoreQuery query = new DataStoreQuery(dss, sessionToken);
        String permId = "20151217153943290-5";        
        String sourceRelativeFolder = "original";
        String basePath = new File("").getAbsolutePath();
        String destinationFolder = basePath + "/src/test/java/resources/";
        
        
        File file = new File(destinationFolder + sourceRelativeFolder); 
        if (file.exists()){
        	FileUtils.deleteDirectory(file);
        }    
      
        query.downloadDataSetFiles(permId, destinationFolder);
                       
        Path path = Paths.get(destinationFolder + sourceRelativeFolder);
        DirectoryStream<Path> stream = Files.newDirectoryStream(path);        
        List<String> filesInFolder = new ArrayList<String>();
        for (Path outputFile: stream) {
            filesInFolder.add(outputFile.getFileName().toString());
        }
        assertEquals("api-test", filesInFolder.get(0));        
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

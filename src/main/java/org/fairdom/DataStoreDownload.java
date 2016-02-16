package org.fairdom;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.id.DataSetPermId;
import ch.ethz.sis.openbis.generic.dssapi.v3.IDataStoreServerApi;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.DataSetFile;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.download.DataSetFileDownload;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.download.DataSetFileDownloadOptions;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.download.DataSetFileDownloadReader;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.DataSetFilePermId;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.id.IDataSetFileId;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.search.DataSetFileSearchCriteria;
import ch.systemsx.cisd.common.spring.HttpInvokerUtils;
import ch.systemsx.cisd.common.ssl.SslCertificateHelper;

/**
 * Created by quyennguyen on 13/02/15.
 */
public class DataStoreDownload {    
	private static String endpoint;
    private static String sessionToken;
    private static IDataStoreServerApi dss;

    public DataStoreDownload(String startEndpoint, String startSessionToken ){        
        endpoint = startEndpoint;
        sessionToken = startSessionToken;
        dss = DataStoreDownload.dss(endpoint);
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
        	JSONObject download = options.getDownload();
                        
            DataStoreDownload dssDownload = new DataStoreDownload(endpoints.get("dss").toString(), endpoints.get("sessionToken").toString());
            
            String downloadType = download.get("type").toString();
            String permID = download.get("permID").toString();
            String source = download.get("source").toString();
            String dest = download.get("dest").toString();
            
            if (downloadType.equals("file")){
            	dssDownload.downloadSingleFile(permID, source, dest);
            	System.out.println("Download file " + permID + "#" + source + " into " + dest);
            }else if (downloadType.equals("folder")){
            	dssDownload.downloadFolder(permID, source, dest);
            	System.out.println("Download folder " + permID + "#" + source + " into " + dest);
            }else if (downloadType.equals("dataset")){
            	dssDownload.downloadDataSetFiles(permID, dest);
            	System.out.println("Download dataset files of " + permID + " into " + dest);
            }else{
            	 System.out.println("Invalid download type, nothing to download");
            }
            
            
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            System.exit(-1);
        }
        System.exit(0);
    } 
    
    public static IDataStoreServerApi dss(String endpoint) {
        SslCertificateHelper.trustAnyCertificate(endpoint);
        IDataStoreServerApi dss = HttpInvokerUtils
        		.createStreamSupportingServiceStub(IDataStoreServerApi.class, endpoint
        				+ IDataStoreServerApi.SERVICE_URL, 500000);
        return dss;
    }
    
   public void downloadSingleFile(String permId, String sourceRelative, String destination)throws IOException{
	   DataSetFileDownloadOptions options = new DataSetFileDownloadOptions();
	   options.setRecursive(false);
	   IDataSetFileId fileToDownload = new DataSetFilePermId(new DataSetPermId(permId), sourceRelative);

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
   
   public void downloadFolder(String permId, String sourceRelativeFolder, String destinationFolder)throws IOException{
	   DataSetFileDownloadOptions options = new DataSetFileDownloadOptions();
	   options.setRecursive(true);
	   IDataSetFileId filesToDownload = new DataSetFilePermId(new DataSetPermId(permId), sourceRelativeFolder);

	   InputStream stream = dss.downloadFiles(sessionToken, Arrays.asList(filesToDownload), options);
	   DataSetFileDownloadReader reader = new DataSetFileDownloadReader(stream);
	   DataSetFileDownload file = null;
	   
	   while ((file = reader.read()) != null)	   
	   {
		   InputStream inputStream = file.getInputStream();
		   DataSetFile dataSetFile = file.getDataSetFile();
		   if (dataSetFile.isDirectory()){
			   Path dir = Paths.get(destinationFolder + dataSetFile.getPath());			   
			   Files.createDirectories(dir);
		   }else{
			   File outputFile = new File(destinationFolder + dataSetFile.getPath());
			   OutputStream fileOutputStream = new FileOutputStream(outputFile);
		       
			   IOUtils.copyLarge(inputStream, fileOutputStream);
			   fileOutputStream.close();
		   }	   
	   }
   }

   
   public void downloadDataSetFiles(String permId, String destinationFolder)throws IOException{
	   DataSetFileDownloadOptions options = new DataSetFileDownloadOptions();
	   options.setRecursive(true);
	   
	   DataSetFileSearchCriteria criteria = new DataSetFileSearchCriteria();
	   criteria.withDataSet().withCode().thatEquals(permId);
	   List<DataSetFile> files = dss.searchFiles(sessionToken, criteria);
	   
	   List<IDataSetFileId> filesToDownload = new LinkedList<IDataSetFileId>();
	   for (DataSetFile file : files)
		   filesToDownload.add(file.getPermId());	   
	   

	   InputStream stream = dss.downloadFiles(sessionToken, filesToDownload, options);
	   DataSetFileDownloadReader reader = new DataSetFileDownloadReader(stream);
	   DataSetFileDownload file = null;
	   
	   while ((file = reader.read()) != null)	   
	   {
		   InputStream inputStream = file.getInputStream();
		   DataSetFile dataSetFile = file.getDataSetFile();
		   if (dataSetFile.isDirectory()){
			   Path dir = Paths.get(destinationFolder + dataSetFile.getPath());			   
			   Files.createDirectories(dir);
		   }else{
			   File outputFile = new File(destinationFolder + dataSetFile.getPath());
			   OutputStream fileOutputStream = new FileOutputStream(outputFile);
		       
			   IOUtils.copyLarge(inputStream, fileOutputStream);
			   fileOutputStream.close();
		   }	   
	   }
   }


}

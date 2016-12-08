package org.fairdom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.apache.poi.util.TempFile;
import org.fairdom.testhelpers.JSONHelper;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Test;

/**
 * @author Stuart Owen
 */

public class OpenSeekEntryTest {
	class OpenSeekEntryWrapper extends OpenSeekEntry {
		private int exitCode=-99;

		public OpenSeekEntryWrapper(String[] args) {
			super(args); 
		}
		
		public int getExitCode() {
			return exitCode;
		}
		
		protected void exit(int code) {
			if (this.exitCode==-99) { //stop it being overwritten with a success code later
				this.exitCode=code;
			}			
		}
		
	}
	
	private static String token=null;
		
	private String as_endpoint="https://openbis-api.fair-dom.org/openbis/openbis";
	private String dss_endpoint="https://openbis-api.fair-dom.org/datastore_server";
	private PrintStream oldStream;
	
	private ByteArrayOutputStream outputStream;
	
	@Test
	public void doAsQuery() throws Exception {
		String token = getToken();
		String endpoints = "{\"as\":\""+as_endpoint+"\",\"sessionToken\":\""+token+"\"}";
		String query = "{\"entityType\":\"Space\", \"queryType\":\"ATTRIBUTE\", \"attribute\":\"permID\", \"attributeValue\":\"\"}";
		String [] args = new String[]{"-endpoints",endpoints,"-query",query};
		JSONObject jsonObj = doExecute(args);
		assertNotNull(jsonObj.get("spaces"));
	}	
	
	
	@Test
	public void testLogin() throws Exception {
		String account = "{\"username\":\"apiuser\", \"password\":\"apiuser\"}";
		String [] args = new String[]{"-account",account,"-endpoints","{\"as\":\""+as_endpoint+"\"}"};
		JSONObject jsonObj = doExecute(args);
		assertNotNull(jsonObj.get("token"));					
	}
	
	@Test
	public void doDSSQuery() throws Exception {
		String token = getToken();
		String endpoints = "{\"dss\":\""+dss_endpoint+"\",\"sessionToken\":\""+token+"\"}";
		String query = "{\"entityType\":\"DataSetFile\", \"queryType\":\"PROPERTY\", \"property\":\"SEEK_DATAFILE_ID\", \"propertyValue\":\"DataFile_1\"}";
		String [] args = new String[]{"-endpoints",endpoints,"-query",query};
		JSONObject jsonObj = doExecute(args);
		assertNotNull(jsonObj.get("datasetfiles"));
	}
	 
	@Test
	public void doDSDownload() throws Exception {
		File tempFile = TempFile.createTempFile("openseek-api-test", "dss");
		assertFalse(tempFile.exists());
		String token = getToken();
		String endpoints = "{\"dss\":\""+dss_endpoint+"\",\"sessionToken\":\""+token+"\"}";
		String download = "{\"downloadType\":\"file\", \"permID\":\"20160210130454955-23\", \"source\":\"original/autumn.jpg\", \"dest\":\""+tempFile.getAbsolutePath()+"\"}";
		String [] args = new String[]{"-endpoints",endpoints,"-download",download};
		JSONObject jsonObj = doExecute(args);
		assertNotNull(jsonObj.get("download_info"));
		assertTrue(tempFile.exists());
		tempFile.delete();		
	}	

	private JSONObject doExecute(String[] args) throws ParseException {
		OpenSeekEntryWrapper wrapper = new OpenSeekEntryWrapper(args);
		redirectSysOut();
		wrapper.execute();
		putSysOutBack();
		assertEquals(0,wrapper.exitCode);
		String json = outputStream.toString();
		JSONObject jsonObj=JSONHelper.processJSON(json);
		return jsonObj;
	}

	private String getToken() throws AuthenticationException {
		if (OpenSeekEntryTest.token==null) {
			Authentication au = new Authentication(as_endpoint, "apiuser", "apiuser");
			OpenSeekEntryTest.token = au.sessionToken();
	        assertNotNull(OpenSeekEntryTest.token);
		}
		
        return OpenSeekEntryTest.token;
	}
	
	
	
	private void putSysOutBack() {
		System.setOut(oldStream);
	}
	
	private void redirectSysOut() {
		outputStream = new ByteArrayOutputStream();
	    PrintStream ps = new PrintStream(outputStream);
	    oldStream=System.out;
	    System.setOut(ps);
	}
}


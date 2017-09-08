package org.fairdom;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Stuart Owen
 */

public class OpenSeekEntryTest {
    
    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();
    
	class OpenSeekEntryWrapper extends OpenSeekEntry {
		private int exitCode = -99;

		public OpenSeekEntryWrapper(String[] args) {
			super(args);
		}

		public int getExitCode() {
			return exitCode;
		}

		protected void exit(int code) {
			if (this.exitCode == -99) { // stop it being overwritten with a
										// success code later
				this.exitCode = code;
			}
		}

	}

	private static String token = null;

	private String as_endpoint = "https://openbis-api.fair-dom.org/openbis/openbis";
	private String dss_endpoint = "https://openbis-api.fair-dom.org/datastore_server";
	private PrintStream oldStream;

	private ByteArrayOutputStream outputStream;
        
        @Test
        public void dataSetEntityHasRichDetails() throws Exception {
		String token = getToken();
		String endpoints = "{\"as\":\"" + as_endpoint + "\",\"sessionToken\":\"" + token + "\"}";
		String query = "{\"entityType\":\"DataSet\", \"queryType\":\"ATTRIBUTE\", \"attribute\":\"permID\", \"attributeValue\":\"20170907185702684-36\"}";
		String[] args = new String[] { "-endpoints", endpoints, "-query", query };

                OptionParser options = new OptionParser(args);
                
                OpenSeekEntry client = new OpenSeekEntry(args);
                String res = client.doApplicationServerQuery(options);
                assertNotNull(res);
                //System.out.println(res);
                
                JSONObject jsonObj = JSONHelper.processJSON(res);
                
                assertNotNull(jsonObj.get("datasets"));
                List<JSONObject> sets = (List<JSONObject>)jsonObj.get("datasets");
                
                assertEquals(1,sets.size());
                JSONObject set = sets.get(0);
                assertEquals("2017-09-07 17:57:03.040768",set.get("registrationDate"));
                assertEquals("apiuser",set.get("registerator"));
                
                JSONObject prop = (JSONObject) set.get("properties");
                assertNotNull(prop);
                assertEquals("TOMEK test set",prop.getOrDefault("NAME", "missing"));
                assertTrue(prop.getOrDefault("DESCRIPTION", "").toString().contains("rich metadata"));
                
        }

	@Test
	public void doAsQuery() throws Exception {
		String token = getToken();
		String endpoints = "{\"as\":\"" + as_endpoint + "\",\"sessionToken\":\"" + token + "\"}";
		String query = "{\"entityType\":\"Space\", \"queryType\":\"ATTRIBUTE\", \"attribute\":\"permID\", \"attributeValue\":\"\"}";
		String[] args = new String[] { "-endpoints", endpoints, "-query", query };
		JSONObject jsonObj = doExecute(args);
		assertNotNull(jsonObj.get("spaces"));
	}

	@Test
	public void testLogin() throws Exception {
		String account = "{\"username\":\"apiuser\", \"password\":\"apiuser\"}";
		String[] args = new String[] { "-account", account, "-endpoints", "{\"as\":\"" + as_endpoint + "\"}" };
		JSONObject jsonObj = doExecute(args);
		assertNotNull(jsonObj.get("token"));
	}

	@Test
	public void doDSSQuery() throws Exception {
		String token = getToken();
		String endpoints = "{\"dss\":\"" + dss_endpoint + "\",\"sessionToken\":\"" + token + "\"}";
		String query = "{\"entityType\":\"DataSetFile\", \"queryType\":\"PROPERTY\", \"property\":\"SEEK_DATAFILE_ID\", \"propertyValue\":\"DataFile_1\"}";
		String[] args = new String[] { "-endpoints", endpoints, "-query", query };
		JSONObject jsonObj = doExecute(args);
		assertNotNull(jsonObj.get("datasetfiles"));
	}

	@Test
	public void doDSSQueryByDataSetPermId() throws Exception {
		String token = getToken();
		String endpoints = "{\"dss\":\"" + dss_endpoint + "\",\"sessionToken\":\"" + token + "\"}";
		String query = "{\"entityType\":\"DataSetFile\", \"queryType\":\"ATTRIBUTE\", \"attribute\":\"dataSetPermId\", \"attributeValue\":\"20151217153943290-5\"}";
		String[] args = new String[] { "-endpoints", endpoints, "-query", query };
		JSONObject jsonObj = doExecute(args);
		assertNotNull(jsonObj.get("datasetfiles"));
	}

	@Test
	public void doDSDownload() throws Exception {
		File tempFile = testFolder.newFile(); //TempFile.createTempFile("openseek-api-test", "dss");
		assertFalse(tempFile.length() > 0);
		String token = getToken();
		String endpoints = "{\"dss\":\"" + dss_endpoint + "\",\"sessionToken\":\"" + token + "\"}";
		String download = "{\"downloadType\":\"file\", \"permID\":\"20160210130454955-23\", \"source\":\"original/autumn.jpg\", \"dest\":\""
				+ tempFile.getAbsolutePath() + "\"}";
		String[] args = new String[] { "-endpoints", endpoints, "-download", download };
		JSONObject jsonObj = doExecute(args);
		assertNotNull(jsonObj.get("download_info"));
		assertTrue(tempFile.length() > 0);
	}

	private JSONObject doExecute(String[] args) throws ParseException {
		OpenSeekEntryWrapper wrapper = new OpenSeekEntryWrapper(args);
		redirectSysOut();
		wrapper.execute();
		putSysOutBack();
		assertEquals(0, wrapper.exitCode);
		String json = outputStream.toString();
		JSONObject jsonObj = JSONHelper.processJSON(json);
		return jsonObj;
	}

	private String getToken() throws AuthenticationException {
		if (OpenSeekEntryTest.token == null) {
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
		oldStream = System.out;
		System.setOut(ps);
	}
}

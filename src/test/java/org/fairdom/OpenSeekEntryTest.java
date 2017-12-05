package org.fairdom;

import com.fasterxml.jackson.databind.ObjectMapper;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONArray;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Ignore;
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

	private final String as_endpoint = "https://openbis-api.fair-dom.org/openbis/openbis";
	private final String dss_endpoint = "https://openbis-api.fair-dom.org/datastore_server";
	private PrintStream oldStream;

	private ByteArrayOutputStream outputStream;
        
	@Before
	public void setUpSSL() throws AuthenticationException {
            SslCertificateHelper.addTrustedUrl("https://openbis-api.fair-dom.org/openbis/openbis");   
            SslCertificateHelper.addTrustedUrl("https://127.0.0.1:8443/openbis/openbis");            
        }
        
        String localEndpoint() throws AuthenticationException {
            String localAs = "https://127.0.0.1:8443/openbis/openbis";
            Authentication au = new Authentication(localAs, "seek", "seek");

            String token = au.sessionToken();
            String endpoint = "{\"as\":\"" + localAs + "\",\"sessionToken\":\"" + token + "\"}";            
            return endpoint;
        }
        
        @Test
        public void dataSetEntityHasRichDetails() throws Exception {
		String token = getToken();
		String endpoints = "{\"as\":\"" + as_endpoint + "\",\"sessionToken\":\"" + token + "\"}";
		String query = "{\"entityType\":\"DataSet\", \"queryType\":\"ATTRIBUTE\", \"attribute\":\"permID\", \"attributeValue\":\"20170907185702684-36\"}";
		//String query = "{\"entityType\":\"DataSet\", \"queryType\":\"ATTRIBUTE\", \"attribute\":\"permID\", \"attributeValue\":\"20171002172401546-38\"}";
		//String query = "{\"entityType\":\"DataSet\", \"queryType\":\"ATTRIBUTE\", \"attribute\":\"permID\", \"attributeValue\":\"20171002190934144-40\"}";
		//String query = "{\"entityType\":\"DataSet\", \"queryType\":\"ATTRIBUTE\", \"attribute\":\"permID\", \"attributeValue\":\"20171004182824553-41\"}";
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
        @Ignore
        public void sampleEntityHasRichDetails() throws Exception {
		String token = getToken();
		String endpoints = "{\"as\":\"" + as_endpoint + "\",\"sessionToken\":\"" + token + "\"}";
		String query = "{\"entityType\":\"Sample\", \"queryType\":\"ATTRIBUTE\", \"attribute\":\"permID\", \"attributeValue\":\"20171002172111346-37\"}";
		//String query = "{\"entityType\":\"Sample\", \"queryType\":\"ATTRIBUTE\", \"attribute\":\"permID\", \"attributeValue\":\"20171002172639055-39\"}";
                
		String[] args = new String[] { "-endpoints", endpoints, "-query", query };

                OptionParser options = new OptionParser(args);
                
                OpenSeekEntry client = new OpenSeekEntry(args);
                String res = client.doApplicationServerQuery(options);
                assertNotNull(res);
                //System.out.println(res);
                
                JSONObject jsonObj = JSONHelper.processJSON(res);
                
                assertNotNull(jsonObj.get("samples"));
                List<JSONObject> samples = (List<JSONObject>)jsonObj.get("samples");
                
                assertEquals(1,samples.size());
                JSONObject sam = samples.get(0);
                assertEquals("2017-10-02 16:21:11.346421",sam.get("registrationDate"));
                assertEquals("apiuser",sam.get("registerator"));
                
                JSONObject prop = (JSONObject) sam.get("properties");
                assertNotNull(prop);
                assertEquals("Tomek First",prop.getOrDefault("NAME", "missing"));
                assertTrue(prop.getOrDefault("DESCRIPTION", "").toString().contains("assay"));
                
                JSONArray sets = (JSONArray)sam.get("datasets");
                assertNotNull(sets);
                assertTrue(sets.contains("20171002172401546-38"));
            
        }

        @Test
        public void samplesByType() throws Exception {
		String token = getToken();
		String endpoints = "{\"as\":\"" + as_endpoint + "\",\"sessionToken\":\"" + token + "\"}";
            
                
                Map<String,String> qMap = new HashMap<>();
                qMap.put("entityType", "Sample");
                qMap.put("queryType",QueryType.TYPE.name());
                qMap.put("typeCode","TZ_FAIR_ASSAY");

                ObjectMapper mapper = new ObjectMapper();
                String query = mapper.writeValueAsString(qMap);
                //System.out.println("Query:\n"+query);
                
		String[] args = new String[] { "-endpoints", endpoints, "-query", query };

                OptionParser options = new OptionParser(args);
                
                OpenSeekEntry client = new OpenSeekEntry(args);
                String res = client.doApplicationServerQuery(options);
                assertNotNull(res);
                //System.out.println("Res:\n"+res);
                
                JSONObject jsonObj = JSONHelper.processJSON(res);
                
                assertNotNull(jsonObj.get("samples"));
                List<JSONObject> samples = (List<JSONObject>)jsonObj.get("samples");
                
                assertEquals(2,samples.size());
                
                samples.forEach( s -> {
                    assertEquals("TZ_FAIR_ASSAY", ((JSONObject) s.get("sample_type")).get("code"));
                });
                
        }
        
        @Test
        public void sampleTypesCanBeSearchedBySemanticAnnotations() throws Exception {
            
		String endpoints = localEndpoint();
                
                Map<String,String> qMap = new HashMap<>();
                qMap.put("entityType", "SampleType");
                qMap.put("queryType",QueryType.SEMANTIC.name());
                qMap.put("predicateAccessionId","is_a");
                qMap.put("descriptorAccessionId","assay");

                ObjectMapper mapper = new ObjectMapper();
                String query = mapper.writeValueAsString(qMap);
                //System.out.println("Query:\n"+query);
                
                
		String[] args = new String[] { "-endpoints", endpoints, "-query", query };

                OptionParser options = new OptionParser(args);
                
                OpenSeekEntry client = new OpenSeekEntry(args);
                String res = client.doApplicationServerQuery(options);
                assertNotNull(res);
                //System.out.println("Res:\n"+res);
                
                JSONObject jsonObj = JSONHelper.processJSON(res);
                
                assertNotNull(jsonObj.get("sampletypes"));
                List<JSONObject> sampletypes = (List<JSONObject>)jsonObj.get("sampletypes");
                
                assertEquals(2,sampletypes.size());
                JSONObject sam = sampletypes.get(0);
                assertEquals("UNKNOWN",((JSONObject)sam.get("permId")).get("permId"));
                assertEquals("UNKNOWN",sam.get("code"));
                assertEquals("2017-11-20 17:34:28.861",sam.get("modificationDate"));

                sam = sampletypes.get(1);
                assertEquals("TZ_ASSAY",((JSONObject)sam.get("permId")).get("permId"));
                assertEquals("TZ_ASSAY",sam.get("code"));
                assertEquals("2017-11-21 15:33:36.531",sam.get("modificationDate"));

                
            
        }
        
        @Test
        public void allEntities() throws Exception {
            
            
		String token = getToken();
		String endpoints = "{\"as\":\"" + as_endpoint + "\",\"sessionToken\":\"" + token + "\"}";
            
                ObjectMapper mapper = new ObjectMapper();

                Map<String,String> qMap = new HashMap<>();
                qMap.put("queryType",QueryType.ALL.name());
                
                List<String> types = Arrays.asList("Sample","DataSet","Experiment","Space");
                for (String type : types) {
                    qMap.put("entityType", type);

                    String query = mapper.writeValueAsString(qMap);
                    //System.out.println("Query:\n"+query);
                
                    String[] args = new String[] { "-endpoints", endpoints, "-query", query };

                    OptionParser options = new OptionParser(args);
                
                    OpenSeekEntry client = new OpenSeekEntry(args);
                    String res = client.doApplicationServerQuery(options);
                    
                    assertNotNull(res);
                    //System.out.println("Res:\n"+res);
                
                    JSONObject jsonObj = JSONHelper.processJSON(res);
                
                    String key = type.toLowerCase()+"s";
                    assertNotNull(jsonObj.get(key));
                    List<JSONObject> ent = (List<JSONObject>)jsonObj.get(key);
                    assertFalse(ent.isEmpty());
                
                }                
        }
        
        
        @Test
        @Ignore
        public void fetchingFilesJSON() throws Exception {
		String token = getToken();
		String endpoints = "{\"dss\":\"" + dss_endpoint + "\",\"sessionToken\":\"" + token + "\"}";
		String query = "{\"entityType\":\"DataSetFile\", \"queryType\":\"ATTRIBUTE\", \"attribute\":\"dataSetPermId\", \"attributeValue\":\"20171002190934144-40\"}";
		String[] args = new String[] { "-endpoints", endpoints, "-query", query };
            

                OptionParser options = new OptionParser(args);
                
                OpenSeekEntry client = new OpenSeekEntry(args);
                String res = client.doDataStoreQuery(options);
                assertNotNull(res);
                
                //System.out.println(res);
                
                JSONObject jsonObj = JSONHelper.processJSON(res);
                
                assertNotNull(jsonObj.get("datasetfiles"));
        }
        
        

	@Test
        @Ignore
	public void doAsQuery() throws Exception {
		String token = getToken();
		String endpoints = "{\"as\":\"" + as_endpoint + "\",\"sessionToken\":\"" + token + "\"}";
		String query = "{\"entityType\":\"Space\", \"queryType\":\"ATTRIBUTE\", \"attribute\":\"permID\", \"attributeValue\":\"\"}";
		String[] args = new String[] { "-endpoints", endpoints, "-query", query };
		JSONObject jsonObj = doExecute(args);
		assertNotNull(jsonObj.get("spaces"));
	}

	@Test
        @Ignore
	public void testLogin() throws Exception {
		String account = "{\"username\":\"apiuser\", \"password\":\"apiuser\"}";
		String[] args = new String[] { "-account", account, "-endpoints", "{\"as\":\"" + as_endpoint + "\"}" };
		JSONObject jsonObj = doExecute(args);
		assertNotNull(jsonObj.get("token"));
	}

	@Test
        @Ignore
	public void doDSSQuery() throws Exception {
		String token = getToken();
		String endpoints = "{\"dss\":\"" + dss_endpoint + "\",\"sessionToken\":\"" + token + "\"}";
		String query = "{\"entityType\":\"DataSetFile\", \"queryType\":\"PROPERTY\", \"property\":\"SEEK_DATAFILE_ID\", \"propertyValue\":\"DataFile_1\"}";
		String[] args = new String[] { "-endpoints", endpoints, "-query", query };
		JSONObject jsonObj = doExecute(args);
		assertNotNull(jsonObj.get("datasetfiles"));
	}

	@Test
        @Ignore
	public void doDSSQueryByDataSetPermId() throws Exception {
		String token = getToken();
		String endpoints = "{\"dss\":\"" + dss_endpoint + "\",\"sessionToken\":\"" + token + "\"}";
		String query = "{\"entityType\":\"DataSetFile\", \"queryType\":\"ATTRIBUTE\", \"attribute\":\"dataSetPermId\", \"attributeValue\":\"20151217153943290-5\"}";
		String[] args = new String[] { "-endpoints", endpoints, "-query", query };
		JSONObject jsonObj = doExecute(args);
		assertNotNull(jsonObj.get("datasetfiles"));
	}

	@Test
        @Ignore
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
                System.out.println("----------");
                System.out.println(json);
                System.out.println("----------");
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

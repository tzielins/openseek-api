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

	//private final String as_endpoint = "https://openbis-api.fair-dom.org/openbis/openbis";
	//private final String dss_endpoint = "https://openbis-api.fair-dom.org/datastore_server";
	private final String as_endpoint = "https://127.0.0.1:8443/openbis/openbis";
	private final String dss_endpoint = "https://127.0.0.1:8444/datastore_server";
	private PrintStream oldStream;

	private ByteArrayOutputStream outputStream;
        
	@Before
	public void setUpSSL() throws AuthenticationException {
            //SslCertificateHelper.addTrustedUrl("https://openbis-api.fair-dom.org/openbis/openbis");   
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
                String id = "20180418145905365-49";
		String query = "{\"entityType\":\"DataSet\", \"queryType\":\"ATTRIBUTE\", \"attribute\":\"permID\", \"attributeValue\":\""+id+"\"}";
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
                //assertEquals("2017-09-07 17:57:03.040768",set.get("registrationDate"));
                //assertEquals("apiuser",set.get("registerator"));
                
                JSONObject prop = (JSONObject) set.get("properties");
                assertNotNull(prop);
                assertEquals("TOMEK test set",prop.getOrDefault("NAME", "missing"));
                //assertTrue(prop.getOrDefault("DESCRIPTION", "").toString().contains("rich metadata"));
                
        }
        
        @Test
        public void datasetByType() throws Exception {
		String token = getToken();
		String endpoints = "{\"as\":\"" + as_endpoint + "\",\"sessionToken\":\"" + token + "\"}";
            
                String typeN = "RAW_DATA";
                Map<String,String> qMap = new HashMap<>();
                qMap.put("entityType", "DataSet");
                qMap.put("queryType",QueryType.TYPE.name());
                qMap.put("typeCode",typeN);

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
                
                assertNotNull(jsonObj.get("datasets"));
                List<JSONObject> sets = (List<JSONObject>)jsonObj.get("datasets");
                
                assertEquals(2,sets.size());
                
                sets.forEach( s -> {
                    assertEquals(typeN, ((JSONObject) s.get("dataset_type")).get("code"));
                });
                
        }
        
        @Test
        public void datasetsByTypes() throws Exception {
            
		String endpoints = localEndpoint();
            
                
                Map<String,String> qMap = new HashMap<>();
                qMap.put("entityType", "DataSet");
                qMap.put("queryType",QueryType.TYPE.name());
                qMap.put("typeCodes","RAW_DATA,UNKOWN");

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
                
                assertNotNull(jsonObj.get("datasets"));
                List<JSONObject> sets = (List<JSONObject>)jsonObj.get("datasets");
                
                assertEquals(2,sets.size());
                
                List<String> exp = Arrays.asList("RAW_DATA","UNKOWN");
                sets.forEach( s -> {
                    assertTrue(exp.contains(((JSONObject) s.get("dataset_type")).get("code")));
                });                
                
                
        }
        
        
        @Test
        //@Ignore
        public void sampleEntityHasRichDetails() throws Exception {
            String endpoints = localEndpoint();
            String id = "20180418154046965-53";

            //String token = getToken();
            //String endpoints = "{\"as\":\"" + as_endpoint + "\",\"sessionToken\":\"" + token + "\"}";
            String query = "{\"entityType\":\"Sample\", \"queryType\":\"ATTRIBUTE\", \"attribute\":\"permID\", \"attributeValue\":\""+id+"\"}";
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
                //assertEquals("2017-10-02 16:21:11.346421",sam.get("registrationDate"));
                //assertEquals("apiuser",sam.get("registerator"));
                
                JSONObject prop = (JSONObject) sam.get("properties");
                assertNotNull(prop);
                assertEquals("Purification",prop.getOrDefault("NAME", "missing"));
                assertTrue(prop.getOrDefault("EXPERIMENTAL_GOALS", "").toString().contains("<head>"));
                
                JSONArray sets = (JSONArray)sam.get("datasets");
                assertNotNull(sets);
                assertTrue(sets.contains("20180418165410152-56"));
            
        }

        @Test
        public void samplesByType() throws Exception {
		String token = getToken();
		String endpoints = "{\"as\":\"" + as_endpoint + "\",\"sessionToken\":\"" + token + "\"}";
            
                String typeN = "EXPERIMENTAL_STEP";
                Map<String,String> qMap = new HashMap<>();
                qMap.put("entityType", "Sample");
                qMap.put("queryType",QueryType.TYPE.name());
                qMap.put("typeCode",typeN);

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
                    assertEquals(typeN, ((JSONObject) s.get("sample_type")).get("code"));
                });
                
        }
        
        @Test
        public void samplesByTypes() throws Exception {
            
		String endpoints = localEndpoint();
            
                
                Map<String,String> qMap = new HashMap<>();
                qMap.put("entityType", "Sample");
                qMap.put("queryType",QueryType.TYPE.name());
                qMap.put("typeCodes","UNKNOWN,EXPERIMENTAL_STEP");

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
                
                assertEquals(3,samples.size());
                
                List<String> exp = Arrays.asList("UNKNOWN","EXPERIMENTAL_STEP");
                samples.forEach( s -> {
                    assertTrue(exp.contains(((JSONObject) s.get("sample_type")).get("code")));
                });                
                
                
        }
        
        
        @Test
        public void allSampleTypes() throws Exception {
		String token = getToken();
		String endpoints = "{\"as\":\"" + as_endpoint + "\",\"sessionToken\":\"" + token + "\"}";
                          
                Map<String,String> qMap = new HashMap<>();
                qMap.put("entityType", "SampleType");
                qMap.put("queryType",QueryType.ALL.name());

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
                
                assertEquals(24,sampletypes.size());
        }
        
        @Test
        public void sampleTypesByCode() throws Exception {
		String token = getToken();
		String endpoints = "{\"as\":\"" + as_endpoint + "\",\"sessionToken\":\"" + token + "\"}";
                          
                String typeN = "EXPERIMENTAL_STEP";
                Map<String,String> qMap = new HashMap<>();
                qMap.put("entityType", "SampleType");
                qMap.put("queryType",QueryType.ATTRIBUTE.name());
                qMap.put("attribute","CODE");
                qMap.put("attributeValue",typeN);

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
                assertEquals(1,sampletypes.size());
                assertEquals(typeN,sampletypes.get(0).get("code"));
        }        
        
        @Test
        @Ignore("Seemantic annotations not supported yet")
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
                JSONObject sam = sampletypes.get(1);
                assertEquals("UNKNOWN",((JSONObject)sam.get("permId")).get("permId"));
                assertEquals("UNKNOWN",sam.get("code"));
                assertEquals("2017-11-20 17:34:28.861",sam.get("modificationDate"));

                sam = sampletypes.get(0);
                assertEquals("TZ_ASSAY",((JSONObject)sam.get("permId")).get("permId"));
                assertEquals("TZ_ASSAY",sam.get("code"));
                assertEquals("2017-11-21 15:33:36.531",sam.get("modificationDate"));

                
            
        }
        
        @Test
        public void allDataSetTypes() throws Exception {
		String token = getToken();
		String endpoints = "{\"as\":\"" + as_endpoint + "\",\"sessionToken\":\"" + token + "\"}";
                   
		//endpoints = localEndpoint();
                
                Map<String,String> qMap = new HashMap<>();
                qMap.put("entityType", "DataSetType");
                qMap.put("queryType",QueryType.ALL.name());

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
                
                assertNotNull(jsonObj.get("datasettypes"));
                List<JSONObject> types = (List<JSONObject>)jsonObj.get("datasettypes");
                
                assertEquals(6,types.size());
        }
        
        @Test
        public void dataSetTypesByCode() throws Exception {
		String token = getToken();
		String endpoints = "{\"as\":\"" + as_endpoint + "\",\"sessionToken\":\"" + token + "\"}";
                   
                String typeN = "RAW_DATA";
                Map<String,String> qMap = new HashMap<>();
                qMap.put("entityType", "DataSetType");
                qMap.put("queryType",QueryType.ATTRIBUTE.name());
                qMap.put("attribute","CODE");
                qMap.put("attributeValue",typeN);

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
                
                assertNotNull(jsonObj.get("datasettypes"));
                List<JSONObject> types = (List<JSONObject>)jsonObj.get("datasettypes");                
                assertEquals(1,types.size());
                assertEquals(typeN,types.get(0).get("code"));
        }        
        
        @Test
        public void allExperimentTypes() throws Exception {
		String token = getToken();
		String endpoints = "{\"as\":\"" + as_endpoint + "\",\"sessionToken\":\"" + token + "\"}";
                          
                Map<String,String> qMap = new HashMap<>();
                qMap.put("entityType", "ExperimentType");
                qMap.put("queryType",QueryType.ALL.name());

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
                
                assertNotNull(jsonObj.get("experimenttypes"));
                List<JSONObject> types = (List<JSONObject>)jsonObj.get("experimenttypes");
                
                assertEquals(6,types.size());
        }
        
        @Test
        public void experimentTypesByCode() throws Exception {
		//String token = getToken();
		//String endpoints = "{\"as\":\"" + as_endpoint + "\",\"sessionToken\":\"" + token + "\"}";
                         
                String endpoints = localEndpoint();
                Map<String,String> qMap = new HashMap<>();
                qMap.put("entityType", "ExperimentType");
                qMap.put("queryType",QueryType.ATTRIBUTE.name());
                qMap.put("attribute","CODE");
                qMap.put("attributeValue","DEFAULT_EXPERIMENT");

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
                
                assertNotNull(jsonObj.get("experimenttypes"));
                List<JSONObject> types = (List<JSONObject>)jsonObj.get("experimenttypes");                
                assertEquals(1,types.size());
                assertEquals("DEFAULT_EXPERIMENT",types.get(0).get("code"));
                
                qMap.put("attributeValue","COLLECTION,DEFAULT_EXPERIMENT,");   
                query = mapper.writeValueAsString(qMap);
                args = new String[] { "-endpoints", endpoints, "-query", query };

                options = new OptionParser(args);                
                client = new OpenSeekEntry(args);
                res = client.doApplicationServerQuery(options);
                
                assertNotNull(res);
                //System.out.println("Res:\n"+res);
                
                jsonObj = JSONHelper.processJSON(res);
                
                assertNotNull(jsonObj.get("experimenttypes"));
                types = (List<JSONObject>)jsonObj.get("experimenttypes");                
                assertEquals(2,types.size());
                
        }        
        
        @Test
        public void experimentsByTypes() throws Exception {
            
		String endpoints = localEndpoint();
            
                
                Map<String,String> qMap = new HashMap<>();
                qMap.put("entityType", "Experiment");
                qMap.put("queryType",QueryType.TYPE.name());
                qMap.put("typeCodes","DEFAULT_EXPERIMENT,UNKNOWN");

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
                
                assertNotNull(jsonObj.get("experiments"));
                List<JSONObject> objs = (List<JSONObject>)jsonObj.get("experiments");
                
                assertEquals(3,objs.size());
                
                List<String> exp = Arrays.asList("DEFAULT_EXPERIMENT","UNKNOWN");
                objs.forEach( s -> {
                    assertTrue(exp.contains(((JSONObject) s.get("experiment_type")).get("code")));
                });                
                
                
        }
        
        
        
        @Test
        public void allEntities() throws Exception {
            
            
		String token = getToken();
		String endpoints = "{\"as\":\"" + as_endpoint + "\",\"sessionToken\":\"" + token + "\"}";
            
                //endpoints = localEndpoint();
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
                    // System.out.println("Res:\n"+res);
                
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
            String id = "20171002190934144-40";
            id = "20180418165410152-56";
		String token = getToken();
		String endpoints = "{\"dss\":\"" + dss_endpoint + "\",\"sessionToken\":\"" + token + "\"}";
		String query = "{\"entityType\":\"DataSetFile\", \"queryType\":\"ATTRIBUTE\", \"attribute\":\"dataSetPermId\", \"attributeValue\":\""+id+"\"}";
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
        //@Ignore
	public void doAsQuery() throws Exception {
		String token = getToken();
		String endpoints = "{\"as\":\"" + as_endpoint + "\",\"sessionToken\":\"" + token + "\"}";
		String query = "{\"entityType\":\"Space\", \"queryType\":\"ATTRIBUTE\", \"attribute\":\"permID\", \"attributeValue\":\"\"}";
		String[] args = new String[] { "-endpoints", endpoints, "-query", query };
		JSONObject jsonObj = doExecute(args);
		assertNotNull(jsonObj.get("spaces"));
	}

	@Test
        //@Ignore
	public void testLogin() throws Exception {
                String login =  "seek";
                String pass = "seek";
		String account = "{\"username\":\""+login+"\", \"password\":\""+pass+"\"}";
		String[] args = new String[] { "-account", account, "-endpoints", "{\"as\":\"" + as_endpoint + "\"}" };
		JSONObject jsonObj = doExecute(args);
		assertNotNull(jsonObj.get("token"));
	}

	@Test
        //@Ignore
	public void doDSSQuery() throws Exception {
		String token = getToken();
		String endpoints = "{\"dss\":\"" + dss_endpoint + "\",\"sessionToken\":\"" + token + "\"}";
		String query = "{\"entityType\":\"DataSetFile\", \"queryType\":\"PROPERTY\", \"property\":\"NAME\", \"propertyValue\":\"TOMEK test set\"}";
		String[] args = new String[] { "-endpoints", endpoints, "-query", query };
		JSONObject jsonObj = doExecute(args);
		assertNotNull(jsonObj.get("datasetfiles"));
	}

	@Test
        //@Ignore
	public void doDSSQueryByDataSetPermId() throws Exception {
		String token = getToken();
                String id = "20151217153943290-5";
                id = "20180418165410152-56";
		String endpoints = "{\"dss\":\"" + dss_endpoint + "\",\"sessionToken\":\"" + token + "\"}";
		String query = "{\"entityType\":\"DataSetFile\", \"queryType\":\"ATTRIBUTE\", \"attribute\":\"dataSetPermId\", \"attributeValue\":\""+id+"\"}";
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
			//Authentication au = new Authentication(as_endpoint, "apiuser", "apiuser");
                        Authentication au = new Authentication(as_endpoint, "seek", "seek");
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

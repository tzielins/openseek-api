package org.fairdom;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;

import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.Experiment;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.SampleType;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.space.Space;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.junit.Ignore;

/**
 * @author Quyen Nugyen
 * @author Stuart Owen
 */
public class ApplicationServerQueryTest {

	private String endpoint;
	private String sessionToken;
	private ApplicationServerQuery query;

	@Before
	public void setUp() throws AuthenticationException {
            SslCertificateHelper.addTrustedUrl("https://openbis-api.fair-dom.org/openbis/openbis");   
            SslCertificateHelper.addTrustedUrl("https://127.0.0.1:8443/openbis/openbis");            
            
            Authentication au = new Authentication("https://openbis-api.fair-dom.org/openbis/openbis", "apiuser",
			"apiuser");
            sessionToken = au.sessionToken();
            endpoint = "https://openbis-api.fair-dom.org/openbis/openbis";
            query = new ApplicationServerQuery(endpoint, sessionToken);
	}
        
        protected ApplicationServerQuery localQuery() throws AuthenticationException {
                String localAs = "https://127.0.0.1:8443/openbis/openbis";
                Authentication au = new Authentication(localAs, "seek", "seek");
                return new ApplicationServerQuery(localAs, au.sessionToken());
        }

	@Test
        @Ignore
	public void queryBySpace() throws Exception {
		List<? extends Object> result = query.query("Space", QueryType.ATTRIBUTE, "permID", "");
		assertTrue(result.size() > 0);

		List<String> values = new ArrayList<String>();
		values.add("");
		result = query.query("Space", QueryType.ATTRIBUTE, "permID", values);
		assertTrue(result.size() > 0);
	}

	@Test
        @Ignore
	public void getSpacesByPermIDs() throws Exception {
		List<String> permids = new ArrayList<String>();
		permids.add("API-SPACE");
		permids.add("DEFAULT");
		List<Space> spaces = query.spacesByAttribute("permId", permids);
		assertEquals(2, spaces.size());
		String json = new JSONCreator(spaces).getJSON();
		assertTrue(JSONHelper.isValidJSON(json));
	}

	@Test
        @Ignore
	public void getSpacesByPermID() throws Exception {
		List<String> permids = new ArrayList<String>();
		permids.add("API-SPACE");
		List<Space> spaces = query.spacesByAttribute("permId", permids);
		assertEquals(1, spaces.size());
		String json = new JSONCreator(spaces).getJSON();
		assertTrue(JSONHelper.isValidJSON(json));

		spaces = query.spacesByAttribute("permId", "API-SPACE");
		assertEquals(1, spaces.size());
		json = new JSONCreator(spaces).getJSON();
		JSONObject jsonObj = JSONHelper.processJSON(json);
		assertNotNull(jsonObj.get("spaces"));
		JSONObject space = (JSONObject) ((JSONArray) jsonObj.get("spaces")).get(0);
		JSONArray experiments = (JSONArray) space.get("experiments");
		JSONArray datasets = (JSONArray) space.get("datasets");
		JSONArray projects = (JSONArray) space.get("projects");
		assertEquals(1, experiments.size());
		assertEquals("20151216143716562-2", (String) experiments.get(0));

		assertEquals(1, projects.size());
		assertEquals("20151216135152196-1", (String) projects.get(0));
		assertEquals(12, datasets.size());
		assertTrue(datasets.contains("20160210130359377-22"));
	}
        
	@Test
        @Ignore
	public void getSpacesByPermIDGetsAllDataSetsExperimentAndSamplesOnes() throws Exception {

		List<Space> spaces = query.spacesByAttribute("permId", "API-SPACE");
		assertEquals(1, spaces.size());
                
		String json = new JSONCreator(spaces).getJSON();
		JSONObject jsonObj = JSONHelper.processJSON(json);
		assertNotNull(jsonObj.get("spaces"));
		JSONObject space = (JSONObject) ((JSONArray) jsonObj.get("spaces")).get(0);
		JSONArray experiments = (JSONArray) space.get("experiments");
		JSONArray datasets = (JSONArray) space.get("datasets");
		JSONArray projects = (JSONArray) space.get("projects");
		assertEquals(1, experiments.size());
		assertEquals("20151216143716562-2", (String) experiments.get(0));

		assertEquals(1, projects.size());
		assertEquals("20151216135152196-1", (String) projects.get(0));
		assertEquals(12, datasets.size());
		assertTrue(datasets.contains("20160210130359377-22"));
		assertTrue(datasets.contains("20170907185702684-36"));
		assertTrue(datasets.contains("20171002190934144-40"));
	}        

	@Test
        @Ignore
	public void getAllSpaces() throws Exception {
		List<Space> spaces = query.spacesByAttribute("permId", "");
		assertTrue(spaces.size() > 0);
		String json = new JSONCreator(spaces).getJSON();
		assertTrue(JSONHelper.isValidJSON(json));
	}
        
	@Test
	public void allSpacesGivesAll() throws Exception {
            List<Space> spaces = query.allSpaces();
            //spaces.forEach( s -> System.out.println(s.getCode()));
            assertEquals(2,spaces.size());
	}    
        
        @Test 
        public void allEntitiesWorks() throws InvalidOptionException {
            List<String> types = Arrays.asList("Sample","DataSet","Experiment","Space");
            for (String type : types)
                assertFalse(query.allEntities(type).isEmpty());
        }

	@Test
        @Ignore
	public void getExperimentsByPermID() throws Exception {
		List<String> permids = new ArrayList<String>();
		permids.add("20151216143716562-2");
		List<Experiment> experiments = query.experimentsByAttribute("permId", permids);
		assertEquals(1, experiments.size());
		String json = new JSONCreator(experiments).getJSON();
		assertTrue(JSONHelper.isValidJSON(json));

		experiments = query.experimentsByAttribute("permId", "20151216143716562-2");
		assertEquals(1, experiments.size());
		json = new JSONCreator(experiments).getJSON();
		assertTrue(JSONHelper.isValidJSON(json));
	}

	@Test
        @Ignore
	public void getExperimentsByPermIDs() throws Exception {
		List<String> permids = new ArrayList<String>();
		permids.add("20151216112932823-1");
		permids.add("20151216143716562-2");
		List<Experiment> experiments = query.experimentsByAttribute("permId", permids);
		assertEquals(2, experiments.size());
		String json = new JSONCreator(experiments).getJSON();
		assertTrue(JSONHelper.isValidJSON(json));
	}

	@Test
        @Ignore
	public void getAllExperiments() throws Exception {

		List<Experiment> experiments = query.experimentsByAttribute("permId", "");
		assertTrue(experiments.size() > 0);
		String json = new JSONCreator(experiments).getJSON();
		assertTrue(JSONHelper.isValidJSON(json));
	}
        
	@Test
	public void allExperimentsGetsAll() throws Exception {
            List<Experiment> experiments = query.allExperiments();
            assertEquals(2,experiments.size());
	}        

	@Test
	public void getAllSamples() throws Exception {
		List<Sample> samples = query.samplesByAttribute("permId", "");
		assertTrue(samples.size() > 0);
		String json = new JSONCreator(samples).getJSON();
		assertTrue(JSONHelper.isValidJSON(json));
	}
        
        @Test
	public void allSamplesGivesAll() throws Exception {
		List<Sample> samples = query.allSamples();
                assertEquals(4,samples.size());
	}        

	@Test
	public void getAllDatasets() throws Exception {
		List<DataSet> data = query.dataSetsByAttribute("permId", "");
		assertTrue(data.size() > 0);
		String json = new JSONCreator(data).getJSON();
		assertTrue(JSONHelper.isValidJSON(json));
	}
        
        @Test
        public void allDatasetsGivesAll() throws Exception {
            List<DataSet> data = query.allDatasets();
            assertEquals(12,data.size());
        }

	@Test
        @Ignore
	public void getDatasetByAttribute() throws Exception {
		List<DataSet> data = query.dataSetsByAttribute("permId", "20151217153943290-5");
		String json = new JSONCreator(data).getJSON();
		assertTrue(JSONHelper.isValidJSON(json));
		assertEquals(1, data.size());
	}

	@Test
        @Ignore
	public void getDatasetsByAttribute() throws Exception {
		List<String> values = new ArrayList<String>();
		values.add("20151217153943290-5");
		values.add("20160210130359377-22");
		List<DataSet> data = query.dataSetsByAttribute("permId", values);
		String json = new JSONCreator(data).getJSON();
		assertTrue(JSONHelper.isValidJSON(json));
		assertEquals(2, data.size());
	}
        
        @Test
        public void getsDataSetWithRichMetadata() throws Exception {
            
            String setId = "20170907185702684-36";
            List<DataSet> sets = query.dataSetsByAttribute("permId", setId);
            assertEquals(1,sets.size());
            
            DataSet set = sets.get(0);
            assertEquals(setId,set.getPermId().getPermId());
            
            LocalDateTime reg = LocalDateTime.of(2017,9,7,17,57,3);
           
            assertEquals(reg.toLocalDate(),
                    set.getRegistrationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            
            Map<String,String> props = set.getProperties();
            //System.out.println(properties);
            assertEquals("TOMEK test set",props.getOrDefault("NAME", "missing"));
            assertTrue(props.getOrDefault("DESCRIPTION", "").contains("enhanced"));
            
        }
        
        @Test
        public void getsSamplesWithRichMetadata() throws Exception {
            
            String perId = "20171002172111346-37";
            List<Sample> res = query.samplesByAttribute("permId", perId);
            assertEquals(1,res.size());
            
            Sample sam = res.get(0);
            assertEquals(perId,sam.getPermId().getPermId());
            
            LocalDateTime reg = LocalDateTime.of(2017,10,2,16,21,11);
           
            assertEquals(reg.toLocalDate(),
                    sam.getRegistrationDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            
            Map<String,String> props = sam.getProperties();
            //System.out.println(properties);
            assertEquals("Tomek First",props.getOrDefault("NAME", "missing"));
            assertTrue(props.getOrDefault("DESCRIPTION", "").contains("assay"));
            
        }   
        
        @Test
        public void samplesByTypeCodeWorks() throws AuthenticationException, InvalidOptionException {
            
            Map<String,String> qMap = new HashMap<>();
            qMap.put("entityType", "Sample");
            qMap.put("queryType",QueryType.TYPE.name());
            qMap.put("typeCode","TZ_FAIR_ASSAY");
            
            JSONObject crit = new JSONObject(qMap);
            
            
            List<Sample> res = query.samplesByType(crit);
            assertNotNull(res);
            assertEquals(2, res.size());
            
            res.forEach( s -> {
                assertEquals("TZ_FAIR_ASSAY", s.getType().getCode());
            });            
            
            qMap.put("typeCode","TZ_ASSAY_NOT_DEFINED");           
            crit = new JSONObject(qMap);
            res = query.samplesByType(crit);
            
            assertTrue(res.isEmpty());
        }
        
        @Test
        public void samplesByMultipleTypeCodesWorks() throws AuthenticationException, InvalidOptionException {
            
            //local as it came from the new API
            query = localQuery();
            
            Map<String,Object> qMap = new HashMap<>();
            qMap.put("entityType", "Sample");
            qMap.put("queryType",QueryType.TYPE.name());
            qMap.put("typeCode",Arrays.asList("TZ_ASSAY","EXPERIMENTAL_STEP"));
            
            JSONObject crit = new JSONObject(qMap);
            
            
            List<Sample> res = query.samplesByType(crit);
            assertNotNull(res);
            assertEquals(4, res.size());
            
            List<String> exp = Arrays.asList("TZ_ASSAY","EXPERIMENTAL_STEP");
            res.forEach( s -> {
                assertTrue(exp.contains(s.getType().getCode()));
            });            
            
            qMap.put("typeCode",Arrays.asList("TZ_ASSAY_NOT_DEFINED"));           
            crit = new JSONObject(qMap);
            res = query.samplesByType(crit);
            
            assertTrue(res.isEmpty());
        }
        
        @Test
        public void allSampleTypes() throws AuthenticationException {
                        
            List<SampleType> res = query.allSampleTypes();
            assertNotNull(res);
            assertFalse(res.isEmpty());
            assertEquals(30,res.size());
            
        }
        
	@Test
	public void sampleTypesByCode() throws Exception {
		List<SampleType> res = query.sampleTypesByCode("TZ_FAIR_ASSAY");
                assertEquals(1, res.size());
                assertEquals("TZ_FAIR_ASSAY", res.get(0).getCode());
	}        
        
        @Test
        public void sampleTypesBySemanticSearchesUsingAllFields() throws AuthenticationException {
            
            query = localQuery();
            
            Map<String,String> qMap = new HashMap<>();
            qMap.put("entityType", "SampleType");
            qMap.put("queryType",QueryType.SEMANTIC.name());
            qMap.put("predicateOntologyId","po_id_t");
            qMap.put("predicateOntologyVersion","po_version_t");            
            qMap.put("predicateAccessionId","po_acc_t");
            qMap.put("descriptorOntologyId","do_id_t");            
            qMap.put("descriptorOntologyVersion","do_version_t");
            qMap.put("descriptorAccessionId","do_acc_t");            
            
            JSONObject crit = new JSONObject(qMap);
            
            
            List<SampleType> res = query.sampleTypesBySemantic(crit);
            assertNotNull(res);
            assertFalse(res.isEmpty());
            assertEquals("TZ_ASSAY",res.get(0).getCode());
            
            String[] fields = {"predicateOntologyId","predicateOntologyVersion","predicateAccessionId",
                                "descriptorOntologyId","descriptorOntologyVersion","descriptorAccessionId"};
            
            for (String field : fields) {
                HashMap wMap = new HashMap(qMap);
                wMap.put(field,"1");
                
                crit = new JSONObject(wMap);
                res = query.sampleTypesBySemantic(crit);
                assertNotNull(res);
                assertTrue(res.isEmpty());                
            }
        }
        
        @Test
        public void sampleTypesBySemanticSearchesUsingOnlySetFields() throws AuthenticationException {
            
            query = localQuery();
            
            Map<String,String> qMap = new HashMap<>();
            qMap.put("entityType", "SampleType");
            qMap.put("queryType",QueryType.SEMANTIC.name());
            qMap.put("predicateOntologyId","po_id_t");
            qMap.put("predicateOntologyVersion","po_version_t");            
            qMap.put("predicateAccessionId","po_acc_t");
            qMap.put("descriptorOntologyId","do_id_t");            
            qMap.put("descriptorOntologyVersion","do_version_t");
            qMap.put("descriptorAccessionId","do_acc_t");            
            
            JSONObject crit = new JSONObject(qMap);
            
            
            List<SampleType> res = query.sampleTypesBySemantic(crit);
            assertNotNull(res);
            assertFalse(res.isEmpty());
            assertEquals("TZ_ASSAY",res.get(0).getCode());
            
            String[] fields = {"predicateOntologyId","predicateOntologyVersion","predicateAccessionId",
                                "descriptorOntologyId","descriptorOntologyVersion","descriptorAccessionId"};
            
            for (String field : fields) {
                HashMap wMap = new HashMap(qMap);
                wMap.put(field,null);
                
                crit = new JSONObject(wMap);
                res = query.sampleTypesBySemantic(crit);
                assertNotNull(res);
                assertFalse(res.isEmpty());
                assertEquals("TZ_ASSAY",res.get(0).getCode());              
            }
        }        

	@Test
        @Ignore
	public void getExperimentWithSeekStudyID() throws Exception {
		String property = "SEEK_STUDY_ID";
		String propertyValue = "Study_1";
		List<Experiment> experiments = query.experimentsByProperty(property, propertyValue);
		assertEquals(1, experiments.size());
		Experiment experiment = experiments.get(0);
		assertEquals(propertyValue, experiment.getProperties().get(property));
	}

	@Test
        @Ignore
	public void getExperimentWithSeekStudyIDNoResult() throws Exception {
		String property = "SEEK_STUDY_ID";
		String propertyValue = "SomeID";
		List<Experiment> experiments = query.experimentsByProperty(property, propertyValue);
		assertEquals(0, experiments.size());
	}

	@Test
        @Ignore
	public void getSampleWithSeekAssayID() throws Exception {
		String property = "SEEK_ASSAY_ID";
		String propertyValue = "Assay_1";
		List<Sample> samples = query.samplesByProperty(property, propertyValue);
		assertEquals(1, samples.size());
		Sample sample = samples.get(0);

		assertEquals(propertyValue, sample.getProperties().get(property));
		// SampleIdentifier identifier = new
		// SampleIdentifier("/API_TEST/SAMPLE_1");
		// assertEquals(identifier, sample.getIdentifier());
	}

	@Test
        @Ignore
	public void getSampleWithSeekAssayIDNoResult() throws Exception {
		String property = "SEEK_ASSAY_ID";
		String propertyValue = "SomeID";
		List<Sample> samples = query.samplesByProperty(property, propertyValue);
		assertEquals(0, samples.size());
	}

	@Test
        @Ignore
	public void getDataSetWithSeekDataFileID() throws Exception {
		String property = "SEEK_DATAFILE_ID";
		String propertyValue = "DataFile_1";
		List<DataSet> dataSets = query.dataSetsByProperty(property, propertyValue);
		assertEquals(1, dataSets.size());
		DataSet dataSet = dataSets.get(0);

		assertEquals(propertyValue, dataSet.getProperties().get(property));
	}

	@Test
        @Ignore
	public void getDatasetWithSeekDataFileIDNoResult() throws Exception {
		String property = "SEEK_DATAFILE_ID";
		String propertyValue = "SomeID";
		List<DataSet> dataSets = query.dataSetsByProperty(property, propertyValue);
		assertEquals(0, dataSets.size());
	}

	@Test
	public void jsonResultforExperiment() throws Exception {
		String type = "Experiment";
		String property = "SEEK_STUDY_ID";
		String propertyValue = "Study_1";
		List<? extends Object> result = query.query(type, QueryType.PROPERTY, property, propertyValue);
		String jsonResult = new JSONCreator(result).getJSON();
		;
		assertTrue(jsonResult.matches("(.*)Study_1(.*)"));
	}

	@Test(expected = InvalidOptionException.class)
	public void unrecognizedType() throws Exception {
		String type = "SomeType";
		String property = "SEEK_STUDY_ID";
		String propertyValue = "Study_1";
		query.query(type, QueryType.PROPERTY, property, propertyValue);
	}

	@Test
	public void expirementsByAnyField() throws Exception {
		// project
		String searchTerm = "API-PROJECT";
		List<Experiment> experiments = query.experimentsByAnyField(searchTerm);
		assertTrue(experiments.size() > 0);

		// code
		searchTerm = "E2";
		experiments = query.experimentsByAnyField(searchTerm);
		assertTrue(experiments.size() > 0);

		// permID
		searchTerm = "20151216143716562-2";
		experiments = query.experimentsByAnyField(searchTerm);
		assertTrue(experiments.size() > 0);

		// type
		searchTerm = "TEST_TYPE";
		experiments = query.experimentsByAnyField(searchTerm);
		assertTrue(experiments.size() > 0);

		// property
		searchTerm = "Study_1";
		experiments = query.experimentsByAnyField(searchTerm);
		assertTrue(experiments.size() > 0);

		// tag
		searchTerm = "test_tag";
		experiments = query.experimentsByAnyField(searchTerm);
		assertTrue(experiments.size() > 0);

		// prefix
		searchTerm = "test_";
		experiments = query.experimentsByAnyField(searchTerm);
		assertTrue(experiments.size() > 0);

		// suffix
		searchTerm = "tag";
		experiments = query.experimentsByAnyField(searchTerm);
		assertTrue(experiments.size() > 0);

		// middle
		searchTerm = "st_ta";
		experiments = query.experimentsByAnyField(searchTerm);
		assertTrue(experiments.size() > 0);
	}

	@Test
	public void samplesByAnyField() throws Exception {
		// code
		String searchTerm = "S1";
		List<Sample> samples = query.samplesByAnyField(searchTerm);
		assertTrue(samples.size() > 0);

		// permID
		searchTerm = "20151216143743603-3";
		samples = query.samplesByAnyField(searchTerm);
		assertTrue(samples.size() > 0);

		// type
		searchTerm = "TEST_SAMPLE_TYPE";
		samples = query.samplesByAnyField(searchTerm);
		assertTrue(samples.size() > 0);

		// project -dont work
		// searchTerm = "API-PROJECT";
		// samples = query.samplesByAnyField(searchTerm);
		// assertTrue(samples.size() > 0);

		// experiment -dont work
		// searchTerm = "E2";
		// samples = query.samplesByAnyField(searchTerm);
		// assertTrue(samples.size() > 0);

		// SEEK_ASSAY_ID property
		searchTerm = "Assay_1";
		samples = query.samplesByAnyField(searchTerm);
		assertTrue(samples.size() > 0);
	}

	@Test
	public void datasetsByAnyField() throws Exception {
		// permID
		String searchTerm = "20160215111736723-31";
		List<DataSet> datasets = query.datasetsByAnyField(searchTerm);
		assertTrue(datasets.size() > 0);

		// type
		searchTerm = "TEST_DATASET_TYPE";
		datasets = query.datasetsByAnyField(searchTerm);
		assertTrue(datasets.size() > 0);

		// Source type - dont work
		// searchTerm = "MEASUREMENT";
		// datasets = query.datasetsByAnyField(searchTerm);
		// assertTrue(datasets.size() > 0);

		// project - dont work
		// searchTerm = "API-PROJECT";
		// datasets = query.datasetsByAnyField(searchTerm);
		// assertTrue(datasets.size() > 0);

		// experiment - dont work
		// searchTerm = "E2";
		// datasets = query.datasetsByAnyField(searchTerm);
		// assertTrue(datasets.size() > 0);

		// datastore - dont work
		// searchTerm = "DSS1";
		// datasets = query.datasetsByAnyField(searchTerm);
		// assertTrue(datasets.size() > 0);

		// user - dont work
		// searchTerm = "apiuser";
		// datasets = query.datasetsByAnyField(searchTerm);
		// assertTrue(datasets.size() > 0);

		// registration date - dont work
		// searchTerm = "2016-02-15";
		// datasets = query.datasetsByAnyField(searchTerm);
		// assertTrue(datasets.size() > 0);

		// file type
		searchTerm = "PROPRIETARY";
		datasets = query.datasetsByAnyField(searchTerm);
		assertTrue(datasets.size() > 0);

		// SEEK_DATAFILE_ID property
		searchTerm = "DataFile_9";
		datasets = query.datasetsByAnyField(searchTerm);
		assertTrue(datasets.size() > 0);
	}

}
package org.fairdom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.Experiment;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.space.Space;

/**
 * Created by quyennguyen on 13/02/15.
 */
public class ApplicationServerQueryTest {
	
	private static String endpoint;
    private static String sessionToken;
    private static ApplicationServerQuery query;
    
    @Before
    public void setUp() throws AuthenticationException{
		Authentication au = new Authentication("https://openbis-api.fair-dom.org/openbis/openbis",    			
    			"apiuser", 
    			"apiuser");        
        sessionToken = au.sessionToken();
        endpoint = "https://openbis-api.fair-dom.org/openbis/openbis";
        query = new ApplicationServerQuery(endpoint, sessionToken);
    }
    
    @Test
    public void getAllSpaces() throws Exception {
    	List<Space> spaces = query.spacesByAttribute("permId","");
    	assertTrue(spaces.size()>0);
    	String json = query.jsonResult(spaces);    	
    	assertTrue(isValidJSON(json));
    }

    @Test
    public void getAllExperiments() throws Exception { 
    	
    	List<Experiment> experiments = query.experimentsByAttribute("permId","");
    	assertTrue(experiments.size()>0);    
    	String json = query.jsonResult(experiments);    	
    	assertTrue(isValidJSON(json));
    }
    
    @Test
    public void getAllSamples() throws Exception {    	
    	List<Sample> samples = query.samplesByAttribute("permId","");
    	assertTrue(samples.size()>0);   
    	String json = query.jsonResult(samples);
    	assertTrue(isValidJSON(json));
    }
    
    @Test
    public void getAllDatasets() throws Exception {    	
    	List<DataSet> data = query.dataSetsByAttribute("permId","");
    	assertTrue(data.size()>0);
    	String json = query.jsonResult(data);
    	assertTrue(isValidJSON(json));    	
    }  
    
    @Test
    public void getDatasetByAttribute() throws Exception {
    	List<DataSet> data = query.dataSetsByAttribute("permId","20151217153943290-5");    	
    	String json = query.jsonResult(data);
    	assertTrue(isValidJSON(json));
    	assertEquals(1, data.size());
    }  
    
    @Test
    public void getDatasetsByAttribute() throws Exception {  
    	List<String> values = new ArrayList<String>();
    	values.add("20151217153943290-5");
    	values.add("20160210130359377-22");
    	List<DataSet> data = query.dataSetsByAttribute("permId",values);    	
    	String json = query.jsonResult(data);
    	assertTrue(isValidJSON(json));
    	assertEquals(2, data.size());
    }  

    @Test
    public void getExperimentWithSeekStudyID() throws Exception {        
        String property = "SEEK_STUDY_ID";
        String propertyValue = "Study_1";
        List<Experiment> experiments = query.experimentsByProperty(property, propertyValue);
        assertEquals(1, experiments.size());
        Experiment experiment = experiments.get(0);
        assertEquals(propertyValue, experiment.getProperties().get(property));
    }
    
    @Test
    public void getExperimentWithSeekStudyIDNoResult() throws Exception {        
        String property = "SEEK_STUDY_ID";
        String propertyValue = "SomeID";
        List<Experiment> experiments = query.experimentsByProperty(property, propertyValue);
        assertEquals(0, experiments.size());
    }

    @Test
    public void getSampleWithSeekAssayID() throws Exception {        
        String property = "SEEK_ASSAY_ID";
        String propertyValue = "Assay_1";
        List<Sample> samples = query.samplesByProperty(property, propertyValue);        
        assertEquals(1, samples.size());
        Sample sample = samples.get(0);

        assertEquals(propertyValue, sample.getProperties().get(property));
        //SampleIdentifier identifier = new SampleIdentifier("/API_TEST/SAMPLE_1");
        //assertEquals(identifier, sample.getIdentifier());
    }

    @Test
    public void getSampleWithSeekAssayIDNoResult() throws Exception {        
        String property = "SEEK_ASSAY_ID";
        String propertyValue = "SomeID";
        List<Sample> samples = query.samplesByProperty(property, propertyValue);      
        assertEquals(0, samples.size());
    }

    @Test
    public void getDataSetWithSeekDataFileID() throws Exception {        
        String property = "SEEK_DATAFILE_ID";
        String propertyValue = "DataFile_1";
        List<DataSet> dataSets = query.dataSetsByProperty(property, propertyValue);
        assertEquals(1, dataSets.size());
        DataSet dataSet = dataSets.get(0);

        assertEquals(propertyValue, dataSet.getProperties().get(property));
    }

    @Test
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
        List result = query.query(type, QueryType.PROPERTY, property, propertyValue);
        String jsonResult = query.jsonResult(result);
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
    	//project
    	String searchTerm = "API-PROJECT";
    	List<Experiment> experiments = query.experimentsByAnyField(searchTerm);
    	assertTrue(experiments.size() > 0);
    	
//    	dont work with /, treat like OR from openbis
//    	searchTerm = "/API-SPACE/API-PROJECT/E2";
//    	experiments = query.experimentsByAnyField(searchTerm);
//    	assertTrue(experiments.size() > 0);
    	
    	//code
    	searchTerm = "E2";
    	experiments = query.experimentsByAnyField(searchTerm);
    	assertTrue(experiments.size() > 0);
    	
    	//permID
    	searchTerm = "20151216143716562-2";
    	experiments = query.experimentsByAnyField(searchTerm);
    	assertTrue(experiments.size() > 0);
    	
    	//type
    	searchTerm = "TEST_TYPE";
    	experiments = query.experimentsByAnyField(searchTerm);
    	assertTrue(experiments.size() > 0);

    	//property
    	searchTerm = "Study_1";
    	experiments = query.experimentsByAnyField(searchTerm);
    	assertTrue(experiments.size() > 0);
    	
    	//tag
    	searchTerm = "test_tag";
    	experiments = query.experimentsByAnyField(searchTerm);
    	assertTrue(experiments.size() > 0);
    	
    	//prefix
    	searchTerm = "test_";
    	experiments = query.experimentsByAnyField(searchTerm);
    	assertTrue(experiments.size() > 0);
    	
    	//suffix
    	searchTerm = "tag";
    	experiments = query.experimentsByAnyField(searchTerm);
    	assertTrue(experiments.size() > 0);
    	
    	//middle
    	searchTerm = "st_ta";
    	experiments = query.experimentsByAnyField(searchTerm);
    	assertTrue(experiments.size() > 0);
    }
    
    @Test
    public void samplesByAnyField() throws Exception {    	
    	//code
    	String searchTerm = "S1";
    	List<Sample> samples = query.samplesByAnyField(searchTerm);
    	assertTrue(samples.size() > 0);
    	
    	//permID
    	searchTerm = "20151216143743603-3";
    	samples = query.samplesByAnyField(searchTerm);
    	assertTrue(samples.size() > 0);
    	
    	//type
    	searchTerm = "TEST_SAMPLE_TYPE";
    	samples = query.samplesByAnyField(searchTerm);
    	assertTrue(samples.size() > 0);
    	
    	//project -dont work
//    	searchTerm = "API-PROJECT";
//    	samples = query.samplesByAnyField(searchTerm);
//    	assertTrue(samples.size() > 0);
    	
    	//experiment -dont work
//    	searchTerm = "E2";
//    	samples = query.samplesByAnyField(searchTerm);
//    	assertTrue(samples.size() > 0);
    	
    	//SEEK_ASSAY_ID property
    	searchTerm = "Assay_1";
    	samples = query.samplesByAnyField(searchTerm);
    	assertTrue(samples.size() > 0);
    }
    
    @Test
    public void datasetsByAnyField() throws Exception {
    	//permID
    	String searchTerm = "20160215111736723-31";
    	List<DataSet> datasets = query.datasetsByAnyField(searchTerm);
    	assertTrue(datasets.size() > 0);
    	
    	//type
    	searchTerm = "TEST_DATASET_TYPE";
    	datasets = query.datasetsByAnyField(searchTerm);
    	assertTrue(datasets.size() > 0);
    	
    	//Source type - dont work
//    	searchTerm = "MEASUREMENT";
//    	datasets = query.datasetsByAnyField(searchTerm);
//    	assertTrue(datasets.size() > 0);
    	
    	//project - dont work
//    	searchTerm = "API-PROJECT";
//    	datasets = query.datasetsByAnyField(searchTerm);
//    	assertTrue(datasets.size() > 0);
    	
    	//experiment - dont work
//    	searchTerm = "E2";
//    	datasets = query.datasetsByAnyField(searchTerm);
//    	assertTrue(datasets.size() > 0);
    	
    	//datastore - dont work
//    	searchTerm = "DSS1";
//    	datasets = query.datasetsByAnyField(searchTerm);
//    	assertTrue(datasets.size() > 0);
    	
    	//user - dont work
//    	searchTerm = "apiuser";
//    	datasets = query.datasetsByAnyField(searchTerm);
//    	assertTrue(datasets.size() > 0);
    	
    	//registration date - dont work
//    	searchTerm = "2016-02-15";
//    	datasets = query.datasetsByAnyField(searchTerm);
//    	assertTrue(datasets.size() > 0);
    	
    	//file type
    	searchTerm = "PROPRIETARY";
    	datasets = query.datasetsByAnyField(searchTerm);
    	assertTrue(datasets.size() > 0);

    	//SEEK_DATAFILE_ID property
    	searchTerm = "DataFile_9";
    	datasets = query.datasetsByAnyField(searchTerm);
    	assertTrue(datasets.size() > 0);
    }
    
    public boolean isValidJSON(final String json) {
  	   boolean valid = false;
  	   try {
  	      final JsonParser parser = new ObjectMapper().getJsonFactory()
  	            .createJsonParser(json);
  	      while (parser.nextToken() != null) {
  	      }
  	      valid = true;
  	   } catch (JsonParseException jpe) {
  	      jpe.printStackTrace();
  	   } catch (IOException ioe) {
  	      ioe.printStackTrace();
  	   }

  	   return valid;
  }
}
package org.fairdom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.Experiment;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample;

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
    public void getExperimentWithSeekStudyID() throws Exception {        
        String property = "SEEK_STUDY_ID";
        String propertyValue = "Study_1";
        List<Experiment> experiments = query.experimentsByProperty(property, propertyValue);
        assertEquals(1, experiments.size());
        Experiment experiment = experiments.get(0);
        assertEquals(propertyValue, experiment.getProperties().get(property));
    }

    @Test
    public void getExperiments() throws Exception { 
    	
    	List<Experiment> experiments = query.experimentsByAttribute("permId","");
    	assertTrue(experiments.size()>0);    
    	String json = query.jsonResult(experiments);
    	System.out.println(json);
    	assertTrue(isValidJSON(json));
    }
    
    @Test
    public void getSamples() throws Exception {    	
    	List<Sample> samples = query.samplesByAttribute("permId","");
    	assertTrue(samples.size()>0);   
    	String json = query.jsonResult(samples);
    	assertTrue(isValidJSON(json));
    }
    
    @Test
    public void getDatasets() throws Exception {    	
    	List<DataSet> data = query.dataSetsByAttribute("permId","");
    	assertTrue(data.size()>0);
    	String json = query.jsonResult(data);
    	assertTrue(isValidJSON(json));
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

package org.fairdom;

import ch.ethz.sis.openbis.generic.shared.api.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.entity.dataset.DataSet;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.entity.experiment.Experiment;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.entity.sample.Sample;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.id.sample.SampleIdentifier;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;

import java.io.IOException;
import java.lang.String;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by quyennguyen on 13/02/15.
 */
public class OpenbisQueryTest {

    protected IApplicationServerApi api;
    protected String sessionToken;

    @Before
    public void setUp() throws AuthenticationException, IOException {
    	TestHelper.Credentials creds = TestHelper.readCredentials();
        Authentication au = new Authentication(creds.getEndpoint(),creds.getUsername(),creds.getPassword());
        api = au.api();
        sessionToken = au.sessionToken();
    }
    
    @Test
    public void getExperiments() throws Exception {
    	OpenbisQuery query = new OpenbisQuery(api, sessionToken);
    	List<Experiment> experiments = query.experimentsByAttribute("permId","");
    	assertTrue(experiments.size()>0);    
    	String json = query.jsonResult(experiments);
    	System.out.println(json);
    	assertTrue(isValidJSON(json));
    }
    
    @Test
    public void getSamples() throws Exception {
    	OpenbisQuery query = new OpenbisQuery(api, sessionToken);
    	List<Sample> samples = query.samplesByAttribute("permId","");
    	assertTrue(samples.size()>0);   
    	String json = query.jsonResult(samples);
    	assertTrue(isValidJSON(json));
    }
    
    @Test
    public void getDatasets() throws Exception {
    	OpenbisQuery query = new OpenbisQuery(api, sessionToken);
    	List<DataSet> data = query.dataSetsByAttribute("permId","");
    	assertNotEquals(0, data.size());
    	String json = query.jsonResult(data);
    	assertTrue(isValidJSON(json));
    }  
    
    @Test
    public void getDatasetsWithList() throws Exception {
    	OpenbisQuery query = new OpenbisQuery(api, sessionToken);
    	List<String> ids=new ArrayList<String>(Arrays.asList(new String[]{"20150311140253714-271","20150323162504464-366","20150323162328428-362"}));
    	List<DataSet> data = query.dataSetsByAttribute("permId",ids);
    	assertEquals(3, data.size());
    	String json = query.jsonResult(data);    	
    	assertTrue(isValidJSON(json));
    }
    
    @Test
    public void getSamplesWithList() throws Exception {
    	OpenbisQuery query = new OpenbisQuery(api, sessionToken);
    	List<String> ids=new ArrayList<String>(Arrays.asList(new String[]{"20150319155418281-304","20150309172707806-242"}));
    	List<Sample> samples = query.samplesByAttribute("permId",ids);
    	assertEquals(2, samples.size());
    	String json = query.jsonResult(samples);    	    
    	assertTrue(isValidJSON(json));
    }
    
    @Test
    public void getExperimentsWithList() throws Exception {
    	OpenbisQuery query = new OpenbisQuery(api, sessionToken);
    	List<String> ids=new ArrayList<String>(Arrays.asList(new String[]{"20150319121918550-280","20150319125925820-293","20150319163047131-344"}));
    	List<Experiment> exp = query.experimentsByAttribute("permId",ids);
    	assertEquals(3, exp.size());
    	String json = query.jsonResult(exp);    	
    	assertTrue(isValidJSON(json));
    }
    

    @Test
    public void getExperimentWithSeekStudyID() throws Exception {
        OpenbisQuery query = new OpenbisQuery(api, sessionToken);
        String property = "SEEK_STUDY_ID";
        String propertyValue = "Study_1";
        List<Experiment> experiments = query.experimentsByProperty(property, propertyValue);
        assertEquals(1, experiments.size());
        Experiment experiment = experiments.get(0);
        assertEquals(propertyValue, experiment.getProperties().get(property));
    }

    @Test
    public void getExperimentWithSeekStudyIDNoResult() throws Exception {
        OpenbisQuery query = new OpenbisQuery(api, sessionToken);
        String property = "SEEK_STUDY_ID";
        String propertyValue = "SomeID";
        List<Experiment> experiments = query.experimentsByProperty(property, propertyValue);
        assertEquals(0, experiments.size());
    }

    @Test
    public void getSampleWithSeekAssayID() throws Exception {
        OpenbisQuery query = new OpenbisQuery(api, sessionToken);
        String property = "SEEK_ASSAY_ID";
        String propertyValue = "Assay_1";
        List<Sample> samples = query.samplesByProperty(property, propertyValue);
        assertEquals(1, samples.size());
        Sample sample = samples.get(0);

        assertEquals(propertyValue, sample.getProperties().get(property));
        SampleIdentifier identifier = new SampleIdentifier("/API_TEST/SAMPLE_1");
        assertEquals(identifier, sample.getIdentifier());
    }

    @Test
    public void getSampleWithSeekAssayIDNoResult() throws Exception {
        OpenbisQuery query = new OpenbisQuery(api, sessionToken);
        String property = "SEEK_ASSAY_ID";
        String propertyValue = "SomeID";
        List<Sample> samples = query.samplesByProperty(property, propertyValue);
        assertEquals(0, samples.size());
    }

    @Test
    public void getDataSetWithSeekDataFileID() throws Exception {
        OpenbisQuery query = new OpenbisQuery(api, sessionToken);
        String property = "SEEK_DATAFILE_ID";
        String propertyValue = "DataFile_1";
        List<DataSet> dataSets = query.dataSetsByProperty(property, propertyValue);
        assertEquals(1, dataSets.size());
        DataSet dataSet = dataSets.get(0);

        assertEquals(propertyValue, dataSet.getProperties().get(property));
    }        
    
    @Test
    public void getDatasetWithSeekDataFileIDNoResult() throws Exception {
        OpenbisQuery query = new OpenbisQuery(api, sessionToken);
        String property = "SEEK_DATAFILE_ID";
        String propertyValue = "SomeID";
        List<Sample> samples = query.samplesByProperty(property, propertyValue);
        assertEquals(0, samples.size());
    }

    @Test
    public void jsonResultforExperiment() throws Exception {
        OpenbisQuery query = new OpenbisQuery(api, sessionToken);
        String type = "Experiment";
        String property = "SEEK_STUDY_ID";
        String propertyValue = "Study_1";
        List result = query.query(type, QueryType.PROPERTY,property, propertyValue);
        String jsonResult = query.jsonResult(result);
        assertTrue(jsonResult.matches("(.*)Study_1(.*)"));
    }

    @Test(expected = InvalidOptionException.class)
    public void unrecognizedType() throws Exception {
        OpenbisQuery query = new OpenbisQuery(api, sessionToken);
        String type = "SomeType";
        String property = "SEEK_STUDY_ID";
        String propertyValue = "Study_1";
        query.query(type, QueryType.PROPERTY,property, propertyValue);
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

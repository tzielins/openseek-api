package org.fairdom;

import ch.ethz.sis.openbis.generic.shared.api.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.entity.dataset.DataSet;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.entity.experiment.Experiment;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.entity.sample.Sample;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.id.sample.SampleIdentifier;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.String;
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
    }
    
    @Test
    public void getSamples() throws Exception {
    	OpenbisQuery query = new OpenbisQuery(api, sessionToken);
    	List<Sample> samples = query.samplesByAttribute("permId","");
    	assertTrue(samples.size()>0);    	
    }
    
    @Test
    public void getDatasets() throws Exception {
    	OpenbisQuery query = new OpenbisQuery(api, sessionToken);
    	List<DataSet> data = query.dataSetsByAttribute("permId","");
    	assertTrue(data.size()>0);    	
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
    public void dataSets() throws Exception {
    	TestHelper.Credentials creds = TestHelper.readCredentials();
        Authentication au = new Authentication(creds.getEndpoint(),creds.getUsername(),creds.getPassword());
        api = au.api();
        sessionToken = au.sessionToken();
        OpenbisQuery query = new OpenbisQuery(api, sessionToken);
        query.dataSets();
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
}

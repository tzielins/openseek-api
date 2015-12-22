package org.fairdom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import ch.ethz.sis.openbis.generic.shared.api.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.entity.dataset.DataSet;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.entity.experiment.Experiment;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.entity.sample.Sample;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.search.SearchResult;

/**
 * Created by quyennguyen on 13/02/15.
 */
public class ApplicationServerQueryTest {

    protected IApplicationServerApi as;
    protected String sessionToken;

    @Before
    public void setUp() throws AuthenticationException{
        Authentication au = new Authentication("https://openbis-testing.fair-dom.org", "api-user", "api-user");
        as = au.as();
        sessionToken = au.sessionToken();
    }

    @Test
    public void getExperimentWithSeekStudyID() throws Exception {
        ApplicationServerQuery query = new ApplicationServerQuery(as, sessionToken);
        String property = "SEEK_STUDY_ID";
        String propertyValue = "Study_1";
        SearchResult<Experiment> experiments = query.experiments(property, propertyValue);
        assertEquals(1, experiments.getTotalCount());
        Experiment experiment = experiments.getObjects().get(0);
        assertEquals(propertyValue, experiment.getProperties().get(property));
    }

    @Test
    public void getExperimentWithSeekStudyIDNoResult() throws Exception {
        ApplicationServerQuery query = new ApplicationServerQuery(as, sessionToken);
        String property = "SEEK_STUDY_ID";
        String propertyValue = "SomeID";
        SearchResult<Experiment> experiments = query.experiments(property, propertyValue);
        assertEquals(0, experiments.getTotalCount());
    }

    @Test
    public void getSampleWithSeekAssayID() throws Exception {
        ApplicationServerQuery query = new ApplicationServerQuery(as, sessionToken);
        String property = "SEEK_ASSAY_ID";
        String propertyValue = "Assay_1";
        SearchResult<Sample> samples = query.samples(property, propertyValue);
        assertEquals(1, samples.getTotalCount());
        Sample sample = samples.getObjects().get(0);

        assertEquals(propertyValue, sample.getProperties().get(property));
        //SampleIdentifier identifier = new SampleIdentifier("/API_TEST/SAMPLE_1");
        //assertEquals(identifier, sample.getIdentifier());
    }

    @Test
    public void getSampleWithSeekAssayIDNoResult() throws Exception {
        ApplicationServerQuery query = new ApplicationServerQuery(as, sessionToken);
        String property = "SEEK_ASSAY_ID";
        String propertyValue = "SomeID";
        SearchResult<Sample> samples = query.samples(property, propertyValue);
        assertEquals(0, samples.getTotalCount());
    }

    @Test
    public void getDataSetWithSeekDataFileID() throws Exception {
        ApplicationServerQuery query = new ApplicationServerQuery(as, sessionToken);
        String property = "SEEK_DATAFILE_ID";
        String propertyValue = "DataFile_1";
        SearchResult<DataSet> dataSets = query.dataSets(property, propertyValue);
        assertEquals(1, dataSets.getTotalCount());
        DataSet dataSet = dataSets.getObjects().get(0);

        assertEquals(propertyValue, dataSet.getProperties().get(property));
    }

    @Test
    public void getDatasetWithSeekDataFileIDNoResult() throws Exception {
        ApplicationServerQuery query = new ApplicationServerQuery(as, sessionToken);
        String property = "SEEK_DATAFILE_ID";
        String propertyValue = "SomeID";
        SearchResult<Sample> samples = query.samples(property, propertyValue);
        assertEquals(0, samples.getTotalCount());
    }
    	
    @Test
    public void jsonResultforExperiment() throws Exception {
        ApplicationServerQuery query = new ApplicationServerQuery(as, sessionToken);
        String type = "Experiment";
        String property = "SEEK_STUDY_ID";
        String propertyValue = "Study_1";
        SearchResult result = query.query(type, property, propertyValue);
        String jsonResult = query.jsonResult(result);
        assertTrue(jsonResult.matches("(.*)Study_1(.*)"));
    }

    @Test(expected = InvalidOptionException.class)
    public void unrecognizedType() throws Exception {
        ApplicationServerQuery query = new ApplicationServerQuery(as, sessionToken);
        String type = "SomeType";
        String property = "SEEK_STUDY_ID";
        String propertyValue = "Study_1";
        query.query(type, property, propertyValue);
    }
}

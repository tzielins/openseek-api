package org.fairdom;

import ch.ethz.sis.openbis.generic.shared.api.v3.dto.entity.dataset.DataSet;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.entity.experiment.Experiment;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.entity.sample.Sample;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.id.sample.SampleIdentifier;

import org.junit.Test;

import java.lang.String;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by quyennguyen on 13/02/15.
 */
public class OpenbisQueryTest {

    @Test
    public void getExperimentWithSeekStudyID() throws Exception {
        OpenbisQuery query = new OpenbisQuery();
        String property = "SEEK_STUDY_ID";
        String propertyValue = "Study_1";
        List<Experiment> experiments = query.experiments(property, propertyValue);
        assertEquals(1, experiments.size());
        Experiment experiment = experiments.get(0);
        assertEquals(propertyValue, experiment.getProperties().get(property));
    }

    @Test
    public void getExperimentWithSeekStudyIDNoResult() throws Exception {
        OpenbisQuery query = new OpenbisQuery();
        String property = "SEEK_STUDY_ID";
        String propertyValue = "SomeID";
        List<Experiment> experiments = query.experiments(property, propertyValue);
        assertEquals(0, experiments.size());
    }

    @Test
    public void getSampleWithSeekStudyID() throws Exception {
        OpenbisQuery query = new OpenbisQuery();
        String property = "SEEK_STUDY_ID";
        String propertyValue = "Study_1";
        List<Sample> samples = query.samples(property, propertyValue);
        assertEquals(1, samples.size());
        Sample sample = samples.get(0);
        Experiment experiment = sample.getExperiment();

        assertEquals(propertyValue, experiment.getProperties().get(property));
        SampleIdentifier identifier = new SampleIdentifier("/API_TEST/SAMPLE_1");
        assertEquals(identifier, sample.getIdentifier());
    }

    @Test
    public void getSampleWithSeekStudyIDNoResult() throws Exception {
        OpenbisQuery query = new OpenbisQuery();
        String property = "SEEK_STUDY_ID";
        String propertyValue = "SomeID";
        List<Sample> samples = query.samples(property, propertyValue);
        assertEquals(0, samples.size());
    }

    @Test
    public void getDataSetWithSeekStudyID() throws Exception {
        OpenbisQuery query = new OpenbisQuery();
        String property = "SEEK_STUDY_ID";
        String propertyValue = "Study_1";
        List<DataSet> dataSets = query.dataSets(property, propertyValue);
        assertEquals(1, dataSets.size());
        DataSet dataSet = dataSets.get(0);
        Experiment experiment = dataSet.getExperiment();

        assertEquals(propertyValue, experiment.getProperties().get(property));
    }

    @Test
    public void getDatasetWithSeekStudyIDNoResult() throws Exception {
        OpenbisQuery query = new OpenbisQuery();
        String property = "SEEK_STUDY_ID";
        String propertyValue = "SomeID";
        List<Sample> samples = query.samples(property, propertyValue);
        assertEquals(0, samples.size());
    }

    @Test
    public void jsonResultforExperiment() throws Exception {
        OpenbisQuery query = new OpenbisQuery();
        String type = "Experiment";
        String property = "SEEK_STUDY_ID";
        String propertyValue = "Study_1";
        String jsonResult = query.jsonResult(type, property, propertyValue);
        assertTrue(jsonResult.matches("(.*)Study_1(.*)"));
    }
}

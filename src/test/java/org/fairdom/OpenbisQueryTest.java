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
        String seekStudyID = "Study_1";
        List<Experiment> experiments = query.experiments(seekStudyID);
        assertEquals(1, experiments.size());
        Experiment experiment = experiments.get(0);
        assertEquals(seekStudyID, experiment.getProperties().get("SEEK_STUDY_ID"));
    }

    @Test
    public void getExperimentWithSeekStudyIDNoResult() throws Exception {
        OpenbisQuery query = new OpenbisQuery();
        String seekStudyID = "someID";
        List<Experiment> experiments = query.experiments(seekStudyID);
        assertEquals(0, experiments.size());
    }

    @Test
    public void getSampleWithSeekStudyID() throws Exception {
        OpenbisQuery query = new OpenbisQuery();
        String seekStudyID = "Study_1";
        List<Sample> samples = query.samples(seekStudyID);
        assertEquals(1, samples.size());
        Sample sample = samples.get(0);
        Experiment experiment = sample.getExperiment();

        assertEquals(seekStudyID, experiment.getProperties().get("SEEK_STUDY_ID"));
        SampleIdentifier identifier = new SampleIdentifier("/API_TEST/SAMPLE_1");
        assertEquals(identifier, sample.getIdentifier());
    }

    @Test
    public void getSampleWithSeekStudyIDNoResult() throws Exception {
        OpenbisQuery query = new OpenbisQuery();
        String seekStudyID = "SomeID";
        List<Sample> samples = query.samples(seekStudyID);
        assertEquals(0, samples.size());
    }

    @Test
    public void getDataSetWithSeekStudyID() throws Exception {
        OpenbisQuery query = new OpenbisQuery();
        String seekStudyID = "Study_1";
        List<DataSet> dataSets = query.dataSets(seekStudyID);
        assertEquals(1, dataSets.size());
        DataSet dataSet = dataSets.get(0);
        Experiment experiment = dataSet.getExperiment();

        assertEquals(seekStudyID, experiment.getProperties().get("SEEK_STUDY_ID"));
    }

    @Test
    public void getDatasetWithSeekStudyIDNoResult() throws Exception {
        OpenbisQuery query = new OpenbisQuery();
        String seekStudyID = "SomeID";
        List<Sample> samples = query.samples(seekStudyID);
        assertEquals(0, samples.size());
    }

    @Test
    public void jsonResultforExperiment() throws Exception {
        OpenbisQuery query = new OpenbisQuery();
        String seekStudyID = "Study_1";;
        String jsonResult = query.jsonResult("Experiment", seekStudyID);
        assertTrue(jsonResult.matches("(.*)Study_1(.*)"));
    }
}

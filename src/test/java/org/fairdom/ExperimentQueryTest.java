package org.fairdom;

import ch.ethz.sis.openbis.generic.shared.api.v3.dto.entity.dataset.DataSet;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.entity.experiment.Experiment;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.entity.sample.Sample;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.id.dataset.DataSetPermId;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.id.sample.SampleIdentifier;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import org.junit.Test;
import org.junit.Before;

import java.lang.String;
import java.lang.System;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by quyennguyen on 13/02/15.
 */
public class ExperimentQueryTest {

    @Test
    public void getExperimentWithSeekStudyID() throws Exception {
        ExperimentQuery query = new ExperimentQuery();
        String seekStudyID = "Study_1";
        List<Experiment> experiments = query.experiment(seekStudyID);
        assertEquals(1, experiments.size());
        Experiment experiment = experiments.get(0);
        assertEquals(seekStudyID, experiment.getProperties().get("SEEK_STUDY_ID"));
    }

    @Test
    public void getExperimentWithSeekStudyIDNoResult() throws Exception {
        ExperimentQuery query = new ExperimentQuery();
        String seekStudyID = "someID";
        List<Experiment> experiments = query.experiment(seekStudyID);
        assertEquals(0, experiments.size());
    }

    @Test
    public void getSampleWithSeekStudyID() throws Exception {
        ExperimentQuery query = new ExperimentQuery();
        String seekStudyID = "Study_1";
        List<Sample> samples = query.sample(seekStudyID);
        assertEquals(1, samples.size());
        Sample sample = samples.get(0);
        Experiment experiment = sample.getExperiment();

        assertEquals(seekStudyID, experiment.getProperties().get("SEEK_STUDY_ID"));
        SampleIdentifier identifier = new SampleIdentifier("/API_TEST/SAMPLE_1");
        assertEquals(identifier, sample.getIdentifier());
    }

    @Test
    public void getSampleWithSeekStudyIDNoResult() throws Exception {
        ExperimentQuery query = new ExperimentQuery();
        String seekStudyID = "SomeID";
        List<Sample> samples = query.sample(seekStudyID);
        assertEquals(0, samples.size());
    }

    @Test
    public void getDataSetWithSeekStudyID() throws Exception {
        ExperimentQuery query = new ExperimentQuery();
        String seekStudyID = "Study_1";
        List<DataSet> dataSets = query.dataSet(seekStudyID);
        assertEquals(1, dataSets.size());
        DataSet dataSet = dataSets.get(0);
        Experiment experiment = dataSet.getExperiment();

        assertEquals(seekStudyID, experiment.getProperties().get("SEEK_STUDY_ID"));
    }

    @Test
    public void getDatasetWithSeekStudyIDNoResult() throws Exception {
        ExperimentQuery query = new ExperimentQuery();
        String seekStudyID = "SomeID";
        List<Sample> samples = query.sample(seekStudyID);
        assertEquals(0, samples.size());
    }
}

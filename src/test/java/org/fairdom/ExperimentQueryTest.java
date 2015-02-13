package org.fairdom;

import ch.ethz.sis.openbis.generic.shared.api.v3.dto.entity.experiment.Experiment;
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
}

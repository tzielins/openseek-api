package org.fairdom;

import ch.ethz.sis.openbis.generic.shared.api.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.entity.dataset.DataSet;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.entity.experiment.Experiment;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.entity.sample.Sample;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.fetchoptions.dataset.DataSetFetchOptions;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.fetchoptions.experiment.ExperimentFetchOptions;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.fetchoptions.sample.SampleFetchOptions;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.search.DataSetSearchCriterion;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.search.ExperimentSearchCriterion;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.search.SampleSearchCriterion;
import ch.systemsx.cisd.common.spring.HttpInvokerUtils;
import ch.systemsx.cisd.common.ssl.SslCertificateHelper;

import java.util.List;

/**
 * Created by quyennguyen on 13/02/15.
 */
public class ExperimentQuery {

    public static void main(String[] args){
        ExperimentQuery query = new ExperimentQuery();
        List <Experiment> experiments = query.experiment("Study_1");
        List <Sample> samples = query.sample("Study_1");

        System.out.println("Result:");

        for (Experiment experiment : experiments){
            System.out.println(experiment.getIdentifier());
            System.out.println(experiment.getProperties().get("SEEK_STUDY_ID"));
        }
    }

    public List <Experiment> experiment(String SeekID){
        ExperimentSearchCriterion criterion = new ExperimentSearchCriterion();
        criterion.withProperty("SEEK_STUDY_ID").thatEquals(SeekID);

        ExperimentFetchOptions options = new ExperimentFetchOptions();
        options.withProperties();
        options.withProject();

        Authentication au = new Authentication("https://openbis-testing.fair-dom.org/openbis", "api-user", "api-user");
        IApplicationServerApi api = au.api();
        String sessionToken = au.authentication();

        List <Experiment> experiments = api.searchExperiments(sessionToken, criterion, options);
        return experiments;
    }

    public List <Sample> sample(String SeekID){
        SampleSearchCriterion criterion = new SampleSearchCriterion();
        criterion.withExperiment().withProperty("SEEK_STUDY_ID").thatEquals(SeekID);

        SampleFetchOptions options = new SampleFetchOptions();
        options.withProperties();
        options.withExperiment().withProperties();

        Authentication au = new Authentication("https://openbis-testing.fair-dom.org/openbis", "api-user", "api-user");
        IApplicationServerApi api = au.api();
        String sessionToken = au.authentication();

        List <Sample> samples = api.searchSamples(sessionToken, criterion, options);
        return samples;
    }

    public List <DataSet> dataSet(String SeekID){
        DataSetSearchCriterion criterion = new DataSetSearchCriterion();
        criterion.withExperiment().withProperty("SEEK_STUDY_ID").thatEquals(SeekID);

        DataSetFetchOptions options = new DataSetFetchOptions();
        options.withProperties();
        options.withExperiment().withProperties();

        Authentication au = new Authentication("https://openbis-testing.fair-dom.org/openbis", "api-user", "api-user");
        IApplicationServerApi api = au.api();
        String sessionToken = au.authentication();

        List <DataSet> dataSets = api.searchDataSets(sessionToken, criterion, options);
        return dataSets;
    }

}

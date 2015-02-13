package org.fairdom;

import ch.ethz.sis.openbis.generic.shared.api.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.entity.experiment.Experiment;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.fetchoptions.experiment.ExperimentFetchOptions;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.search.ExperimentSearchCriterion;
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

        System.out.println("Result:");

        for (Experiment experiment : experiments){
            System.out.println(experiment.getIdentifier());
            System.out.println(experiment.getProperties().get("SEEK_STUDY_ID"));
        }
    }

    public List <Experiment> experiment(String SeekID){
        ExperimentSearchCriterion criterion = new ExperimentSearchCriterion();
        criterion.withProperty("SEEK_STUDY_ID").thatContains(SeekID);
        ExperimentFetchOptions options = new ExperimentFetchOptions();
        options.withProperties();

        Authentication au = new Authentication("https://openbis-testing.fair-dom.org/openbis", "api-user", "api-user");
        IApplicationServerApi api = au.api();
        String sessionToken = au.authentication();

        List <Experiment> experiments = api.searchExperiments(sessionToken, criterion, options);
        return experiments;
    }
}

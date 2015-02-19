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
import ch.systemsx.cisd.openbis.generic.shared.api.v3.json.GenericObjectMapper;

import java.io.StringWriter;
import java.util.List;

/**
 * Created by quyennguyen on 13/02/15.
 */
public class OpenbisQuery {

    public List <Experiment> experiments(String property, String propertyValue){
        ExperimentSearchCriterion criterion = new ExperimentSearchCriterion();
        criterion.withProperty(property).thatEquals(propertyValue);

        ExperimentFetchOptions options = new ExperimentFetchOptions();
        options.withProperties();
        options.withProject();

        Authentication au = new Authentication("https://openbis-testing.fair-dom.org/openbis", "api-user", "api-user");
        IApplicationServerApi api = au.api();
        String sessionToken = au.authentication();

        List <Experiment> experiments = api.searchExperiments(sessionToken, criterion, options);
        return experiments;
    }

    public String jsonResult(String type, String property, String propertyValue){
        GenericObjectMapper mapper = new GenericObjectMapper();
        StringWriter sw = new StringWriter();
        List result = null;
        try {
            if (type.equals("Experiment")){
                result = experiments(property, propertyValue);
            }else if (type.equals("Sample")){
                result = samples(property, propertyValue);
            }else{
                result = dataSets(property, propertyValue);
            }
            mapper.writeValue(sw, result);

        }catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return sw.toString();
    }

    public List <Sample> samples(String property, String propertyValue){
        SampleSearchCriterion criterion = new SampleSearchCriterion();
        criterion.withProperty(property).thatEquals(propertyValue);

        SampleFetchOptions options = new SampleFetchOptions();
        options.withProperties();
        options.withExperiment().withProperties();

        Authentication au = new Authentication("https://openbis-testing.fair-dom.org/openbis", "api-user", "api-user");
        IApplicationServerApi api = au.api();
        String sessionToken = au.authentication();

        List <Sample> samples = api.searchSamples(sessionToken, criterion, options);
        return samples;
    }

    public List <DataSet> dataSets(String property, String propertyValue){
        DataSetSearchCriterion criterion = new DataSetSearchCriterion();
        criterion.withProperty(property).thatEquals(propertyValue);

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

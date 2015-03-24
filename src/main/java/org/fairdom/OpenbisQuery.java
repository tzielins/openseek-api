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
    private IApplicationServerApi api;
    private String sessionToken;

    public OpenbisQuery(IApplicationServerApi startApi, String startSessionToken ){
        api = startApi;
        sessionToken = startSessionToken;
    }

    public List query(String type, String property, String propertyValue) throws InvalidOptionException {
        List result = null;
        if (type.equals("Experiment")){
            result = experimentsByProperty(property, propertyValue);
        }else if (type.equals("Sample")){
            result = samplesByProperty(property, propertyValue);
        }else if (type.equals("DataSet")){
            result = dataSetsByProperty(property, propertyValue);
        }else{
            throw new InvalidOptionException("Unrecognised type: " + type);
        }
        return result;
    }

    public String jsonResult(List result){
        GenericObjectMapper mapper = new GenericObjectMapper();
        StringWriter sw = new StringWriter();
        try {
            mapper.writeValue(sw, result);
        }catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return sw.toString();
    }       

    public List <Experiment> experimentsByProperty(String property, String propertyValue){
        ExperimentSearchCriterion criterion = new ExperimentSearchCriterion();
        criterion.withProperty(property).thatEquals(propertyValue);

        ExperimentFetchOptions options = new ExperimentFetchOptions();
        options.withProperties();        

        List <Experiment> experiments = api.searchExperiments(sessionToken, criterion, options);
        return experiments;
    }

    public List <Sample> samplesByProperty(String property, String propertyValue){
        SampleSearchCriterion criterion = new SampleSearchCriterion();
        criterion.withProperty(property).thatEquals(propertyValue);

        SampleFetchOptions options = new SampleFetchOptions();
        options.withProperties();

        List <Sample> samples = api.searchSamples(sessionToken, criterion, options);
        return samples;
    }

    public List <DataSet> dataSetsByProperty(String property, String propertyValue){
        DataSetSearchCriterion criterion = new DataSetSearchCriterion();
        criterion.withProperty(property).thatEquals(propertyValue);

        DataSetFetchOptions options = new DataSetFetchOptions();
        options.withProperties();

        List <DataSet> dataSets = api.searchDataSets(sessionToken, criterion, options);
        return dataSets;
    }

}

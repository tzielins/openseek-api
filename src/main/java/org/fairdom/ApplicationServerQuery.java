package org.fairdom;

import java.io.StringWriter;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.fetchoptions.DataSetFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.search.DataSetSearchCriteria;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.Experiment;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.fetchoptions.ExperimentFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.search.ExperimentSearchCriteria;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.fetchoptions.SampleFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.search.SampleSearchCriteria;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.json.GenericObjectMapper;

/**
 * Created by quyennguyen on 13/02/15.
 */
public class ApplicationServerQuery {
    private IApplicationServerApi as;    
    private String sessionToken;

    public ApplicationServerQuery(IApplicationServerApi startAs, String startSessionToken ){
        as = startAs;
        sessionToken = startSessionToken;
    }
    
    public static void main(String[] args) {
    	OptionParser options = null;
        try {
            options = new OptionParser(args);
        } catch (InvalidOptionException e) {
            System.err.println("Invalid option: " + e.getMessage());
            System.exit(-1);
		} catch (ParseException pe) {
			System.out.println("position: " + pe.getPosition());
			System.out.println(pe);
			System.exit(-1);
		}

        try {
        	JSONObject account = options.getAccount();
        	JSONObject endpoints = options.getEndpoints();
        	JSONObject query = options.getQuery();
        	
            Authentication au = new Authentication(endpoints.get("as").toString(), endpoints.get("dss").toString(), account.get("username").toString(), account.get("password").toString());
            IApplicationServerApi as = au.as();
            String sessionToken = au.sessionToken();

            ApplicationServerQuery asQuery = new ApplicationServerQuery(as, sessionToken);           
            SearchResult result = asQuery.query(query.get("entityType").toString(), query.get("property").toString(), query.get("propertyValue").toString());
            String jsonResult = asQuery.jsonResult(result);
            System.out.println(jsonResult);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            System.exit(-1);
        }
        System.exit(0);
    }
    
    public SearchResult query(String type, String property, String propertyValue) throws InvalidOptionException {
    	SearchResult result = null;
        if (type.equals("Experiment")){
            result = experiments(property, propertyValue);
        }else if (type.equals("Sample")){
            result = samples(property, propertyValue);
        }else if (type.equals("DataSet")){
            result = dataSets(property, propertyValue);
        }else{
            throw new InvalidOptionException("Unrecognised type: " + type);
        }
        return result;
    }


    public String jsonResult(SearchResult result){
        GenericObjectMapper mapper = new GenericObjectMapper();
        StringWriter sw = new StringWriter();
        try {
            mapper.writeValue(sw, result);
        }catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return sw.toString();
    }

    public SearchResult <Experiment> experiments(String property, String propertyValue){
        ExperimentSearchCriteria criterion = new ExperimentSearchCriteria();
        criterion.withProperty(property).thatEquals(propertyValue);

        ExperimentFetchOptions options = new ExperimentFetchOptions();
        options.withProperties();

        SearchResult<Experiment> experiments = as.searchExperiments(sessionToken, criterion, options);
        return experiments;
    }

    public SearchResult <Sample> samples(String property, String propertyValue){
        SampleSearchCriteria criterion = new SampleSearchCriteria();
        criterion.withProperty(property).thatEquals(propertyValue);

        SampleFetchOptions options = new SampleFetchOptions();
        options.withProperties();

        SearchResult<Sample> samples = as.searchSamples(sessionToken, criterion, options);
        return samples;
    }

    public SearchResult <DataSet> dataSets(String property, String propertyValue){
        DataSetSearchCriteria criterion = new DataSetSearchCriteria();
        criterion.withProperty(property).thatEquals(propertyValue);

        DataSetFetchOptions options = new DataSetFetchOptions();
        options.withProperties();

        SearchResult <DataSet> dataSets = as.searchDataSets(sessionToken, criterion, options);
        return dataSets;
    }
}

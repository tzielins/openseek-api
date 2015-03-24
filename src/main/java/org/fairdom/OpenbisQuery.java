package org.fairdom;

import ch.ethz.sis.openbis.generic.shared.api.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.entity.dataset.DataSet;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.entity.experiment.Experiment;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.entity.sample.Sample;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.fetchoptions.dataset.DataSetFetchOptions;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.fetchoptions.experiment.ExperimentFetchOptions;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.fetchoptions.sample.SampleFetchOptions;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.search.AbstractEntitySearchCriterion;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.search.AbstractSearchCriterion;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.search.DataSetSearchCriterion;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.search.ExperimentSearchCriterion;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.search.SampleSearchCriterion;
import ch.systemsx.cisd.openbis.generic.shared.api.v3.json.GenericObjectMapper;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory.Feature;

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

    public List query(String type, QueryType queryType, String key, String value) throws InvalidOptionException {
        List result = null;
        if (queryType==QueryType.PROPERTY) {
        	if (type.equals("Experiment")){
                result = experimentsByProperty(key, value);
            }else if (type.equals("Sample")){
                result = samplesByProperty(key, value);
            }else if (type.equals("DataSet")){
                result = dataSetsByProperty(key, value);
            }else{
                throw new InvalidOptionException("Unrecognised type: " + type);
            }
        }
        else if (queryType==QueryType.ATTRIBUTE) {
        	if (type.equals("Experiment")) {
                result = experimentsByAttribute(key, value);
            }else if (type.equals("Sample")) {
                result = samplesByAttribute(key, value);
            }else if (type.equals("DataSet")) {
                result = dataSetsByAttribute(key, value);
            }else {
                throw new InvalidOptionException("Unrecognised type: " + type);
            }
        }
        
        return result;
    }

    public String jsonResult(List result){
    	Map<String, Object> map = new HashMap<String, Object>();
    	for (Object item : result) {    		
    		if (item instanceof Experiment) {
    			if (!map.containsKey("experiments")) {
    				map.put("experiments", new ArrayList<Object>());
    			}
    			((List)map.get("experiments")).add(jsonMap((Experiment)item));    			
    		}
    		if (item instanceof DataSet) {
    			if (!map.containsKey("datasets")) {
    				map.put("datasets", new ArrayList<Object>());
    			}
    			((List)map.get("datasets")).add(jsonMap((DataSet)item));    			
    		}
    		if (item instanceof Sample) {
    			if (!map.containsKey("samples")) {
    				map.put("samples", new ArrayList<Object>());
    			}
    			((List)map.get("samples")).add(jsonMap((Sample)item));    			
    		}
    	}
        GenericObjectMapper mapper = new GenericObjectMapper();        
        StringWriter sw = new StringWriter();
        try {
            mapper.writeValue(sw, map);
        }catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
        return sw.toString();
    }  
    
    private Map<String,Object> jsonMap(Experiment experiment) {
    	Map<String,Object> map = new HashMap<String, Object>();
    	map.put("permId", experiment.getPermId().getPermId());
    	map.put("code", experiment.getCode());
    	map.put("project", experiment.getProject().getPermId().getPermId());
    	map.put("properties", experiment.getProperties());
    	map.put("modificationDate", experiment.getModificationDate());
    	map.put("registrationDate", experiment.getRegistrationDate());
    	map.put("identifier",experiment.getIdentifier().getIdentifier());
    	map.put("modifier",experiment.getModifier().getUserId());
    	map.put("tags", experiment.getTags());
    	return map;
    }
    
    private Map<String,Object> jsonMap(DataSet dataset) {
    	Map<String,Object> map = new HashMap<String, Object>();
    	map.put("permId", dataset.getPermId().getPermId());
    	map.put("code", dataset.getCode());    	
    	map.put("properties", dataset.getProperties());
    	map.put("modificationDate", dataset.getModificationDate());
    	map.put("registrationDate", dataset.getRegistrationDate());    	
    	map.put("modifier",dataset.getModifier().getUserId());
    	map.put("experiment", dataset.getExperiment().getPermId().getPermId());
    	map.put("tags", dataset.getTags());
    	return map;
    }
    
    private Map<String,Object> jsonMap(Sample sample) {
    	Map<String,Object> map = new HashMap<String, Object>();
    	map.put("permId", sample.getPermId().getPermId());
    	map.put("code", sample.getCode());    	
    	map.put("properties", sample.getProperties());
    	map.put("modificationDate", sample.getModificationDate());
    	map.put("registrationDate", sample.getRegistrationDate());    	
    	map.put("modifier",sample.getModifier().getUserId());
    	if (sample.getExperiment()!=null) {
    		map.put("experiment", sample.getExperiment().getPermId().getPermId());
    	}
    	
    	map.put("tags", sample.getTags());
    	return map;
    }
    
    public List<Experiment> experimentsByAttribute(String attribute,String value) throws InvalidOptionException {
    	ExperimentSearchCriterion criterion = new ExperimentSearchCriterion();
    	updateCriterianForAttribute(criterion, attribute, value);
    	ExperimentFetchOptions options = new ExperimentFetchOptions();
        options.withProperties();
        options.withSamples();
        options.withDataSets();
        options.withProject();
        options.withModifier();
        options.withTags();
        
        return api.searchExperiments(sessionToken, criterion, options);
    }
    
    public List <DataSet> dataSetsByAttribute(String attribute, String value) throws InvalidOptionException{
        DataSetSearchCriterion criterion = new DataSetSearchCriterion();
        updateCriterianForAttribute(criterion, attribute, value);

        DataSetFetchOptions options = new DataSetFetchOptions();
        options.withProperties();
        options.withSample();
        options.withExperiment();
        options.withModifier();
        options.withTags();        

        List <DataSet> dataSets = api.searchDataSets(sessionToken, criterion, options);
        return dataSets;
    }
    
    public List <Sample> samplesByAttribute(String attribute, String value) throws InvalidOptionException{
        SampleSearchCriterion criterion = new SampleSearchCriterion();
        updateCriterianForAttribute(criterion, attribute, value);
        
        SampleFetchOptions options = new SampleFetchOptions();
        options.withProperties();
        options.withExperiment();
        options.withDataSets();
        options.withModifier();
        options.withTags();

        List <Sample> samples = api.searchSamples(sessionToken, criterion, options);
        return samples;
    }
        
    
    private void updateCriterianForAttribute(AbstractEntitySearchCriterion<?> criterion,String key,String value) throws InvalidOptionException {
    	if (key.equalsIgnoreCase("permid")) {
    		criterion.withPermId().thatContains(value);
    	}    	
    	else {
    		throw new InvalidOptionException("Invalid attribute name:"+key);
    	}
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

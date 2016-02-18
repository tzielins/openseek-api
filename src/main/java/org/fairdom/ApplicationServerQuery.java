package org.fairdom;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.AbstractEntitySearchCriteria;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.fetchoptions.DataSetFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.id.DataSetPermId;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.id.IDataSetId;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.search.DataSetSearchCriteria;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.Experiment;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.fetchoptions.ExperimentFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.id.ExperimentPermId;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.id.IExperimentId;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.search.ExperimentSearchCriteria;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.fetchoptions.SampleFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.id.ISampleId;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.id.SamplePermId;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.search.SampleSearchCriteria;
import ch.systemsx.cisd.common.spring.HttpInvokerUtils;
import ch.systemsx.cisd.common.ssl.SslCertificateHelper;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.json.GenericObjectMapper;

/**
 * Created by quyennguyen on 13/02/15.
 */
public class ApplicationServerQuery {
    private static String endpoint;    
    private static String sessionToken;
    private static IApplicationServerApi as;

    public ApplicationServerQuery(String startEndpoint, String startSessionToken ){
    	endpoint = startEndpoint;
        sessionToken = startSessionToken;
        as = ApplicationServerQuery.as(endpoint);
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
        	JSONObject endpoints = options.getEndpoints();
        	JSONObject query = options.getQuery();
        
        	ApplicationServerQuery asQuery = new ApplicationServerQuery(endpoints.get("as").toString(), endpoints.get("sessionToken").toString());
        	List result;
        	if (query.get("queryType")==QueryType.PROPERTY) {
            	result = asQuery.query(query.get("entityType").toString(), QueryType.PROPERTY,query.get("property").toString(), query.get("propertyValue").toString());            	
            }
            else {
            	result = asQuery.query(query.get("entityType").toString(), QueryType.ATTRIBUTE,query.get("attribute").toString(), query.get("attributeValue").toString());   
            }            
            String jsonResult = asQuery.jsonResult(result);
            System.out.println(jsonResult);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            System.exit(-1);
        }
        System.exit(0);
    }
    
    public static IApplicationServerApi as(String endpoint) {       
        SslCertificateHelper.trustAnyCertificate(endpoint);
        IApplicationServerApi as = HttpInvokerUtils
                .createServiceStub(IApplicationServerApi.class, endpoint
                        + IApplicationServerApi.SERVICE_URL, 500000);

        return as;
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
    
    public List query(String type, QueryType queryType, String key, List<String> values) throws InvalidOptionException {
        List result = null;
        if (queryType==QueryType.ATTRIBUTE) {
        	if (type.equals("Experiment")) {
                result = experimentsByAttribute(key, values);
            }else if (type.equals("Sample")) {
                result = samplesByAttribute(key, values);
            }else if (type.equals("DataSet")) {
                result = dataSetsByAttribute(key, values);
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
    	if (experiment.getModifier() != null)
    		map.put("modifier",experiment.getModifier().getUserId());
    	else
    		map.put("modifier",null);
    	
    	map.put("tags", experiment.getTags());
    	map.put("registerator", experiment.getRegistrator().getUserId());
    	Map<String,String> expType = new HashMap<String, String>();
    	if (experiment.getType()!=null) {
    		if (experiment.getType().getDescription()!=null) {
    			expType.put("description",experiment.getType().getDescription());
    		}
    		else {
    			expType.put("description","");
    		}
    		if (experiment.getType().getCode()!=null) {
    			expType.put("code", experiment.getType().getCode());
    		}
    		else {
    			expType.put("code","");
    		}        	
    	}    	    	    	
    	map.put("experiment_type", expType);
    	List<String> sampleIds = new ArrayList<String>();
    	for (Sample sample : experiment.getSamples()) {
    		sampleIds.add(sample.getPermId().getPermId());
    	}
    	map.put("samples", sampleIds);
    	List<String> datasetIds = new ArrayList<String>();
    	for (DataSet dataset : experiment.getDataSets()) {
    		datasetIds.add(dataset.getPermId().getPermId());
    	}
    	map.put("datasets", datasetIds);
    	return map;
    }
    
    private Map<String,Object> jsonMap(DataSet dataset) {
    	Map<String,Object> map = new HashMap<String, Object>();
    	map.put("permId", dataset.getPermId().getPermId());
    	map.put("code", dataset.getCode());    	    	
    	map.put("properties", dataset.getProperties());
    	map.put("modificationDate", dataset.getModificationDate());
    	map.put("registrationDate", dataset.getRegistrationDate());    	
    	if (dataset.getModifier() != null)
    		map.put("modifier",dataset.getModifier().getUserId());
    	else
    		map.put("modifier", null);
    	
    	map.put("registerator", dataset.getRegistrator().getUserId());
    	map.put("experiment", dataset.getExperiment().getPermId().getPermId());
    	map.put("tags", dataset.getTags());
    	
    	Map<String,String> dsType = new HashMap<String, String>();
    	if (dataset.getType()!=null) {
    		if (dataset.getType().getDescription()!=null) {
    			dsType.put("description",dataset.getType().getDescription() );
    		}
    		else {
    			dsType.put("description","");
    		}
    		if (dataset.getType().getCode()!=null) {
    			dsType.put("code", dataset.getType().getCode());
    		}
    		else {
    			dsType.put("code","");
    		}   
    	}    	    	    	
    	map.put("dataset_type", dsType);    	
    	
    	List<String> sampleIds = new ArrayList<String>();
    	if (dataset.getSample()!=null) {
    		sampleIds.add(dataset.getSample().getPermId().getPermId());
    	}
    	map.put("samples", sampleIds);
    	return map;
    }
    
    private Map<String,Object> jsonMap(Sample sample) {
    	Map<String,Object> map = new HashMap<String, Object>();
    	map.put("permId", sample.getPermId().getPermId());
    	map.put("code", sample.getCode());    	
    	map.put("properties", sample.getProperties());
    	map.put("identifier",sample.getIdentifier().getIdentifier());
    	map.put("modificationDate", sample.getModificationDate());
    	map.put("registrationDate", sample.getRegistrationDate());   
    	if (sample.getModifier() != null)
    		map.put("modifier",sample.getModifier().getUserId());
    	else
    		map.put("modifier", null);
    	
    	map.put("registerator", sample.getRegistrator().getUserId());
    	
    	Map<String,String> sampleType = new HashMap<String, String>();
    	if (sample.getType()!=null) {
    		if (sample.getType().getDescription()!=null) {
    			sampleType.put("description",sample.getType().getDescription() );
    		}
    		else {
    			sampleType.put("description","");
    		}
    		if (sample.getType().getCode()!=null) {
    			sampleType.put("code", sample.getType().getCode());
    		}
    		else {
    			sampleType.put("code","");
    		}  
    		sampleType.put("code", sample.getType().getCode());
    	}    	    	    	
    	map.put("sample_type", sampleType);    	    
    	
    	if (sample.getExperiment()!=null) {
    		map.put("experiment", sample.getExperiment().getPermId().getPermId());
    	}    	
    	map.put("tags", sample.getTags());
    	List<String> datasetIds = new ArrayList<String>();
    	for (DataSet dataset : sample.getDataSets()) {
    		datasetIds.add(dataset.getPermId().getPermId());
    	}
    	map.put("datasets", datasetIds);
    	return map;
    }
    
    public List <Experiment> experimentsByAttribute(String attribute,List<String> values) throws InvalidOptionException {    	
    	ExperimentFetchOptions options = new ExperimentFetchOptions();
        options.withProperties();
        options.withSamples();
        options.withDataSets();
        options.withProject();
        options.withModifier();
        options.withRegistrator();
        options.withTags();
        options.withType();
        
        List<ExperimentPermId> permIds = buildExperimentPermIdArray(values);


        //FIXME: this is very hacky, but a quick way to get around searching for all, or by a list of ids
        //the attribute paramater is currently redundant, as we can search by permId only
        if (permIds.size()>0) {
        	Map<IExperimentId, Experiment> mapExperiments = as.mapExperiments(sessionToken, permIds, options);
        	List<Experiment> experiments =  new ArrayList<Experiment>(mapExperiments.values());        	
        	return experiments;            
        }
        else {
        	ExperimentSearchCriteria criterion = new ExperimentSearchCriteria();
            updateCriterianForAttribute(criterion, attribute, values.get(0));
            return as.searchExperiments(sessionToken, criterion, options).getObjects();
        }
    }
    
    public List<Experiment> experimentsByAttribute(String attribute,String value) throws InvalidOptionException {
    	List<String> values = new ArrayList<String>(Arrays.asList(new String[]{value}));
        return experimentsByAttribute(attribute,values);
    	
    }
    
    public List <DataSet> dataSetsByAttribute(String attribute, List<String> values) throws InvalidOptionException{    	
        
    	List<DataSetPermId> permIds = buildDataSetPermIdArray(values);

        DataSetFetchOptions options = new DataSetFetchOptions();
        options.withProperties();
        options.withSample();
        options.withExperiment();
        options.withModifier();
        options.withRegistrator();
        options.withTags();
        options.withType();

        //FIXME: this is very hacky, but a quick way to get around searching for all, or by a list of ids
        //the attribute paramater is currently redundant, as we can search by permId only
        if (permIds.size()>0) {
        	Map<IDataSetId, DataSet> mapDataSets = as.mapDataSets(sessionToken, permIds, options);
            return Arrays.asList(mapDataSets.values().toArray(new DataSet []{}));
        }
        else {
        	DataSetSearchCriteria criterion = new DataSetSearchCriteria();
            updateCriterianForAttribute(criterion, attribute, values.get(0));
            return as.searchDataSets(sessionToken, criterion, options).getObjects();
        }
        
    }

	private List<DataSetPermId> buildDataSetPermIdArray(List<String> values) {
		List<DataSetPermId> permIds = new ArrayList<DataSetPermId>();
    	for (String value : values) {
    		if (value.length()>0) {
    			permIds.add(new DataSetPermId(value));
    		}
    	}
		return permIds;
	}
	
	private List<ExperimentPermId> buildExperimentPermIdArray(List<String> values) {
		List<ExperimentPermId> permIds = new ArrayList<ExperimentPermId>();
    	for (String value : values) {
    		if (value.length()>0) {
    			permIds.add(new ExperimentPermId(value));
    		}
    	}
		return permIds;
	}
	
	private List<SamplePermId> buildSamplePermIdArray(List<String> values) {
		List<SamplePermId> permIds = new ArrayList<SamplePermId>();
    	for (String value : values) {
    		if (value.length()>0) {
    			permIds.add(new SamplePermId(value));
    		}
    	}
		return permIds;
	}
    
    public List <DataSet> dataSetsByAttribute(String attribute, String value) throws InvalidOptionException{
        List<String> values = new ArrayList<String>(Arrays.asList(new String[]{value}));
        return dataSetsByAttribute(attribute,values);
    }
    
    public List <Sample> samplesByAttribute(String attribute, String value) throws InvalidOptionException{
    	List<String> values = new ArrayList<String>(Arrays.asList(new String[]{value}));
        return samplesByAttribute(attribute,values);
    }
    
    public List <Sample> samplesByAttribute(String attribute, List<String> values) throws InvalidOptionException{                
        SampleFetchOptions options = new SampleFetchOptions();
        options.withProperties();
        options.withExperiment();
        options.withDataSets();
        options.withModifier();
        options.withRegistrator();
        options.withTags();
        options.withType();
        
        List<SamplePermId> permIds = buildSamplePermIdArray(values);

      //FIXME: this is very hacky, but a quick way to get around searching for all, or by a list of ids
        //the attribute paramater is currently redundant, as we can search by permId only
        if (permIds.size()>0) {
        	Map<ISampleId, Sample> mapSamples = as.mapSamples(sessionToken, permIds, options);
            return Arrays.asList(mapSamples.values().toArray(new Sample []{}));
        }
        else {
        	SampleSearchCriteria criterion = new SampleSearchCriteria();
            updateCriterianForAttribute(criterion, attribute, values.get(0));
            return as.searchSamples(sessionToken, criterion, options).getObjects();
        }

        
    }
        
    
    private void updateCriterianForAttribute(AbstractEntitySearchCriteria<?> criterion,String key,String value) throws InvalidOptionException {
    	if (key.equalsIgnoreCase("permid")) {
    		criterion.withPermId().thatContains(value);
    	}    	
    	else {
    		throw new InvalidOptionException("Invalid attribute name:"+key);
    	}
    }
    
    public List <Experiment> experimentsByProperty(String property, String propertyValue){
        ExperimentSearchCriteria criterion = new ExperimentSearchCriteria();
        criterion.withProperty(property).thatEquals(propertyValue);

        ExperimentFetchOptions options = new ExperimentFetchOptions();        
        options.withProperties();
        options.withSamples();
        options.withDataSets();
        options.withProject();
        options.withModifier();
        options.withRegistrator();
        options.withTags();
        options.withType();

        List <Experiment> experiments = as.searchExperiments(sessionToken, criterion, options).getObjects();
        return experiments;
    }

    public List <Sample> samplesByProperty(String property, String propertyValue){
        SampleSearchCriteria criterion = new SampleSearchCriteria();
        criterion.withProperty(property).thatEquals(propertyValue);

        SampleFetchOptions options = new SampleFetchOptions();
        options.withProperties();
        options.withExperiment();
        options.withDataSets();
        options.withModifier();
        options.withRegistrator();
        options.withTags();
        options.withType();

        List <Sample> samples = as.searchSamples(sessionToken, criterion, options).getObjects();
        return samples;
    }        

    public List <DataSet> dataSetsByProperty(String property, String propertyValue){
        DataSetSearchCriteria criterion = new DataSetSearchCriteria();
        criterion.withProperty(property).thatEquals(propertyValue);

        DataSetFetchOptions options = new DataSetFetchOptions();
        options.withProperties();
        options.withSample();
        options.withExperiment();
        options.withModifier();
        options.withRegistrator();
        options.withTags();
        options.withType();

        List <DataSet> dataSets = as.searchDataSets(sessionToken, criterion, options).getObjects();
        return dataSets;
    }
    
}

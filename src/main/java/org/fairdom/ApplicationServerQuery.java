package org.fairdom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.AbstractEntitySearchCriteria;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSetType;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.fetchoptions.DataSetFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.fetchoptions.DataSetTypeFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.search.DataSetSearchCriteria;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.search.DataSetTypeSearchCriteria;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.Experiment;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.ExperimentType;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.fetchoptions.ExperimentFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.fetchoptions.ExperimentTypeFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.search.ExperimentSearchCriteria;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.search.ExperimentTypeSearchCriteria;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.SampleType;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.fetchoptions.SampleFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.fetchoptions.SampleTypeFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.search.SampleSearchCriteria;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.search.SampleTypeSearchCriteria;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.space.Space;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.space.fetchoptions.SpaceFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.space.search.SpaceSearchCriteria;
import ch.systemsx.cisd.common.spring.HttpInvokerUtils;
import java.util.Map;
import java.util.stream.Collectors;
import org.json.simple.JSONObject;

/**
 * @author Quyen Nguyen
 * @author Stuart Owen
 */
public class ApplicationServerQuery {
	private static IApplicationServerApi as;
	private static String endpoint;
	private static String sessionToken;
        
        static final int TIMEOUT = 500000;

	public static IApplicationServerApi as(String endpoint) {
            //SslCertificateHelper.trustAnyCertificate(endpoint);
            SslCertificateHelper.addTrustedUrl(endpoint);

            
            return HttpInvokerUtils.createServiceStub(IApplicationServerApi.class,
				endpoint + IApplicationServerApi.SERVICE_URL, TIMEOUT);        
                
	}

	public ApplicationServerQuery(String startEndpoint, String startSessionToken) {
		endpoint = startEndpoint;
		sessionToken = startSessionToken;
		as = ApplicationServerQuery.as(endpoint);
	}
        
	public List<DataSet> allDatasets() throws InvalidOptionException {
            return dataSetsByAttribute("permId", "");
	}        

	public List<DataSet> datasetsByAnyField(String searchTerm) {
		DataSetFetchOptions options = dataSetFetchOptions();

		DataSetSearchCriteria criterion = new DataSetSearchCriteria();
		criterion.withAnyField().thatContains(searchTerm);
		return as.searchDataSets(sessionToken, criterion, options).getObjects();
	}

	public List<DataSet> dataSetsByAttribute(String attribute, List<String> values) throws InvalidOptionException {
		DataSetFetchOptions options = dataSetFetchOptions();

		DataSetSearchCriteria criterion = new DataSetSearchCriteria();
		criterion.withOrOperator();
		updateCriterianForAttribute(criterion, attribute, values);

		return as.searchDataSets(sessionToken, criterion, options).getObjects();

	}

	public List<DataSet> dataSetsByAttribute(String attribute, String value) throws InvalidOptionException {
		List<String> values = Arrays.asList(new String[] { value });
		return dataSetsByAttribute(attribute, values);
	}

	public List<DataSet> dataSetsByProperty(String property, String propertyValue) {
		DataSetSearchCriteria criterion = new DataSetSearchCriteria();
		criterion.withProperty(property).thatContains(propertyValue);

		DataSetFetchOptions options = dataSetFetchOptions();

		return as.searchDataSets(sessionToken, criterion, options).getObjects();
	}
        
	protected List<DataSet> dataSetsByCriteria(DataSetSearchCriteria criteria, DataSetFetchOptions fetchOptions) {

		return as.searchDataSets(sessionToken, criteria, fetchOptions).getObjects();
	}        
        
        @SuppressWarnings("unchecked")
        public List<DataSet> dataSetsByType(JSONObject query) throws InvalidOptionException {

            if (!query.containsKey("typeCode") && !query.containsKey("typeCodes"))
                throw new InvalidOptionException("Missing type code(s)");

            DataSetFetchOptions options = dataSetFetchOptions();
            DataSetSearchCriteria criterion = new DataSetSearchCriteria();

            if (query.containsKey("typeCodes")) {
                List<String> codes = Arrays.asList(query.get("typeCodes").toString().split(","));
                criterion.withType().withCodes().thatIn(codes);
            } else {
                String typeCode = (String)query.get("typeCode");
                criterion.withType().withCode().thatEquals(typeCode);
            }
            
            
            return as.searchDataSets(sessionToken, criterion, options).getObjects();
        }
        

	public List<Experiment> allExperiments() throws InvalidOptionException  {

            return experimentsByAttribute("permId", "");
	}
        
	public List<Experiment> experimentsByAnyField(String searchTerm) {
		ExperimentFetchOptions options = experimentFetchOptions();

		ExperimentSearchCriteria criterion = new ExperimentSearchCriteria();
		criterion.withAnyField().thatContains(searchTerm);
		return as.searchExperiments(sessionToken, criterion, options).getObjects();
	}

	public List<Experiment> experimentsByAttribute(String attribute, List<String> values)
			throws InvalidOptionException {
		ExperimentFetchOptions options = experimentFetchOptions();

		ExperimentSearchCriteria criterion = new ExperimentSearchCriteria();
		criterion.withOrOperator();
		updateCriterianForAttribute(criterion, attribute, values);

		return as.searchExperiments(sessionToken, criterion, options).getObjects();

	}

	public List<Experiment> experimentsByAttribute(String attribute, String value) throws InvalidOptionException {
		List<String> values = Arrays.asList(new String[] { value });
		return experimentsByAttribute(attribute, values);

	}

	public List<Experiment> experimentsByProperty(String property, String propertyValue) {
		ExperimentSearchCriteria criterion = new ExperimentSearchCriteria();
		criterion.withProperty(property).thatContains(propertyValue);

		ExperimentFetchOptions options = experimentFetchOptions();

		return as.searchExperiments(sessionToken, criterion, options).getObjects();
	}
        
        @SuppressWarnings("unchecked")
        public List<Experiment> experimentsByType(JSONObject query) throws InvalidOptionException {

            if (!query.containsKey("typeCode") && !query.containsKey("typeCodes"))
                throw new InvalidOptionException("Missing type code(s)");

            ExperimentFetchOptions options = experimentFetchOptions();
            ExperimentSearchCriteria criterion = new ExperimentSearchCriteria();

            if (query.containsKey("typeCodes")) {
                List<String> codes = Arrays.asList(query.get("typeCodes").toString().split(","));
                criterion.withType().withCodes().thatIn(codes);
                
            } else {
                String typeCode = (String)query.get("typeCode");
                criterion.withType().withCode().thatEquals(typeCode);
            }
            
            
            return as.searchExperiments(sessionToken, criterion, options).getObjects();
        }
        
        
        public List<ExperimentType> allExperimentTypes() {
            
            ExperimentTypeFetchOptions fetchOptions = new ExperimentTypeFetchOptions();
            //fetchOptions.withPropertyAssignments().withSemanticAnnotations();
            fetchOptions.withPropertyAssignments();

            ExperimentTypeSearchCriteria searchCriteria = new ExperimentTypeSearchCriteria();
            
            SearchResult<ExperimentType> types = as.searchExperimentTypes(sessionToken, searchCriteria, fetchOptions);
            return types.getObjects();
            
        }       
        
        public List<ExperimentType> experimentTypesByCodes(List<String> codes) {
            
            ExperimentTypeFetchOptions fetchOptions = new ExperimentTypeFetchOptions();
            //fetchOptions.withPropertyAssignments().withSemanticAnnotations();
            fetchOptions.withPropertyAssignments();
            
            ExperimentTypeSearchCriteria searchCriteria = new ExperimentTypeSearchCriteria();            
            searchCriteria.withCodes().thatIn(codes);
            SearchResult<ExperimentType> types = as.searchExperimentTypes(sessionToken, searchCriteria, fetchOptions);
            return types.getObjects();            
        }        
        
        
        public List<? extends Object> allEntities(String type) throws InvalidOptionException {
            
            switch (type) {
                case "Experiment":
                    return allExperiments();
                case "Sample":
                    return allSamples();
                case "DataSet":
                    return allDatasets();
                case "Space":
                    return allSpaces();
                case "SampleType":
                    return allSampleTypes();
                case "DataSetType":
                    return allDataSetTypes();
                case "ExperimentType":
                    return allExperimentTypes();
                default:
                    throw new InvalidOptionException("Unrecognised type: " + type);
            }
        }        

	public List<? extends Object> query(String type, QueryType queryType, String key, List<String> values)
			throws InvalidOptionException {
		List<? extends Object> result = null;
		if (queryType == QueryType.ATTRIBUTE) {
                    switch (type) {
                        case "Experiment":
                            result = experimentsByAttribute(key, values);
                            break;
                        case "Sample":
                            result = samplesByAttribute(key, values);
                            break;
                        case "DataSet":
                            result = dataSetsByAttribute(key, values);
                            break;
                        case "Space":
                            result = spacesByAttribute(key, values);
                            break;
                        case "SampleType":
                            if (!"CODE".equals(key)) throw new InvalidOptionException("Unsupported attribute: " + key);
                            result = sampleTypesByCode(values.get(0));
                            break;
                        case "DataSetType":
                            if (!"CODE".equals(key)) throw new InvalidOptionException("Unsupported attribute: " + key);
                            result = dataSetTypesByCode(values.get(0));
                            break;
                        case "ExperimentType":
                            if (!"CODE".equals(key)) throw new InvalidOptionException("Unsupported attribute: " + key);
                            result = experimentTypesByCodes(values);
                            break;
                        default:
                            throw new InvalidOptionException("Unrecognised type: " + type);
                    }
		} else {
			throw new InvalidOptionException("It is only possible to query by ATTRIBUTE when using an array of values");
		}

		return result;
	}

	public List<? extends Object> query(String type, QueryType queryType, String key, String value)
			throws InvalidOptionException {
		List<? extends Object> result = null;
		if (null == queryType) {
                    throw new InvalidOptionException("Unrecognised query type");
                } else switch (queryType) {
                case PROPERTY:
                    switch (type) {
                        case "Experiment":
                            result = experimentsByProperty(key, value);
                            break;
                        case "Sample":
                            result = samplesByProperty(key, value);
                            break;
                        case "DataSet":
                            result = dataSetsByProperty(key, value);
                            break;
                        default:
                            throw new InvalidOptionException("Unrecognised entity type: " + type);
                    }
                    break;
                case ATTRIBUTE:
                    List<String> values = new ArrayList<>();
                    values.add(value);
                    result = query(type, queryType, key, values);
                    break;
                default:
                    throw new InvalidOptionException("Unrecognised query type");
            }

		return result;
	}

	public List<Sample> samplesByAnyField(String searchTerm) {
		SampleFetchOptions options = sampleFetchOptions();

		SampleSearchCriteria criterion = new SampleSearchCriteria();
		criterion.withAnyField().thatContains(searchTerm);
		return as.searchSamples(sessionToken, criterion, options).getObjects();
	}
        
	public List<Sample> allSamples() throws InvalidOptionException {
		return samplesByAttribute("permId", "");
	}        

	public List<Sample> samplesByAttribute(String attribute, List<String> values) throws InvalidOptionException {
		SampleFetchOptions options = sampleFetchOptions();

		SampleSearchCriteria criterion = new SampleSearchCriteria();

		criterion.withOrOperator();
		updateCriterianForAttribute(criterion, attribute, values);

		return as.searchSamples(sessionToken, criterion, options).getObjects();
	}

	public List<Sample> samplesByAttribute(String attribute, String value) throws InvalidOptionException {
		List<String> values = Arrays.asList(new String[] { value });
		return samplesByAttribute(attribute, values);
	}

	public List<Sample> samplesByProperty(String property, String propertyValue) {
		SampleSearchCriteria criterion = new SampleSearchCriteria();
		criterion.withProperty(property).thatContains(propertyValue);

		SampleFetchOptions options = sampleFetchOptions();

		return as.searchSamples(sessionToken, criterion, options).getObjects();
	}

        @SuppressWarnings("unchecked")
        public List<Sample> samplesByType(JSONObject query) throws InvalidOptionException {

            if (!query.containsKey("typeCode") && !query.containsKey("typeCodes"))
                throw new InvalidOptionException("Missing type code(s)");

            SampleFetchOptions options = sampleFetchOptions();
            SampleSearchCriteria criterion = new SampleSearchCriteria();

            if (query.containsKey("typeCodes")) {
                List<String> codes = Arrays.asList(query.get("typeCodes").toString().split(","));
                criterion.withType().withCodes().thatIn(codes);
            } else {
                String typeCode = (String)query.get("typeCode");
                criterion.withType().withCode().thatEquals(typeCode);
            }
            
            
            return as.searchSamples(sessionToken, criterion, options).getObjects();
        }
    
	public List<Space> allSpaces() throws InvalidOptionException  {
            return spacesByAttribute("permId", "");
	}        
        
	public List<Space> spacesByAttribute(String attribute, List<String> values) throws InvalidOptionException {
		SpaceFetchOptions options = new SpaceFetchOptions();
		options.withProjects().withExperiments().withDataSets();
		options.withSamples().withDataSets();
		SpaceSearchCriteria criterion = new SpaceSearchCriteria();
                
		criterion.withOrOperator();
		for (String value : values) {
			criterion.withPermId().thatContains(value);
		}

		return as.searchSpaces(sessionToken, criterion, options).getObjects();
	}

	public List<Space> spacesByAttribute(String attribute, String value) throws InvalidOptionException {
		List<String> values = Arrays.asList(new String[] { value });
		return spacesByAttribute(attribute, values);
	}
        
        protected <K> boolean notNullAtKey(Map map,K key) {
            return map.containsKey(key) && ( map.get(key) != null);
        }
        
        public List<SampleType> allSampleTypes() {
            
            SampleTypeFetchOptions fetchOptions = new SampleTypeFetchOptions();
            //fetchOptions.withSemanticAnnotations();
            //fetchOptions.withPropertyAssignments().withSemanticAnnotations();
            fetchOptions.withPropertyAssignments();

            SampleTypeSearchCriteria searchCriteria = new SampleTypeSearchCriteria();
            
            SearchResult<SampleType> types = as.searchSampleTypes(sessionToken, searchCriteria, fetchOptions);
            return types.getObjects();
            
        }    
        
       
        
        public List<SampleType> sampleTypesByCode(String code) {
            
            SampleTypeFetchOptions fetchOptions = new SampleTypeFetchOptions();
            //fetchOptions.withSemanticAnnotations();
            //fetchOptions.withPropertyAssignments().withSemanticAnnotations();
            fetchOptions.withPropertyAssignments();
            
            SampleTypeSearchCriteria searchCriteria = new SampleTypeSearchCriteria();
            
            if (code.contains(",")) {
                List<String> codes = Arrays.asList(code.split(","));
                searchCriteria.withCodes().thatIn(codes);
            } else {
                searchCriteria.withCode().thatEquals(code);
            }
            
            SearchResult<SampleType> types = as.searchSampleTypes(sessionToken, searchCriteria, fetchOptions);
            return types.getObjects();            
        }        
        
        public List<SampleType> sampleTypesBySemantic(JSONObject query) throws AuthenticationException {
            
            throw new UnsupportedOperationException("Semantic annotationa are not available in official release");
            /*
            SampleTypeFetchOptions fetchOptions = new SampleTypeFetchOptions();
            fetchOptions.withSemanticAnnotations();
            fetchOptions.withPropertyAssignments().withSemanticAnnotations();
            
            SampleTypeSearchCriteria searchCriteria = new SampleTypeSearchCriteria();
                
            SemanticAnnotationSearchCriteria semCriteria = searchCriteria.withSemanticAnnotations();
        
            
            if (notNullAtKey(query,"predicateOntologyId"))
                semCriteria.withPredicateOntologyId().thatEquals(query.get("predicateOntologyId").toString());

            if (notNullAtKey(query,"predicateOntologyVersion"))
                semCriteria.withPredicateOntologyVersion().thatEquals(query.get("predicateOntologyVersion").toString());
            
            if (notNullAtKey(query,"predicateAccessionId"))
                semCriteria.withPredicateAccessionId().thatEquals(query.get("predicateAccessionId").toString());

            if (notNullAtKey(query,"descriptorOntologyId"))
                semCriteria.withDescriptorOntologyId().thatEquals(query.get("descriptorOntologyId").toString());

            if (notNullAtKey(query,"descriptorOntologyVersion"))
                semCriteria.withDescriptorOntologyVersion().thatEquals(query.get("descriptorOntologyVersion").toString());
            
            if (notNullAtKey(query,"descriptorAccessionId"))
                semCriteria.withDescriptorAccessionId().thatEquals(query.get("descriptorAccessionId").toString());

            SearchResult<SampleType> types = as.searchSampleTypes(sessionToken, searchCriteria, fetchOptions);
            return types.getObjects();
            */
        }        
        
        public List<DataSetType> dataSetTypesByCode(String code) {
            
            DataSetTypeFetchOptions fetchOptions = new DataSetTypeFetchOptions();
            //fetchOptions.withPropertyAssignments().withSemanticAnnotations();
            fetchOptions.withPropertyAssignments();
            
            DataSetTypeSearchCriteria searchCriteria = new DataSetTypeSearchCriteria();
            
            searchCriteria.withCode().thatEquals(code);
            
            SearchResult<DataSetType> types = as.searchDataSetTypes(sessionToken, searchCriteria, fetchOptions);
            return types.getObjects();            
        }  
        
        public List<DataSetType> allDataSetTypes() {
            
            DataSetTypeFetchOptions fetchOptions = new DataSetTypeFetchOptions();
            //fetchOptions.withPropertyAssignments().withSemanticAnnotations();
            fetchOptions.withPropertyAssignments();

            DataSetTypeSearchCriteria searchCriteria = new DataSetTypeSearchCriteria();
            
            SearchResult<DataSetType> types = as.searchDataSetTypes(sessionToken, searchCriteria, fetchOptions);
            return types.getObjects();
            
        }           

	private DataSetFetchOptions dataSetFetchOptions() {
		DataSetFetchOptions options = new DataSetFetchOptions();
		options.withProperties();
		options.withSample();
		options.withExperiment();
		options.withModifier();
		options.withRegistrator();
		options.withTags();
		options.withType();
		return options;
	}

	private ExperimentFetchOptions experimentFetchOptions() {
		ExperimentFetchOptions options = new ExperimentFetchOptions();
		options.withProperties();
		options.withSamples();
		options.withDataSets();
		options.withProject();
		options.withModifier();
		options.withRegistrator();
		options.withTags();
		options.withType();
		return options;
	}

	private SampleFetchOptions sampleFetchOptions() {
		SampleFetchOptions options = new SampleFetchOptions();
		options.withProperties();
		options.withExperiment();
		options.withDataSets();
		options.withModifier();
		options.withRegistrator();
		options.withTags();
		options.withType();
		return options;
	}

	private void updateCriterianForAttribute(AbstractEntitySearchCriteria<?> criteria, String key, List<String> values)
			throws InvalidOptionException {
		if (key.equalsIgnoreCase("permid")) {
			for (String value : values) {
				criteria.withPermId().thatContains(value);
			}
		} else {
			throw new InvalidOptionException("Invalid attribute name:" + key);
		}
	}













}

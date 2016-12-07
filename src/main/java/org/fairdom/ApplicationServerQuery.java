package org.fairdom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.AbstractEntitySearchCriteria;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.DataSet;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.fetchoptions.DataSetFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.dataset.search.DataSetSearchCriteria;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.Experiment;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.fetchoptions.ExperimentFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.experiment.search.ExperimentSearchCriteria;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.Sample;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.fetchoptions.SampleFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.sample.search.SampleSearchCriteria;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.space.Space;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.space.fetchoptions.SpaceFetchOptions;
import ch.ethz.sis.openbis.generic.asapi.v3.dto.space.search.SpaceSearchCriteria;
import ch.systemsx.cisd.common.spring.HttpInvokerUtils;
import ch.systemsx.cisd.common.ssl.SslCertificateHelper;

/**
 * @author Quyen Nguyen
 * @author Stuart Owen
 */
public class ApplicationServerQuery {
	private static IApplicationServerApi as;
	private static String endpoint;
	private static String sessionToken;

	public static IApplicationServerApi as(String endpoint) {
		SslCertificateHelper.trustAnyCertificate(endpoint);
		IApplicationServerApi as = HttpInvokerUtils.createServiceStub(IApplicationServerApi.class,
				endpoint + IApplicationServerApi.SERVICE_URL, 500000);

		return as;
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

			ApplicationServerQuery asQuery = new ApplicationServerQuery(endpoints.get("as").toString(),
					endpoints.get("sessionToken").toString());
			List result;
			if (query.get("queryType").toString().equals(QueryType.PROPERTY.toString())) {
				result = asQuery.query(query.get("entityType").toString(), QueryType.PROPERTY,
						query.get("property").toString(), query.get("propertyValue").toString());
			} else {
				List<String> attributeValues = options.constructAttributeValues(query.get("attributeValue").toString());
				result = asQuery.query(query.get("entityType").toString(), QueryType.ATTRIBUTE,
						query.get("attribute").toString(), attributeValues);
			}
			String jsonResult = new JSONCreator(result).getJSON();
			System.out.println(jsonResult);
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
			ex.printStackTrace();
			System.exit(-1);
		}
		System.exit(0);
	}

	public ApplicationServerQuery(String startEndpoint, String startSessionToken) {
		endpoint = startEndpoint;
		sessionToken = startSessionToken;
		as = ApplicationServerQuery.as(endpoint);
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
		List<String> values = new ArrayList<String>(Arrays.asList(new String[] { value }));
		return dataSetsByAttribute(attribute, values);
	}

	public List<DataSet> dataSetsByProperty(String property, String propertyValue) {
		DataSetSearchCriteria criterion = new DataSetSearchCriteria();
		criterion.withProperty(property).thatContains(propertyValue);

		DataSetFetchOptions options = dataSetFetchOptions();

		return as.searchDataSets(sessionToken, criterion, options).getObjects();
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
		List<String> values = new ArrayList<String>(Arrays.asList(new String[] { value }));
		return experimentsByAttribute(attribute, values);

	}

	public List<Experiment> experimentsByProperty(String property, String propertyValue) {
		ExperimentSearchCriteria criterion = new ExperimentSearchCriteria();
		criterion.withProperty(property).thatContains(propertyValue);

		ExperimentFetchOptions options = experimentFetchOptions();

		return as.searchExperiments(sessionToken, criterion, options).getObjects();
	}

	public List query(String type, QueryType queryType, String key, List<String> values) throws InvalidOptionException {
		List result = null;
		if (queryType == QueryType.ATTRIBUTE) {
			if (type.equals("Experiment")) {
				result = experimentsByAttribute(key, values);
			} else if (type.equals("Sample")) {
				result = samplesByAttribute(key, values);
			} else if (type.equals("DataSet")) {
				result = dataSetsByAttribute(key, values);
			} else if (type.equals("Space")) {
				result = spacesByAttribute(key, values);
			} else {
				throw new InvalidOptionException("Unrecognised type: " + type);
			}
		}

		return result;
	}

	public List query(String type, QueryType queryType, String key, String value) throws InvalidOptionException {
		List result = null;
		if (queryType == QueryType.PROPERTY) {
			if (type.equals("Experiment")) {
				result = experimentsByProperty(key, value);
			} else if (type.equals("Sample")) {
				result = samplesByProperty(key, value);
			} else if (type.equals("DataSet")) {
				result = dataSetsByProperty(key, value);
			} else {
				throw new InvalidOptionException("Unrecognised type: " + type);
			}
		} else if (queryType == QueryType.ATTRIBUTE) {
			if (type.equals("Experiment")) {
				result = experimentsByAttribute(key, value);
			} else if (type.equals("Sample")) {
				result = samplesByAttribute(key, value);
			} else if (type.equals("DataSet")) {
				result = dataSetsByAttribute(key, value);
			} else if (type.equals("Space")) {
				result = spacesByAttribute(key, value);
			} else {
				throw new InvalidOptionException("Unrecognised type: " + type);
			}
		}

		return result;
	}

	public List<Sample> samplesByAnyField(String searchTerm) {
		SampleFetchOptions options = sampleFetchOptions();

		SampleSearchCriteria criterion = new SampleSearchCriteria();
		criterion.withAnyField().thatContains(searchTerm);
		return as.searchSamples(sessionToken, criterion, options).getObjects();
	}

	public List<Sample> samplesByAttribute(String attribute, List<String> values) throws InvalidOptionException {
		SampleFetchOptions options = sampleFetchOptions();

		SampleSearchCriteria criterion = new SampleSearchCriteria();
		criterion.withOrOperator();
		updateCriterianForAttribute(criterion, attribute, values);

		return as.searchSamples(sessionToken, criterion, options).getObjects();
	}

	public List<Sample> samplesByAttribute(String attribute, String value) throws InvalidOptionException {
		List<String> values = new ArrayList<String>(Arrays.asList(new String[] { value }));
		return samplesByAttribute(attribute, values);
	}

	public List<Sample> samplesByProperty(String property, String propertyValue) {
		SampleSearchCriteria criterion = new SampleSearchCriteria();
		criterion.withProperty(property).thatContains(propertyValue);

		SampleFetchOptions options = sampleFetchOptions();

		return as.searchSamples(sessionToken, criterion, options).getObjects();
	}

	public List<Space> spacesByAttribute(String attribute, List<String> values) throws InvalidOptionException {
		SpaceFetchOptions options = new SpaceFetchOptions();
		options.withProjects();

		SpaceSearchCriteria criterion = new SpaceSearchCriteria();
		criterion.withOrOperator();
		for (String value : values) {
			criterion.withPermId().thatContains(value);
		}

		return as.searchSpaces(sessionToken, criterion, options).getObjects();
	}

	public List<Space> spacesByAttribute(String attribute, String value) throws InvalidOptionException {
		List<String> values = new ArrayList<String>(Arrays.asList(new String[] { value }));
		return spacesByAttribute(attribute, values);
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

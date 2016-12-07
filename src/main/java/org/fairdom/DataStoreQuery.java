package org.fairdom;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.DataSetFile;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.fetchoptions.DataSetFileFetchOptions;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.search.DataSetFileSearchCriteria;
import ch.systemsx.cisd.openbis.generic.shared.api.v1.json.GenericObjectMapper;

/**
 * Created by quyennguyen on 13/02/15.
 */
public class DataStoreQuery extends DataStoreStream {

	public DataStoreQuery(String startEndpoint, String startSessionToken) {
		super(startEndpoint, startSessionToken);
	}

	public List<DataSetFile> datasetFilesByAttribute(String attribute, List<String> values)
			throws InvalidOptionException {

		// FIXME: ability to search by OR operator, through set of PermID
		// criteria.withOperator(SearchOperator.OR);
		// for now loop through the permids
		List<DataSetFile> result = new ArrayList<DataSetFile>();
		for (String value : values) {
			DataSetFileSearchCriteria criteria = new DataSetFileSearchCriteria();
			criteria.withDataSet().withPermId().thatContains(value);

			SearchResult<DataSetFile> files_result = dss.searchFiles(sessionToken, criteria,
					new DataSetFileFetchOptions());
			result.addAll(files_result.getObjects());

		}
		return result;

	}

	public List<DataSetFile> datasetFilesByAttribute(String attribute, String value) throws InvalidOptionException {
		List<String> values = new ArrayList<String>(Arrays.asList(new String[] { value }));
		return datasetFilesByAttribute(attribute, values);
	}

	public List<DataSetFile> datasetFilesByProperty(String property, String propertyValue) {
		DataSetFileSearchCriteria criteria = new DataSetFileSearchCriteria();
		criteria.withDataSet().withProperty(property).thatContains(propertyValue);

		SearchResult<DataSetFile> result = dss.searchFiles(sessionToken, criteria, new DataSetFileFetchOptions());
		List<DataSetFile> searchFiles = result.getObjects();
		return searchFiles;
	}

	
	public List query(String type, QueryType queryType, String key, List<String> values) throws InvalidOptionException {
		List result = null;
		if (queryType == QueryType.ATTRIBUTE) {
			if (type.equals("DataSetFile")) {
				result = datasetFilesByAttribute(key, values);
			} else {
				throw new InvalidOptionException("Unrecognised type: " + type);
			}
		}

		return result;
	}

	public List query(String type, QueryType queryType, String key, String value) throws InvalidOptionException {
		List result = null;
		if (queryType == QueryType.PROPERTY) {
			if (type.equals("DataSetFile")) {
				result = datasetFilesByProperty(key, value);
			} else {
				throw new InvalidOptionException("Unrecognised type: " + type);
			}
		} else if (queryType == QueryType.ATTRIBUTE) {
			if (type.equals("DataSetFile")) {
				result = datasetFilesByAttribute(key, value);
			} else {
				throw new InvalidOptionException("Unrecognised type: " + type);
			}
		}

		return result;
	}

}

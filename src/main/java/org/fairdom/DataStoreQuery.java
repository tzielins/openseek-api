package org.fairdom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.ethz.sis.openbis.generic.asapi.v3.dto.common.search.SearchResult;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.DataSetFile;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.fetchoptions.DataSetFileFetchOptions;
import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.search.DataSetFileSearchCriteria;

/**
 * @author Quyen Nugyen
 * @author Stuart Owen
 */
public class DataStoreQuery extends DataStoreStream {

	public DataStoreQuery(String startEndpoint, String startSessionToken) {
		super(startEndpoint, startSessionToken);
	}

	public List<DataSetFile> datasetFilesByDataSetPermIds(List<String> dataSetPermIds)
			throws InvalidOptionException {
		
		List<DataSetFile> result = new ArrayList<DataSetFile>();
		for (String dataSetPermId : dataSetPermIds) {
			DataSetFileSearchCriteria criteria = new DataSetFileSearchCriteria();			
			criteria.withDataSet().withPermId().thatContains(dataSetPermId);

			SearchResult<DataSetFile> files_result = dss.searchFiles(sessionToken, criteria,
					new DataSetFileFetchOptions());
			result.addAll(files_result.getObjects());

		}
		return result;

	}

	public List<DataSetFile> datasetFilesByDataSetPermId(String dataSetPermId) throws InvalidOptionException {
		List<String> values = new ArrayList<String>(Arrays.asList(new String[] { dataSetPermId }));
		return datasetFilesByDataSetPermIds(values);
	}

	public List<DataSetFile> datasetFilesByProperty(String property, String propertyValue) {
		DataSetFileSearchCriteria criteria = new DataSetFileSearchCriteria();
		criteria.withDataSet().withProperty(property).thatContains(propertyValue);

		SearchResult<DataSetFile> result = dss.searchFiles(sessionToken, criteria, new DataSetFileFetchOptions());
		List<DataSetFile> searchFiles = result.getObjects();
		return searchFiles;
	}

	
	public List<? extends Object> query(String type, QueryType queryType, String key, List<String> values) throws InvalidOptionException {
		List<? extends Object> result = null;
		if (queryType == QueryType.ATTRIBUTE) {
			if (type.equals("DataSetFile")) {
				if (!key.equals("dataSetPermId")) {
					throw new InvalidOptionException("Only dataSetPermId is currently supported");
				}
				result = datasetFilesByDataSetPermIds(values);
			} else {
				throw new InvalidOptionException("Unrecognised type: " + type);
			}
		}else {
			throw new InvalidOptionException("It is only possible to query by ATTRIBUTE when using an array of values");
		}

		return result;
	}

	public List<? extends Object> query(String type, QueryType queryType, String key, String value) throws InvalidOptionException {
		List<? extends Object> result = null;
		if (queryType == QueryType.PROPERTY) {
			if (type.equals("DataSetFile")) {
				result = datasetFilesByProperty(key, value);
			} else {
				throw new InvalidOptionException("Unrecognised type: " + type);
			}
		} else if (queryType == QueryType.ATTRIBUTE) {
			List<String> values = new ArrayList<String>();
			values.add(value);
			result=query(type,queryType,key,value);
		} else {
			throw new InvalidOptionException("Unrecognised query type");
		}					

		return result;
	}

}

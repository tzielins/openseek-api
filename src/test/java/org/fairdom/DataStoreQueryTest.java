package org.fairdom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ch.ethz.sis.openbis.generic.dssapi.v3.dto.datasetfile.DataSetFile;

/**
 * Created by quyennguyen on 13/02/15.
 * 
 */
public class DataStoreQueryTest {

	private static String endpoint;
	private static String sessionToken;

	@Before
	public void setUp() throws AuthenticationException {
		Authentication au = new Authentication("https://openbis-api.fair-dom.org/openbis/openbis", "apiuser",
				"apiuser");
		endpoint = "https://openbis-api.fair-dom.org/datastore_server";
		sessionToken = au.sessionToken();
	}

	@Test
	public void getDataSetFileWithSeekDataFileID() throws Exception {
		DataStoreQuery query = new DataStoreQuery(endpoint, sessionToken);
		String property = "SEEK_DATAFILE_ID";
		String propertyValue = "DataFile_1";
		List<DataSetFile> files = query.datasetFilesByProperty(property, propertyValue);
		DataSetFile file = files.get(files.size() - 1);
		String perID = "20151217153943290-5#original/api-test";
		long fileLength = 25;
		assertEquals(perID, file.getPermId().toString());
		assertEquals(fileLength, file.getFileLength());
		String json = new JSONCreator(files).getJSON();
		assertTrue(JSONHelper.isValidJSON(json));
	}

	@Test
	public void jsonResultforDataSetFile() throws Exception {
		DataStoreQuery query = new DataStoreQuery(endpoint, sessionToken);
		String property = "SEEK_DATAFILE_ID";
		String propertyValue = "DataFile_1";
		List<DataSetFile> files = query.datasetFilesByProperty(property, propertyValue);
		assertEquals(3, files.size());
		String json = new JSONCreator(files).getJSON();
		assertTrue(JSONHelper.isValidJSON(json));
	}

	@Test
	public void getDataSetFileWithDataSetPermID() throws Exception {
		DataStoreQuery query = new DataStoreQuery(endpoint, sessionToken);
		List<String> values = new ArrayList<String>(
				Arrays.asList(new String[] { "20151217153943290-5", "20160210130359377-22" }));
		List<DataSetFile> files = query.datasetFilesByDataSetPermIds(values);
		assertFalse(files.isEmpty());
	}

	@Test
	public void doQueryWithDataSetPermID() throws Exception {
		DataStoreQuery query = new DataStoreQuery(endpoint, sessionToken);
		List<String> values = new ArrayList<String>(
				Arrays.asList(new String[] { "20151217153943290-5", "20160210130359377-22" }));
		List<? extends Object> files = query.query("DataSetFile", QueryType.ATTRIBUTE, "dataSetPermId", values);
		assertFalse(files.isEmpty());
	}

	@Test(expected = InvalidOptionException.class)
	public void doQueryWithPermIDFails() throws Exception {
		DataStoreQuery query = new DataStoreQuery(endpoint, sessionToken);
		List<String> values = new ArrayList<String>(
				Arrays.asList(new String[] { "20151217153943290-5", "20160210130359377-22" }));
		query.query("DataSetFile", QueryType.ATTRIBUTE, "permId", values);
	}

}

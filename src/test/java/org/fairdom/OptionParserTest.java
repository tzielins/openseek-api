package org.fairdom;

/**
 * @author Quyen Nugyen
 * @author Stuart Owen
 */
import static org.junit.Assert.assertEquals;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.Test;

public class OptionParserTest {
	
	@Test
	public void testAccount() throws Exception {
		String account = "{\"username\":\"test\", \"password\":\"test\"}";
		String endpoints = "{\"as\":\"http://as.example.com\"}";
		String[] args = new String[] { "-account", account, "-endpoints", endpoints};
		OptionParser p = new OptionParser(args);	
		JSONObject accountArgs = p.getAccount();
		assertEquals("test", accountArgs.get("username"));
		assertEquals("test", accountArgs.get("password"));
	}
	
	@Test
	public void testEndpoints() throws Exception {
		String endpoints = "{\"as\":\"http://as.example.com\", \"dss\":\"http://dss.example.com\", \"sessionToken\":\"somevalue\"}";
		String query = "{\"entityType\":\"Experiment\", \"queryType\":\"PROPERTY\", \"property\":\"SEEK_STUDY_ID\", \"propertyValue\":\"Study_1\"}";
		String[] args = new String[] { "-endpoints", endpoints, "-query",query};
		OptionParser p = new OptionParser(args);
		JSONObject endpointsArgs = p.getEndpoints();
		assertEquals("http://as.example.com", endpointsArgs.get("as"));
		assertEquals("http://dss.example.com", endpointsArgs.get("dss"));
		assertEquals("somevalue", endpointsArgs.get("sessionToken"));
	}
	
	@Test
	public void testQuery() throws Exception {
		String endpoints = "{\"as\":\"http://as.example.com\", \"dss\":\"http://dss.example.com\", \"sessionToken\":\"somevalue\"}";
		String query = "{\"entityType\":\"Experiment\", \"queryType\":\"PROPERTY\", \"property\":\"SEEK_STUDY_ID\", \"propertyValue\":\"Study_1\"}";
		String[] args = new String[] { "-endpoints",endpoints,"-query", query};
		OptionParser p = new OptionParser(args);	
		JSONObject queryArgs = p.getQuery();
		assertEquals("Experiment", queryArgs.get("entityType"));
		assertEquals("PROPERTY", queryArgs.get("queryType"));
		assertEquals("SEEK_STUDY_ID", queryArgs.get("property"));
		assertEquals("Study_1", queryArgs.get("propertyValue"));
	}
	
	@Test
	public void testDownload() throws Exception {
		String download = "{\"downloadType\":\"file\", \"permID\":\"ID100\", \"source\":\"original/testfile\", \"dest\":\"/home/test/testfile\"}";
		String[] args = new String[] { "-download", download};
		OptionParser p = new OptionParser(args);
		JSONObject downloadArgs = p.getDownload();
		assertEquals("file", downloadArgs.get("downloadType"));
		assertEquals("ID100", downloadArgs.get("permID"));
		assertEquals("original/testfile", downloadArgs.get("source"));
		assertEquals("/home/test/testfile", downloadArgs.get("dest"));
	}
	
	@Test
	public void testMultipleArgs() throws Exception {		
		String account = "{\"username\":\"test\", \"password\":\"test\"}";
		String endpoints = "{\"as\":\"http://as.example.com\", \"dss\":\"http://dss.example.com\"}";
		String query = "{\"entityType\":\"Experiment\", \"property\":\"SEEK_STUDY_ID\", \"propertyValue\":\"Study_1\"}";
		String download = "{\"type\":\"file\", \"permID\":\"ID100\", \"source\":\"original/testfile\", \"dest\":\"/home/test/testfile\"}";

		String[] args = new String[] { "-account", account, "-endpoints", endpoints, "-query", query, "-download", download};
		OptionParser p = new OptionParser(args);
		assertEquals("test", p.getAccount().get("username"));
		assertEquals("http://as.example.com", p.getEndpoints().get("as"));
		assertEquals("Experiment", p.getQuery().get("entityType"));
		assertEquals("file", p.getDownload().get("type"));
	}

	@Test(expected = InvalidOptionException.class)
	public void testInvalidArg() throws Exception {
		String[] args = new String[] { "-ss", "something" };
		new OptionParser(args);
	}

	@Test(expected = InvalidOptionException.class)
	public void testEmptyOptionValue() throws Exception {
		String[] args = new String[] { "-account", "", "-endpoints", "   ", "-query", null };
		new OptionParser(args);
	}
	
	@Test(expected = ParseException.class)
	public void testInvalidJsonString() throws Exception {
		String[] args = new String[] { "-account", "{'username':'test'}" };
		new OptionParser(args);
	}
	
	@Test
	public void testDeterimineAction() throws Exception {
		String account = "{\"username\":\"test\", \"password\":\"test\"}";
		String as_endpoints = "{\"as\":\"http://as.example.com\"}";
		String ds_endpoints = "{\"dss\":\"http://dss.example.com\"}";
		String query = "{\"entityType\":\"Experiment\", \"property\":\"SEEK_STUDY_ID\", \"propertyValue\":\"Study_1\"}";
		String download = "{\"type\":\"file\", \"permID\":\"ID100\", \"source\":\"original/testfile\", \"dest\":\"/home/test/testfile\"}";
		
		OptionParser p = new OptionParser(new String[] {"-account",account,"-endpoints",as_endpoints});
		assertEquals(Action.LOGIN,p.getAction());
		
		p = new OptionParser(new String[] {"-endpoints",as_endpoints,"-query",query});
		assertEquals(Action.AS_QUERY,p.getAction());
		
		p = new OptionParser(new String[] {"-endpoints",ds_endpoints,"-query",query});
		assertEquals(Action.DS_QUERY,p.getAction());
		
		p = new OptionParser(new String[] {"-endpoints",ds_endpoints,"-download",download});
		assertEquals(Action.DOWNLOAD,p.getAction());
		
		
	}
	
	
}
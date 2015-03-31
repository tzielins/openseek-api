package org.fairdom;

/**
 * Created by quyennguyen on 19/02/15.
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;

import org.junit.Test;

public class OptionParserTest {

    @Test
    public void testType() throws Exception {
        String type = "Experiment";
        String[] args = new String[] { "-t", type };
        OptionParser p = new OptionParser(args);
        assertEquals(type, p.getType());
    }

    @Test
    public void testProperty() throws Exception {
        String property = "SEEK_STUDY_ID";
        String[] args = new String[] { "-p", property};
        OptionParser p = new OptionParser(args);
        assertEquals(property, p.getProperty());
    }
    
    @Test
    public void testAttribute() throws Exception {
    	String attr = "permId";
        String[] args = new String[] { "-a", attr};
        OptionParser p = new OptionParser(args);
        assertEquals(attr, p.getAttribute());
    }
        
    @Test(expected = InvalidOptionException.class)
    public void testInvalidAttribute() throws Exception {
    	String attr = "something";
        String[] args = new String[] { "-a", attr};
        OptionParser p = new OptionParser(args);        
    }
    
    @Test
    public void testAttributeValues() throws Exception {
    	String attrValue = "XXX-XXX";
        String[] args = new String[] { "-av", attrValue};
        OptionParser p = new OptionParser(args);
        assertEquals(new String[]{attrValue}, p.getAttributeValues().toArray(new String[]{}));
        
        attrValue = "XXX-XXX,YYY-YYY, ZZZ-ZZZ";
        args = new String[] { "-av", attrValue};
        p = new OptionParser(args);
        assertEquals(new String[]{"XXX-XXX","YYY-YYY","ZZZ-ZZZ"}, p.getAttributeValues().toArray(new String[]{}));
    }
    
    
    
    @Test
    public void testEmptyAttributeValue() throws Exception {
    	String attrValue = "";
        String[] args = new String[] { "-av", attrValue};
        OptionParser p = new OptionParser(args);
        assertEquals(new ArrayList<String>(), p.getAttributeValues());
    }
            

    @Test
    public void testPropertyValue() throws Exception {
        String propertyValue = "Study_1";
        String[] args = new String[] { "-pv", propertyValue};
        OptionParser p = new OptionParser(args);
        assertEquals(propertyValue, p.getPropertyValue());
    }

    @Test
    public void testUsername() throws Exception {
        String username = "test";
        String[] args = new String[] { "-u", username};
        OptionParser p = new OptionParser(args);
        assertEquals(username, p.getUsername());
    }

    @Test
    public void testPassword() throws Exception {
        String password = "test";
        String[] args = new String[] { "-pw", password};
        OptionParser p = new OptionParser(args);
        assertEquals(password, p.getPassword());
    }

    @Test
    public void testEndpoint() throws Exception {
        String endpoint = "http://example.com";
        String[] args = new String[] { "-e", endpoint};
        OptionParser p = new OptionParser(args);
        assertEquals(endpoint, p.getEndpoint());
    }

    @Test
    public void testMultipleArgs() throws Exception {
        String type = "Experiment";
        String property = "SEEK_STUDY_ID";
        String propertyValue = "Study_1";
        String username = "test";
        String password = "test";
        String endpoint = "http://example.com";

        String[] args = new String[] { "-t", type, "-p", property, "-pv", propertyValue,
                                       "-u", username, "-pw", password, "-e", endpoint};
        OptionParser p = new OptionParser(args);
        assertEquals(type, p.getType());
        assertEquals(property, p.getProperty());
        assertEquals(propertyValue, p.getPropertyValue());
        assertEquals(username, p.getUsername());
        assertEquals(password, p.getPassword());
        assertEquals(endpoint, p.getEndpoint());
    }

    @Test(expected = InvalidOptionException.class)
    public void testInvalidArg() throws Exception {
        String[] args = new String[] { "-ss", "something" };
        new OptionParser(args);
    }

    @Test(expected = InvalidOptionException.class)
    public void testEmptyOptionValue() throws Exception {
        String[] args = new String[] { "-t", "", "-p", "   ", "-pv", null };
        new OptionParser(args);
    }
}
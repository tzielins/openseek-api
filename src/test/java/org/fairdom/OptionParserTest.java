package org.fairdom;

/**
 * Created by quyennguyen on 19/02/15.
 */
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

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
    public void testPropertyValue() throws Exception {
        String propertyValue = "Study_1";
        String[] args = new String[] { "-pv", propertyValue};
        OptionParser p = new OptionParser(args);
        assertEquals(propertyValue, p.getPropertyValue());
    }

    @Test
    public void testAllArgs() throws Exception {
        String type = "Experiment";
        String property = "SEEK_STUDY_ID";
        String propertyValue = "Study_1";
        String[] args = new String[] { "-t", type, "-p", property, "-pv", propertyValue};
        OptionParser p = new OptionParser(args);
        assertEquals(type, p.getType());
        assertEquals(property, p.getProperty());
        assertEquals(propertyValue, p.getPropertyValue());
    }

    @Test(expected = InvalidOptionException.class)
    public void testBadSheet() throws Exception {
        String[] args = new String[] { "-ss", "something" };
        new OptionParser(args);
    }
}
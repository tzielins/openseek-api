//package org.fairdom;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import org.junit.Test;
import org.junit.Before;

import java.lang.String;
import java.lang.System;

import static org.junit.Assert.*;

public class AuthenticationTest {
    protected String endpoint;
    protected String username;
    protected String password;

    @Before
    public void setUp(){
        endpoint = new String("https://openbis-testing.fair-dom.org/openbis");
        username = new String("guester");
        password = new String("guester");
    }

    @Test
    public void successfullyAuthenticated() throws Exception {
        Authentication au = new Authentication(endpoint, username, password);
        String sessionToken = au.authentication();
        //assertTrue(sessionToken.matches(username.concat("(.*)")));
    }

    @Test
    public void invalidAccount() throws Exception {
        String invalidUsername = new String("test");
        String invalidPassword = new String("test");
        Authentication au = new Authentication(endpoint, invalidUsername, invalidPassword);
        String sessionToken = au.authentication();
        assertEquals("Invalid username or password", sessionToken);
    }

    @Test
    public void invalidEndpoint() throws Exception {
        String invalidEndpoint = new String("https://example.com");
        Authentication au = new Authentication(invalidEndpoint, username, password);
        String sessionToken = au.authentication();
        assertTrue(sessionToken.matches("Could not access(.*)"));
    }

}

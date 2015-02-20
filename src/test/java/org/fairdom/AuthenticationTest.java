package org.fairdom;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import org.junit.Test;
import org.junit.Before;
import org.springframework.remoting.RemoteAccessException;

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
        username = new String("api-user");
        password = new String("api-user");
    }

    @Test
    public void successfullyAuthenticated() throws Exception {
        Authentication au = new Authentication(endpoint, username, password);
        String sessionToken = au.sessionToken();
        assertTrue(sessionToken.matches(username.concat("(.*)")));
    }

    @Test (expected = AuthenticationException.class)
    public void invalidAccount() throws Exception {
        String invalidUsername = new String("test");
        String invalidPassword = new String("test");
        Authentication au = new Authentication(endpoint, invalidUsername, invalidPassword);
        au.sessionToken();
    }

    @Test(expected = RemoteAccessException.class)
    public void invalidEndpoint() throws Exception {
        String invalidEndpoint = new String("https://example.com");
        Authentication au = new Authentication(invalidEndpoint, username, password);
        au.sessionToken();
    }

}

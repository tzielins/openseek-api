package org.fairdom;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.springframework.remoting.RemoteAccessException;

public class AuthenticationTest {
    protected String endpoint;
    protected String username;
    protected String password;

    @Before
    public void setUp() throws IOException{
    	TestHelper.readCredentials().getEndpoint();
        endpoint = TestHelper.readCredentials().getEndpoint();
        username = TestHelper.readCredentials().getUsername();
        password = TestHelper.readCredentials().getPassword();
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

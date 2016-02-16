package org.fairdom;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.springframework.remoting.RemoteAccessException;

public class AuthenticationTest {
    protected String asEndpoint;
    protected String dssEndpoint;
    protected String username;
    protected String password;

    @Before
    public void setUp(){
        asEndpoint = new String("https://openbis-api.fair-dom.org/openbis/openbis");        
        username = new String("apiuser");
        password = new String("apiuser");
    }

    @Test
    public void successfullyAuthenticated() throws Exception {
        Authentication au = new Authentication(asEndpoint, username, password);
        String sessionToken = au.sessionToken();
        assertTrue(sessionToken.matches(username.concat("(.*)")));
    }

    @Test (expected = AuthenticationException.class)
    public void invalidAccount() throws Exception {
        String invalidUsername = new String("test1");
        String invalidPassword = new String("test");
        Authentication au = new Authentication(asEndpoint, invalidUsername, invalidPassword);
        au.sessionToken();
    }

    @Test(expected = RemoteAccessException.class)
    public void invalidEndpoint() throws Exception {
        String invalidEndpoint = new String("https://example.com");
        Authentication au = new Authentication(invalidEndpoint, username, password);
        au.sessionToken();
    }

}

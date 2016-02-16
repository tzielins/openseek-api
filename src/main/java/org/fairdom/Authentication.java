package org.fairdom;


import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.systemsx.cisd.common.spring.HttpInvokerUtils;
import ch.systemsx.cisd.common.ssl.SslCertificateHelper;

/**
 * Created by quyennguyen on 09/02/15.
 */
public class Authentication {
    private String asEndpoint;   
    private String username;
    private String password;

    public Authentication(String startAsEndpoint, String startUsername, String startPassword){
    	asEndpoint = startAsEndpoint;    	
        username = startUsername;
        password = startPassword;
    }
    
    public static void main(String[] args) {
        OptionParser options = null;
        try {
            options = new OptionParser(args);
        } catch (InvalidOptionException e) {
            System.err.println("Invalid option: " + e.getMessage());
            System.exit(-1);
		} catch (ParseException pe) {
			System.out.println("position: " + pe.getPosition());
			System.out.println(pe);
			System.exit(-1);
		}

        try {
        	JSONObject account = options.getAccount();
        	JSONObject endpoints = options.getEndpoints();        	
            Authentication au = new Authentication(endpoints.get("as").toString(), account.get("username").toString(), account.get("password").toString());           
            String sessionToken = au.sessionToken();
            System.out.println(sessionToken);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            System.exit(-1);
        }
        System.exit(0);
    }
    

    public IApplicationServerApi as() {
        String AS_URL = asEndpoint;
        SslCertificateHelper.trustAnyCertificate(AS_URL);
        IApplicationServerApi as = HttpInvokerUtils
                .createServiceStub(IApplicationServerApi.class, AS_URL
                        + IApplicationServerApi.SERVICE_URL, 500000);

        return as;
    }   

    public String sessionToken() throws AuthenticationException {
        String sessionToken;
        IApplicationServerApi as = as();
        sessionToken = as.login(username, password);
        if (sessionToken == null){
            throw new AuthenticationException("Invalid username or password");
        }
        return sessionToken;
    }
}


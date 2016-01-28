package org.fairdom;


import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.dssapi.v3.IDataStoreServerApi;
import ch.systemsx.cisd.common.spring.HttpInvokerUtils;
import ch.systemsx.cisd.common.ssl.SslCertificateHelper;

/**
 * Created by quyennguyen on 09/02/15.
 */
public class Authentication {
    private String asEndpoint;
    private String dssEndpoint;
    private String username;
    private String password;

    public Authentication(String startAsEndpoint, String startDssEndpoint, String startUsername, String startPassword){
    	asEndpoint = startAsEndpoint;
    	dssEndpoint = startDssEndpoint;
        username = startUsername;
        password = startPassword;
    }

    public IApplicationServerApi as() {
        String AS_URL = asEndpoint;
        SslCertificateHelper.trustAnyCertificate(AS_URL);
        IApplicationServerApi as = HttpInvokerUtils
                .createServiceStub(IApplicationServerApi.class, AS_URL
                        + IApplicationServerApi.SERVICE_URL, 500000);

        return as;
    }
    
    public IDataStoreServerApi dss() {    	
        String DSS_URL = dssEndpoint;        
        SslCertificateHelper.trustAnyCertificate(DSS_URL);
        IDataStoreServerApi dss = HttpInvokerUtils
        		.createStreamSupportingServiceStub(IDataStoreServerApi.class, DSS_URL
        				+ IDataStoreServerApi.SERVICE_URL, 500000);
        return dss;
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


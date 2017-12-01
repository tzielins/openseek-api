package org.fairdom;

import ch.ethz.sis.openbis.generic.asapi.v3.IApplicationServerApi;
import ch.systemsx.cisd.common.spring.HttpInvokerUtils;

/**
 * Created by quyennguyen on 09/02/15.
 */
public class Authentication {

	private String asEndpoint;
	private String password;

	private String username;

	public Authentication(String startAsEndpoint, String startUsername, String startPassword) {
		asEndpoint = startAsEndpoint;
		username = startUsername;
		password = startPassword;
	}

	public IApplicationServerApi as() {
		String AS_URL = asEndpoint;
		//SslCertificateHelper.trustAnyCertificate(AS_URL);                
                SslCertificateHelper.addTrustedUrl(AS_URL);
		IApplicationServerApi as = HttpInvokerUtils.createServiceStub(IApplicationServerApi.class,
				AS_URL + IApplicationServerApi.SERVICE_URL, 500000);

		return as;
	}

	public String sessionToken() throws AuthenticationException {
		String sessionToken;
		IApplicationServerApi as = as();
		sessionToken = as.login(username, password);
		if (sessionToken == null) {
			throw new AuthenticationException("Invalid username or password");
		}
		return sessionToken;
	}
}

package org.fairdom;

import ch.ethz.sis.openbis.generic.dssapi.v3.IDataStoreServerApi;
import ch.systemsx.cisd.common.spring.HttpInvokerUtils;

/**
 * Created by quyennguyen on 13/02/15.
 */
public class DataStoreStream {
	protected static IDataStoreServerApi dss;
	protected static String endpoint;
	protected static String sessionToken;

	public static IDataStoreServerApi dss(String endpoint) {
		//SslCertificateHelper.trustAnyCertificate(endpoint);
                SslCertificateHelper.addTrustedUrl(endpoint);
		IDataStoreServerApi dss = HttpInvokerUtils.createStreamSupportingServiceStub(IDataStoreServerApi.class,
				endpoint + IDataStoreServerApi.SERVICE_URL, 500000);
		return dss;
	}

	public DataStoreStream(String startEndpoint, String startSessionToken) {
		endpoint = startEndpoint;
		sessionToken = startSessionToken;
		dss = DataStoreQuery.dss(endpoint);
	}
}
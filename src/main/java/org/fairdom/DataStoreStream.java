package org.fairdom;

import ch.ethz.sis.openbis.generic.dssapi.v3.IDataStoreServerApi;
import ch.systemsx.cisd.common.spring.HttpInvokerUtils;
import ch.systemsx.cisd.common.ssl.SslCertificateHelper;

/**
 * Created by quyennguyen on 13/02/15.
 */
public class DataStoreStream {    
	protected static String endpoint;
    protected static String sessionToken;
    protected static IDataStoreServerApi dss;

    public DataStoreStream(String startEndpoint, String startSessionToken ){        
        endpoint = startEndpoint;
        sessionToken = startSessionToken;
        dss = DataStoreQuery.dss(endpoint);
    }
    
    public static IDataStoreServerApi dss(String endpoint) {
        SslCertificateHelper.trustAnyCertificate(endpoint);
        IDataStoreServerApi dss = HttpInvokerUtils
        		.createStreamSupportingServiceStub(IDataStoreServerApi.class, endpoint
        				+ IDataStoreServerApi.SERVICE_URL, 500000);
        return dss;
    }
}
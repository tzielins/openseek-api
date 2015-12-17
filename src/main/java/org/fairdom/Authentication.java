package org.fairdom;

import ch.ethz.sis.openbis.generic.shared.api.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.entity.experiment.Experiment;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.fetchoptions.experiment.ExperimentFetchOptions;
import ch.ethz.sis.openbis.generic.dss.api.v3.dto.search.DataSetFileSearchCriteria;
//import ch.ethz.sis.openbis.generic.shared.api.v3.dto.search.ExperimentSearchCriterion;
import ch.systemsx.cisd.common.spring.HttpInvokerUtils;
import ch.systemsx.cisd.common.ssl.SslCertificateHelper;

/**
 * Created by quyennguyen on 09/02/15.
 */
public class Authentication {
    private String endpoint;
    private String username;
    private String password;

    public Authentication(String startEndpoint, String startUsername, String startPassword){
        endpoint = startEndpoint;
        username = startUsername;
        password = startPassword;
    }

    public IApplicationServerApi api() {
        String AS_URL = endpoint + "/openbis";
        SslCertificateHelper.trustAnyCertificate(AS_URL);
        IApplicationServerApi api = HttpInvokerUtils
                .createServiceStub(IApplicationServerApi.class, AS_URL
                        + IApplicationServerApi.SERVICE_URL, 500000);

        return api;
    }

    public String sessionToken() throws AuthenticationException {
        String sessionToken;
        IApplicationServerApi api = api();
        sessionToken = api.login(username, password);
        if (sessionToken == null){
            throw new AuthenticationException("Invalid username or password");
        }
        return sessionToken;
    }
}


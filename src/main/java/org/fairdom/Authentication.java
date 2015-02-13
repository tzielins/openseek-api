//package org.fairdom;

import ch.ethz.sis.openbis.generic.shared.api.v3.IApplicationServerApi;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.entity.experiment.Experiment;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.fetchoptions.experiment.ExperimentFetchOptions;
import ch.ethz.sis.openbis.generic.shared.api.v3.dto.search.ExperimentSearchCriterion;
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

    public String authentication() {
        String AS_URL = endpoint + "/openbis";
        String sessionToken;
        try {
            SslCertificateHelper.trustAnyCertificate(AS_URL);
            IApplicationServerApi v3 = HttpInvokerUtils
                    .createServiceStub(IApplicationServerApi.class, AS_URL
                            + IApplicationServerApi.SERVICE_URL, 500000);

            sessionToken = v3.login(username, password);
            if(sessionToken == null) {
                sessionToken = "Invalid username or password";
            }
        } catch (Exception ex) {
            sessionToken = ex.getMessage();
        }
        return sessionToken;
    }
}


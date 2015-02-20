package org.fairdom;

import ch.ethz.sis.openbis.generic.shared.api.v3.IApplicationServerApi;

import java.util.List;

/**
 * Created by quyennguyen on 19/02/15.
 */
public class QueryMain {

    public static void main(String[] args) {
        OptionParser options = null;
        try {
            options = new OptionParser(args);
        } catch (InvalidOptionException e) {
            System.err.println("Invalid option: " + e.getMessage());
            System.exit(-1);
        }

        try {
            Authentication au = new Authentication(options.getEndpoint(), options.getUsername(), options.getPassword());
            IApplicationServerApi api = au.api();
            String sessionToken = au.sessionToken();

            OpenbisQuery query = new OpenbisQuery(api, sessionToken);
            List result = query.query(options.getType(), options.getProperty(), options.getPropertyValue());
            String jsonResult = query.jsonResult(result);
            System.out.println(jsonResult);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            System.exit(-1);
        }
        System.exit(0);
    }
}

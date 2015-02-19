package org.fairdom;

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

        OpenbisQuery query = new OpenbisQuery();
        String result = query.jsonResult(options.getType(), options.getProperty(), options.getPropertyValue());
        try {
            System.out.println(result);
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
            System.exit(-1);
        }
        System.exit(0);
    }
}

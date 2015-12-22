package org.fairdom;

/**
 * Created by quyennguyen on 19/02/15.
 */
public class OptionParser {

    private String type = null;
    private String property = null;
    private String propertyValue = null;
    private String username = null;
    private String password = null;
    private String asEndpoint = null;
    private String dssEndpoint = null;

    public OptionParser(String[] args) throws InvalidOptionException {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-t")) {
                i++;
                setType(args[i]);
                handleEmptyOptionValue(arg, getType());
            }
            else if (arg.equals("-p")) {
                i++;
                setProperty(args[i]);
                handleEmptyOptionValue(arg, getProperty());
            }
            else if (arg.equals("-pv")) {
                i++;
                setPropertyValue(args[i]);
                handleEmptyOptionValue(arg, getPropertyValue());
            }
            else if (arg.equals("-u")) {
                i++;
                setUsername(args[i]);
                handleEmptyOptionValue(arg, getUsername());
            }
            else if (arg.equals("-pw")) {
                i++;
                setPassword(args[i]);
                handleEmptyOptionValue(arg, getPassword());
            }
            else if (arg.equals("-ae")) {
                i++;
                setAsEndpoint(args[i]);
                handleEmptyOptionValue(arg, getAsEndpoint());
            }
            else if (arg.equals("-de")) {
                i++;
                setDssEndpoint(args[i]);
                handleEmptyOptionValue(arg, getDssEndpoint());
            }
            else {
                throw new InvalidOptionException("Unrecognised option: " + args[i]);
            }
        }
    }

    private void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    private void setProperty(String property) {
        this.property = property;
    }

    public String getProperty() {
        return property;
    }

    private void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    private void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    private void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    private void setAsEndpoint(String asEndpoint) {
        this.asEndpoint = asEndpoint;
    }

    public String getAsEndpoint() {
        return asEndpoint;
    }
    
    private void setDssEndpoint(String dssEndpoint) {
        this.dssEndpoint = dssEndpoint;
    }

    public String getDssEndpoint() {
        return dssEndpoint;
    }

    private void handleEmptyOptionValue(String option, String value) throws InvalidOptionException {
        if (value.trim().isEmpty()){
            throw new InvalidOptionException("Empty value for: " + option);
        }
    }
}

package org.fairdom;

/**
 * Created by quyennguyen on 19/02/15.
 */
public class OptionParser {

    private String type = null;
    private String property = null;
    private String propertyValue = null;

    public OptionParser(String[] args) throws InvalidOptionException {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-t")) {
                i++;
                setType(args[i]);
            }
            else if (arg.equals("-p")) {
                i++;
                setProperty(args[i]);
            }
            else if (arg.equals("-pv")) {
                i++;
                setPropertyValue(args[i]);
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
}

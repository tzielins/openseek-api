package org.fairdom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by quyennguyen on 19/02/15.
 */
public class OptionParser {

    private String type = null;
    private String property = null;
    private String propertyValue = null;
    private String attribute=null;
    private List<String> attributeValues=new ArrayList<String>(Arrays.asList(new String[]{""}));
    private String username = null;
    private String password = null;
    private String endpoint = null;
    
    private final List<String> validAttributes = new ArrayList<String>(Arrays.asList(new String[]{"permId"}));

    public OptionParser(String[] args) throws InvalidOptionException {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-t")) {
                i++;
                setType(args[i]);
                handleEmptyOptionValue(arg, getType());
            }
            else if (arg.equals("-a")) {
            	i++;
            	setAttribute(args[i]);
            	handleEmptyOptionValue(arg, getAttribute());
            }
            else if (arg.equals("-av")) {
            	i++;
            	setAttributeValues(args[i]);            	
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
            else if (arg.equals("-e")) {
                i++;
                setEndpoint(args[i]);
                handleEmptyOptionValue(arg, getEndpoint());
            }
            else {
                throw new InvalidOptionException("Unrecognised option: " + args[i]);
            }
        }
    }
    
    public QueryType getQueryType() throws InvalidOptionException {
    	if (getProperty()!=null) {
    		return QueryType.PROPERTY;
    	}
    	else if (getAttribute()!=null) {
    		return QueryType.ATTRIBUTE;
    	}
    	else {
    		throw new InvalidOptionException("No property or attribute has been defined");
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

    private void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getEndpoint() {
        return endpoint;
    }

    private void handleEmptyOptionValue(String option, String value) throws InvalidOptionException {
        if (value.trim().isEmpty()){
            throw new InvalidOptionException("Empty value for: " + option);
        }
    }

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) throws InvalidOptionException {
		if (validAttributes.contains(attribute)) {
			this.attribute = attribute;
		}
		else {
			throw new InvalidOptionException("The attribute "+attribute+" is invalid");
		}
		
	}

	public List<String> getAttributeValues() {
		return attributeValues;
	}

	public void setAttributeValues(String attributeValue) {
		String[] values = attributeValue.split(",");
		attributeValues=new ArrayList<String>();
		for (String value : values) {
			value = value.trim();
			if (value.length()>0) {
				attributeValues.add(value);
			}
		}
		if (attributeValues.isEmpty()) {
			attributeValues.add("");
		}
	}
}

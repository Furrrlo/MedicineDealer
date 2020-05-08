package gov.ismonnet.medicine.persistence;

import javax.inject.Inject;
import java.util.Properties;

public class PropertiesCredentialsService implements CredentialsService {

    private final Properties properties;

    @Inject PropertiesCredentialsService(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String get(String key) {
        final String value = properties.getProperty(key);
        if(value == null)
            throw new AssertionError("Missing value for key " + key + " specified in credentials properties!");
        return value;
    }

    @Override
    public int getInt(String key) {
        try {
            return Integer.parseInt(get(key));
        } catch (NumberFormatException ex) {
            throw new AssertionError("Invalid int value for key " + key + " specified in credentials properties!");
        }
    }

    @Override
    public double getDouble(String key) {
        try {
            return Double.parseDouble(get(key));
        } catch (NumberFormatException ex) {
            throw new AssertionError("Invalid double value for key " + key + " specified in credentials properties!");
        }
    }
}

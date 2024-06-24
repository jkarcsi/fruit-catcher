package util;

import exception.ConfigException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {

    private ConfigUtil() {}

    private static final String CONFIG_FILE = "config.properties";
    private static final String TEST_CONFIG_FILE = "test.properties";
    private static final Properties properties = new Properties();
    private static final Properties testProperties = new Properties();

    static {
        loadProperties(CONFIG_FILE, properties);
    }

    public static void loadTestConfig() {
        loadProperties(TEST_CONFIG_FILE, testProperties);
    }

    private static void loadProperties(String fileName, Properties props) {
        try (InputStream input = ConfigUtil.class.getClassLoader().getResourceAsStream(fileName)) {
            if (input == null) {
                throw new ConfigException("Configuration file not found: " + fileName);
            }
            props.load(input);
        } catch (IOException e) {
            throw new ConfigException("Failed to load configuration from file: " + fileName, e);
        }
    }

    public static String getConfig(String key) {
        return properties.getProperty(key);
    }

    public static String getTestConfig(String key) {
        return testProperties.getProperty(key);
    }
}

package utils;

import exceptions.ConfigException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {
    private ConfigUtil() {}
    private static final String CONFIG_FILE = "config.properties";
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigUtil.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new ConfigException("Configuration file not found: " + CONFIG_FILE + " in classpath: " + System.getProperty("java.class.path"));
            }
            properties.load(input);
        } catch (ConfigException | IOException e) {
            // Re-throw as unchecked exception to handle in static context
            throw new RuntimeException(e);
        }
    }

    public static String getConfig(String key) {
        return properties.getProperty(key);
    }
}

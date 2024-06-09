package utils;

import exceptions.ConfigException;

import java.io.*;
import java.util.Properties;

public class ConfigUtil {

    private ConfigUtil() {}

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigUtil.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                throw new ConfigException("Configuration file not found: " + CONFIG_FILE);
            }
            properties.load(input);
        } catch (IOException e) {
            throw new ConfigException("Failed to load configuration from file: " + CONFIG_FILE, e);
        }
    }

    public static String getConfig(String key) {
        return properties.getProperty(key);
    }

    public static void setConfig(String key, String value) {
        properties.setProperty(key, value);
        try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, null);
        } catch (IOException e) {
            throw new ConfigException("Failed to save configuration to file: " + CONFIG_FILE, e);
        }
    }
}

package utils;

import java.io.*;
import java.util.Properties;

public class PreferencesUtil {
    private static final String PREFERENCES_FILE = "user_preferences.properties";
    private static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    private static synchronized void loadProperties() {
        File file = new File(PREFERENCES_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (InputStream input = new FileInputStream(PREFERENCES_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static synchronized void saveProperties() {
        try (OutputStream output = new FileOutputStream(PREFERENCES_FILE)) {
            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void setPreference(String username, String key, String value) {
        properties.setProperty(username + "." + key, value);
        saveProperties();
    }

    public static synchronized String getPreference(String username, String key, String defaultValue) {
        return properties.getProperty(username + "." + key, defaultValue);
    }
}

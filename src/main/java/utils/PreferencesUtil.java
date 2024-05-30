package utils;

import java.io.*;
import java.util.Properties;

public class PreferencesUtil {
    private static final String PREFERENCES_FILE = "user_preferences.properties";

    private static void createFileIfNotExists() {
        File file = new File(PREFERENCES_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setPreference(String username, String key, String value) {
        createFileIfNotExists();
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(PREFERENCES_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        properties.setProperty(username + "." + key, value);

        try (OutputStream output = new FileOutputStream(PREFERENCES_FILE)) {
            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getPreference(String username, String key, String defaultValue) {
        createFileIfNotExists();
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(PREFERENCES_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return properties.getProperty(username + "." + key, defaultValue);
    }
}

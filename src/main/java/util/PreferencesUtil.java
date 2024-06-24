package util;

import java.io.*;
import java.util.Properties;

import static util.ResourcePaths.DEFAULT_LOG_DIRECTORY;
import static util.SceneConstants.*;

public class PreferencesUtil {

    private PreferencesUtil() {}
    private static final String PREFERENCES_FILE = "user_preferences.properties";
    private static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    private static synchronized void loadProperties() {
        File file = new File(PREFERENCES_FILE);
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    LoggerUtil.logDebug("New user preferences file created.");
                }
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

    public static synchronized void setPreference(String key, String value) {
        properties.setProperty(UserSession.getInstance().getUsername() + DOT + key, value);
        saveProperties();
    }

    public static synchronized String getPreference(String key, String defaultValue) {
        return properties.getProperty(UserSession.getInstance().getUsername() + DOT + key, defaultValue);
    }

    public static String getDefaultLogPath() {
        return new File(DEFAULT_LOG_DIRECTORY).getAbsolutePath();
    }

    public static synchronized void setDefaultPreferences(String username) {
        loadProperties();
        if (!properties.containsKey(username + DOT + GAME_MODE)) {
            setPreference(GAME_MODE, GameMode.NORMAL.getValue());
            setPreference(DIFFICULTY, Difficulty.EASY.getValue());
            setPreference(LOG_FILE_PATH, "");
            setPreference(TEXTURE, Texture.FOREST.getValue());
            setPreference(LANGUAGE, ENGLISH);
            setPreference(LEFT_KEY, LEFT_ARROW);
            setPreference(RIGHT_KEY, RIGHT_ARROW);
        }
    }

    public static synchronized void setTexture(Texture texture) {
        setPreference(TEXTURE, texture.getValue());
    }

    public static synchronized Texture getTexture() {
        String textureName = getPreference(TEXTURE, Texture.FOREST.getValue());
        return Texture.valueOf(textureName.toUpperCase());
    }

}

package util;

import model.user.User;

import java.io.*;
import java.util.Properties;

import static util.ResourcePaths.DEFAULT_LOG_DIRECTORY;
import static util.SceneConstants.*;
import static util.UserRole.ADMIN;
import static util.UserRole.USER;

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

    public static synchronized void setDefaultPreferences(User user) {
        loadProperties();
        if (user.getRole().equals(USER.value()) && !properties.containsKey(user.getUsername() + DOT + GAME_MODE)) {
            setPreference(GAME_MODE, GameMode.NORMAL.getValue());
            setPreference(DIFFICULTY, Difficulty.EASY.getValue());
            setPreference(LOG_FILE_PATH, "");
            setPreference(TEXTURE, Texture.FOREST.getValue());
            setPreference(LANGUAGE, ENGLISH);
            setPreference(LEFT_KEY, LEFT_ARROW);
            setPreference(RIGHT_KEY, RIGHT_ARROW);
        }
        if (user.getRole().equals(ADMIN.value()) && !properties.containsKey(ADMIN + DOT + GAME_MODE)) {
            setPreference(LANGUAGE, ENGLISH);
        }
        setLoginLanguagePreference();
    }

    public static synchronized void setLoginLanguagePreference() {
        String preference = getPreference(LANGUAGE, ENGLISH);
        properties.setProperty("null" + DOT + LANGUAGE, preference);
        saveProperties();
    }

    public static synchronized void setTexture(Texture texture) {
        setPreference(TEXTURE, texture.getValue());
    }

    public static synchronized Texture getTexture() {
        String textureName = getPreference(TEXTURE, Texture.FOREST.getValue());
        return Texture.valueOf(textureName.toUpperCase());
    }

}

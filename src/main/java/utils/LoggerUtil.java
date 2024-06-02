package utils;

import controller.SettingsController;
import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.File;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;
import java.util.prefs.Preferences;

public class LoggerUtil {
    private LoggerUtil() {
        // empty constructor
    }

    private static Logger logger = Logger.getLogger(LoggerUtil.class.getName());
    private static TextArea logTextArea;
    private static FileHandler fileHandler;

    static {
        configureLogger();
    }

    public static void setLogTextArea(TextArea textArea) {
        logTextArea = textArea;
    }

    public static void configureLogger() {
        if (fileHandler != null) {
            logger.removeHandler(fileHandler);
            fileHandler.close();
        }

        try {
            // Get current date and time for unique log file name
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String logDirectoryPath = getLogDirectory();
            String logFileName = logDirectoryPath + File.separator + "game_" + timeStamp + ".txt";

            // Check if the directory is writable
            if (!Files.isWritable(Paths.get(logDirectoryPath))) {
                logDirectoryPath = System.getProperty("user.home") + File.separator + "logs";
                Files.createDirectories(Paths.get(logDirectoryPath));
                logFileName = logDirectoryPath + File.separator + "game_" + timeStamp + ".txt";
            }

            // Configure the logger with handler and formatter
            fileHandler = new FileHandler(logFileName, true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            // Set the logger level to ALL to log all levels of messages
            logger.setLevel(Level.ALL);
        } catch (AccessDeniedException e) {
            logger.log(Level.SEVERE, "Access denied to log directory.", e.getMessage());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error occurred in FileHandler.", e);
        }
    }

    public static String getLogDirectory() {
        Preferences prefs = Preferences.userNodeForPackage(SettingsController.class);
        String logDirectoryPath = prefs.get("logFilePath", "log");
        File logDirectory = new File(logDirectoryPath);
        if (!logDirectory.exists()) {
            logDirectory.mkdir();
        }
        return logDirectoryPath;
    }

    public static void logInfo(String message) {
        log(message, Level.INFO);
    }

    public static void logWarning(String message) {
        log(message, Level.WARNING);
    }

    public static void logSevere(String message) {
        log(message, Level.SEVERE);
    }

    public static void logDebug(String message) {
        log(message, Level.FINE);
    }

    private static void log(String message, Level level) {
        logger.log(level, message);
        if (logTextArea != null) {
            Platform.runLater(() -> logTextArea.appendText(level.getLocalizedName() + ": " + message + "\n"));
        }
    }
}

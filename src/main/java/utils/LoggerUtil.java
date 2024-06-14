package utils;

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

public class LoggerUtil {
    private LoggerUtil() {
        // empty constructor
    }

    private static final Logger logger = Logger.getLogger(LoggerUtil.class.getName());
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
            File logDirectory = new File(logDirectoryPath);
            if (!logDirectory.exists() && !logDirectory.mkdirs()) {
                throw new AccessDeniedException("Cannot create log directory: " + logDirectoryPath);
            }

            if (!Files.isWritable(logDirectory.toPath())) {
                throw new AccessDeniedException("Log directory is not writable: " + logDirectoryPath);
            }

            // Configure the logger with handler and formatter
            fileHandler = new FileHandler(logFileName, true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);
            logger.addHandler(consoleHandler);

            // Set the logger level to ALL to log all levels of messages
            logger.setLevel(Level.ALL);
        } catch (AccessDeniedException e) {
            String fallbackLogDirectory = System.getProperty("user.home") + File.separator + "logs";
            configureFallbackLogger(fallbackLogDirectory);
            logSevere("Access denied to log directory: " + e.getMessage() + ". Using fallback directory: " + fallbackLogDirectory);
        } catch (IOException e) {
            logSevere("Error occurred in FileHandler: " + e.getMessage());
        }
    }

    private static void configureFallbackLogger(String fallbackLogDirectory) {
        try {
            // Get current date and time for unique log file name
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String logFileName = fallbackLogDirectory + File.separator + "game_" + timeStamp + ".txt";

            // Create fallback directory if it does not exist
            Files.createDirectories(Paths.get(fallbackLogDirectory));

            // Configure the logger with handler and formatter
            fileHandler = new FileHandler(logFileName, true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            // Set the logger level to ALL to log all levels of messages
            logger.setLevel(Level.ALL);

            // Set console handler level to ALL to log all levels of messages to the console
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.ALL);
            logger.addHandler(consoleHandler);

        } catch (IOException e) {
            logSevere("Error occurred in FileHandler with fallback directory: " + e.getMessage());
        }
    }

    public static String getLogDirectory() {
        String username = UserSession.getInstance().getUsername();
        return PreferencesUtil.getPreference(username, "logFilePath", "log");
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

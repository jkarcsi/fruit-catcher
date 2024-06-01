package utils;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class LoggerUtil {
    private LoggerUtil() {
        // empty constructor
    }
    private static Logger logger = Logger.getLogger(LoggerUtil.class.getName());
    private static TextArea logTextArea;

    static {
        try {
            // Define the log directory and create it if it does not exist
            String logDirectoryPath = "log";
            File logDirectory = new File(logDirectoryPath);
            if (!logDirectory.exists()) {
                logDirectory.mkdir();
            }

            // Get current date and time for unique log file name
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String logFileName = logDirectoryPath + File.separator + "game_" + timeStamp + ".txt";

            // Configure the logger with handler and formatter
            FileHandler fileHandler = new FileHandler(logFileName, true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            // Set the logger level to ALL to log all levels of messages
            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error occurred in FileHandler.", e);
        }
    }

    public static void setLogTextArea(TextArea textArea) {
        logTextArea = textArea;
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

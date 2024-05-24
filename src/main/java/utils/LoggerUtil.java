package utils;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerUtil {
    private static Logger logger = Logger.getLogger(LoggerUtil.class.getName());

    static {
        try {
            // Configure the logger with handler and formatter
            FileHandler fileHandler = new FileHandler("game.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);

            // Set the logger level to ALL to log all levels of messages
            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error occurred in FileHandler.", e);
        }
    }

    public static void logInfo(String message) {
        logger.info(message);
    }

    public static void logWarning(String message) {
        logger.warning(message);
    }

    public static void logSevere(String message) {
        logger.severe(message);
    }

    public static void logDebug(String message) {
        logger.fine(message);
    }
}

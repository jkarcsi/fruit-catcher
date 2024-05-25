package utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerUtil {

    private LoggerUtil(){
    }

    private static Logger logger = Logger.getLogger(LoggerUtil.class.getName());

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

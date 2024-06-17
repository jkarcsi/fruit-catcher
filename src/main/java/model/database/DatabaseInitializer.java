package model.database;

import utils.ConfigUtil;
import utils.LoggerUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private DatabaseInitializer(){}

    private static String DB_URL = ConfigUtil.getConfig("db.url");
    private static final String DB_USER = ConfigUtil.getConfig("db.user");
    private static final String DB_PASSWORD = ConfigUtil.getConfig("db.password");
    private static final String DB_NAME = null == DB_URL ? null : ConfigUtil.getConfig("db.url").substring(DB_URL.lastIndexOf('/') + 1);
    private static final String BASE_DB_URL = null == DB_URL ? null :  ConfigUtil.getConfig("db.url").substring(0, DB_URL.lastIndexOf('/'));

    public static void initializeDatabase() throws SQLException {
        try (Connection baseConnection = DriverManager.getConnection(BASE_DB_URL, DB_USER, DB_PASSWORD);
             Statement baseStatement = baseConnection.createStatement()) {

            // Create database if it doesn't exist
            baseStatement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
        } catch (SQLException e) {
            DB_URL = "jdbc:sqlite:fcg.db";
            initializeInnerDatabase();
        }

        // Connect to the newly created database
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {

            // Create users table if it doesn't exist
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                    "username VARCHAR(50) NOT NULL, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "password_reminder VARCHAR(255) DEFAULT NULL, " +
                    "role VARCHAR(10) DEFAULT 'user', " +
                    "status VARCHAR(10) DEFAULT 'active', " +
                    "PRIMARY KEY (username)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");

            // Create scores table if it doesn't exist
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS scores (" +
                    "id INT NOT NULL AUTO_INCREMENT, " +
                    "username VARCHAR(50) DEFAULT NULL, " +
                    "score INT DEFAULT NULL, " +
                    "timestamp TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "PRIMARY KEY (id), " +
                    "KEY fk_scores_users (username), " +
                    "CONSTRAINT fk_scores_users FOREIGN KEY (username) REFERENCES users (username) ON DELETE CASCADE" +
                    ") ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci");

        } catch (SQLException e) {
            DB_URL = "jdbc:sqlite:fcg.db";
            initializeInnerDatabase();
        }
    }

    public static void initializeInnerDatabase() throws SQLException {
        try (Connection connection = DriverManager.getConnection(DB_URL);
             Statement statement = connection.createStatement()) {

            // Create users table if it doesn't exist
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                    "username VARCHAR(50) NOT NULL, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "password_reminder VARCHAR(255) DEFAULT NULL, " +
                    "role VARCHAR(10) DEFAULT 'user', " +
                    "status VARCHAR(10) DEFAULT 'active', " +
                    "PRIMARY KEY (username)" +
                    ")");

            // Create scores table if it doesn't exist
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS scores (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username VARCHAR(50) DEFAULT NULL, " +
                    "score INTEGER, " +
                    "timestamp VARCHAR(50) NULL DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (username) REFERENCES users (username) ON DELETE CASCADE" +
                    ")");
        }
    }
}

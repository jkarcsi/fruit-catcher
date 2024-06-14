package model.database;

import utils.ConfigUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private DatabaseInitializer() {}

    private static final String DB_URL = ConfigUtil.getConfig("db.url");
    private static final String DB_USER = ConfigUtil.getConfig("db.user");
    private static final String DB_PASSWORD = ConfigUtil.getConfig("db.password");
    private static final String DB_NAME = ConfigUtil.getConfig("db.url").substring(DB_URL.lastIndexOf('/') + 1);
    private static final String BASE_DB_URL = ConfigUtil.getConfig("db.url").substring(0, DB_URL.lastIndexOf('/'));

    public static void initializeDatabase() throws SQLException {
        try (Connection baseConnection = DriverManager.getConnection(BASE_DB_URL, DB_USER, DB_PASSWORD);
             Statement baseStatement = baseConnection.createStatement()) {

            // Create database if it doesn't exist
            baseStatement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
        }

        // Connect to the newly created database
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement()) {

            // Use the new database
            statement.executeUpdate("USE " + DB_NAME);

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

            // Create admin and user accounts
            createUserIfNotExists(statement, "admin", "admin", "admin");
            createUserIfNotExists(statement, "user", "user", "user");
        }
    }

    private static void createUserIfNotExists(Statement statement, String username, String password, String role) throws SQLException {
        // Hash the password using SHA-256
        String hashedPassword = hashPassword(password);

        // Insert user if not exists
        statement.executeUpdate("INSERT IGNORE INTO users (username, password, role) VALUES ('" + username + "', '" + hashedPassword + "', '" + role + "')");
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}

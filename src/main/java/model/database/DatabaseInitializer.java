package model.database;

import utils.ConfigUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static final String CREATE_EXTERNAL_SCORES_TABLE = "CREATE TABLE IF NOT EXISTS scores (" +
            "id INT NOT NULL AUTO_INCREMENT, " +
            "username VARCHAR(50) DEFAULT NULL, " +
            "score INT DEFAULT NULL, " +
            "timestamp TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP, " +
            "PRIMARY KEY (id), " +
            "KEY fk_scores_users (username), " +
            "CONSTRAINT fk_scores_users FOREIGN KEY (username) REFERENCES users (username) ON DELETE CASCADE" +
            ") ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci";
    public static final String CREATE_EXTERNAL_USERS_TABLE = "CREATE TABLE IF NOT EXISTS users (" +
            "username VARCHAR(50) NOT NULL, " +
            "password VARCHAR(255) NOT NULL, " +
            "password_reminder VARCHAR(255) DEFAULT NULL, " +
            "role VARCHAR(10) DEFAULT 'user', " +
            "status VARCHAR(10) DEFAULT 'active', " +
            "PRIMARY KEY (username)" +
            ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci";
    public static final String CREATE_INTERNAL_USERS_TABLE = "CREATE TABLE IF NOT EXISTS users (" +
            "username VARCHAR(50) NOT NULL, " +
            "password VARCHAR(255) NOT NULL, " +
            "password_reminder VARCHAR(255) DEFAULT NULL, " +
            "role VARCHAR(10) DEFAULT 'user', " +
            "status VARCHAR(10) DEFAULT 'active', " +
            "PRIMARY KEY (username)" +
            ")";
    public static final String CREATE_INTERNAL_SCORES_TABLE = "CREATE TABLE IF NOT EXISTS scores (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "username VARCHAR(50) DEFAULT NULL, " +
            "score INTEGER, " +
            "timestamp VARCHAR(50) NULL DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (username) REFERENCES users (username) ON DELETE CASCADE" +
            ")";

    private DatabaseInitializer() {}

    private static String dbUrl;
    private static String dbUser;
    private static String dbPassword;
    private static String dbName;
    private static String baseDbUrl;

    public static void initializeDatabase() throws SQLException {
        dbUrl = ConfigUtil.getConfig("db.url");
        dbUser = ConfigUtil.getConfig("db.user");
        dbPassword = ConfigUtil.getConfig("db.password");

        if (dbUrl == null || dbUser == null || dbPassword == null) {
            dbUrl = "jdbc:sqlite:fcg.db";
            initializeSQLiteDatabase(dbUrl);
        } else {
            getAdditionalDbData(false);
            initializeMySQLDatabase(dbUrl);
        }
    }

    public static void initializeTestDatabase() throws SQLException {
        dbUrl = ConfigUtil.getTestConfig("test.db.url");
        dbUser = ConfigUtil.getTestConfig("test.db.user");
        dbPassword = ConfigUtil.getTestConfig("test.db.password");

        if (dbUrl == null || dbUser == null || dbPassword == null) {
            dbUrl = "jdbc:sqlite:fcg_test.db";
            initializeSQLiteDatabase(dbUrl);
        } else {
            getAdditionalDbData(true);
            initializeMySQLDatabase(dbUrl);
        }
    }

    private static void initializeMySQLDatabase(String dbUrl) throws SQLException {
        try (Connection baseConnection = DriverManager.getConnection(baseDbUrl, dbUser, dbPassword);
             Statement baseStatement = baseConnection.createStatement()) {

            baseStatement.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);
        }

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(CREATE_EXTERNAL_USERS_TABLE);

            statement.executeUpdate(CREATE_EXTERNAL_SCORES_TABLE);
        }
    }

    private static void initializeSQLiteDatabase(String dbUrl) throws SQLException {
        try (Connection connection = DriverManager.getConnection(dbUrl);
             Statement statement = connection.createStatement()) {

            statement.executeUpdate(CREATE_INTERNAL_USERS_TABLE);

            statement.executeUpdate(CREATE_INTERNAL_SCORES_TABLE);
        }
    }

    private static void getAdditionalDbData(boolean isTest) {
        dbName = isTest ?
                ConfigUtil.getTestConfig("test.db.url").substring(dbUrl.lastIndexOf('/') + 1)
                :
                ConfigUtil.getConfig("db.url").substring(dbUrl.lastIndexOf('/') + 1);
        baseDbUrl = isTest ? ConfigUtil.getTestConfig("test.db.url").substring(0, dbUrl.lastIndexOf('/'))
                :
                ConfigUtil.getConfig("db.url").substring(0, dbUrl.lastIndexOf('/'));
    }
}

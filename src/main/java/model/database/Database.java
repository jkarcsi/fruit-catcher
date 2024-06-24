package model.database;

import util.ConfigUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private Database() {}
    private static Connection connection;

    private static final String INNER_DB_URL = "jdbc:sqlite:fcg.db";
    private static final String TEST_DB_URL = "jdbc:sqlite:fcg_test.db";

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver"); // load MySQL JDBC driver
                String url = ConfigUtil.getConfig("db.url");
                String user = ConfigUtil.getConfig("db.user");
                String password = ConfigUtil.getConfig("db.password");
                if (url == null || url.isEmpty()) {
                    connection = getInternalConnection();
                } else {
                    connection = DriverManager.getConnection(url, user, password);
                }
            } catch (ClassNotFoundException | SQLException | NullPointerException e) {
                connection = getInternalConnection();
            }
        }
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Unable to establish an internal or external database connection.");
        }
        return connection;
    }

    public static Connection getTestConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver"); // load MySQL JDBC driver
                String url = ConfigUtil.getTestConfig("test.db.url");
                String user = ConfigUtil.getTestConfig("test.db.user");
                String password = ConfigUtil.getTestConfig("test.db.password");
                if (url == null || url.isEmpty()) {
                    connection = DriverManager.getConnection(TEST_DB_URL);
                } else {
                    connection = DriverManager.getConnection(url, user, password);
                }
            } catch (ClassNotFoundException | SQLException | NullPointerException e) {
                connection = DriverManager.getConnection(TEST_DB_URL);
            }
        }
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Unable to establish a test database connection.");
        }
        return connection;
    }

    private static Connection getInternalConnection() throws SQLException {
        return DriverManager.getConnection(INNER_DB_URL);
    }
}

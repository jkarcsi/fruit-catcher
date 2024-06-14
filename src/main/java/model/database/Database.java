package model.database;

import utils.ConfigUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private Database() {}
    private static Connection connection;

    private static final String INNER_DB_URL = "jdbc:sqlite:fcg.db";

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver"); // load MySQL JDBC driver
                String url = ConfigUtil.getConfig("db.url");
                String user = ConfigUtil.getConfig("db.user");
                String password = ConfigUtil.getConfig("db.password");
                connection = DriverManager.getConnection(url, user, password);
            } catch (ClassNotFoundException | SQLException | NullPointerException e) {
                connection = getInternalConnection();
            }
        }
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Unable to establish an internal or external database connection.");
        }
        return connection;
    }

    private static Connection getInternalConnection() throws SQLException {
        return DriverManager.getConnection(INNER_DB_URL);
    }
}

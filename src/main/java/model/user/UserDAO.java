package model.user;

import model.Score;
import model.database.Database;
import utils.LoggerUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public User getUser(String username) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String password = resultSet.getString("password");
                String passwordReminder = resultSet.getString("password_reminder");
                String role = resultSet.getString("role");
                String status = resultSet.getString("status");
                return new User(username, password, passwordReminder, role, status);
            }
        }
        return null;
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String passwordReminder = resultSet.getString("password_reminder");
                String role = resultSet.getString("role");
                String status = resultSet.getString("status");
                users.add(new User(username, password, passwordReminder, role, status));
            }
        }
        return users;
    }

    public void banUser(String username) throws SQLException {
        String query = "UPDATE users SET status = 'banned' WHERE username = ?";
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.executeUpdate();
        }
    }

    public void unbanUser(String username) throws SQLException {
        String query = "UPDATE users SET status = 'active' WHERE username = ?";
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.executeUpdate();
        }
    }

    public void saveUser(User user) throws SQLException {
        String query = "INSERT INTO users (username, password, password_reminder, role, status) VALUES (?, ?, ?, ?, ?)";
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getPasswordReminder());
            statement.setString(4, user.getRole());
            statement.setString(5, user.getStatus());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateUser(User user) throws SQLException {
        String query = "UPDATE users SET password = ?, password_reminder = ? WHERE username = ?";
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getPassword());
            statement.setString(2, user.getPasswordReminder());
            statement.setString(3, user.getUsername());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(String username) throws SQLException {
        String deleteUserQuery = "DELETE FROM users WHERE username = ?";
        String deleteResultsQuery = "DELETE FROM scores WHERE username = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement deleteUserStmt = connection.prepareStatement(deleteUserQuery);
             PreparedStatement deleteResultsStmt = connection.prepareStatement(deleteResultsQuery)) {

            // Begin transaction
            connection.setAutoCommit(false);

            // Delete user
            deleteUserStmt.setString(1, username);
            deleteUserStmt.executeUpdate();

            // Delete user results
            deleteResultsStmt.setString(1, username);
            deleteResultsStmt.executeUpdate();

            // Commit transaction
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void saveScore(String username, int score) throws SQLException {
        List<Score> userTopScores = getTopScores(username, 10);
        List<Score> overallTopScores = getTopScores(null, 10);
        if ((userTopScores.size() < 10 || score > userTopScores.get(userTopScores.size() - 1)
                .getScore()) || (overallTopScores.size() < 10 || score > overallTopScores.get(overallTopScores.size() - 1)
                .getScore())) {
            insertScore(username, score);
        }
    }

    private void insertScore(String username, int score) throws SQLException {
        LoggerUtil.logDebug(username);
        String query = "INSERT INTO scores (username, score) VALUES (?, ?)";
        LoggerUtil.logDebug(query);
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setInt(2, score);
            statement.executeUpdate();
        }
    }

    public List<Score> getTopScores(String username, int limit) throws SQLException {
        List<Score> scores = new ArrayList<>();
        String query = username != null
                ? "SELECT score, timestamp FROM scores WHERE username = ? ORDER BY score DESC LIMIT ?"
                : "SELECT username, score, timestamp FROM scores ORDER BY score DESC LIMIT ?";
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            if (username != null) {
                statement.setString(1, username);
                statement.setInt(2, limit);
            } else {
                statement.setInt(1, limit);
            }
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String user = username != null ? username : resultSet.getString("username");
                int score = resultSet.getInt("score");
                String timestamp = resultSet.getString("timestamp");
                scores.add(new Score(user, score, timestamp));
            }
        }
        return scores;
    }
}
package model.user;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.score.Score;
import model.database.Database;
import model.score.Ranking;
import util.LoggerUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    public User getUser(String username, boolean isTest) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";
        Connection connection = getConnection(isTest);
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

    public List<User> getAllUsers(boolean isTest) throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        Connection connection = getConnection(isTest);
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

    public void banUser(String username, boolean isTest) throws SQLException {
        String query = "UPDATE users SET status = 'banned' WHERE username = ?";
        Connection connection = getConnection(isTest);
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.executeUpdate();
        }
    }

    public void unbanUser(String username, boolean isTest) throws SQLException {
        String query = "UPDATE users SET status = 'active' WHERE username = ?";
        Connection connection = getConnection(isTest);
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.executeUpdate();
        }
    }

    public void saveUser(User user, boolean isTest) throws SQLException {
        String query = "INSERT INTO users (username, password, password_reminder, role, status) VALUES (?, ?, ?, ?, ?)";
        Connection connection = getConnection(isTest);
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

    public void updateUser(User user, boolean isTest) throws SQLException {
        String query = "UPDATE users SET password = ?, password_reminder = ? WHERE username = ?";
        Connection connection = getConnection(isTest);
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, user.getPassword());
            statement.setString(2, user.getPasswordReminder());
            statement.setString(3, user.getUsername());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteUser(String username, boolean isTest) throws SQLException {
        String deleteUserQuery = "DELETE FROM users WHERE username = ?";
        String deleteResultsQuery = "DELETE FROM scores WHERE username = ?";
        Connection connection = getConnection(isTest);
        try (PreparedStatement deleteUserStmt = connection.prepareStatement(deleteUserQuery);
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

    private static Connection getConnection(boolean isTest) throws SQLException {
        Connection connection;
        if (isTest) {
            connection = Database.getTestConnection();
        } else {
            connection = Database.getConnection();
        }
        return connection;
    }

    public void saveScore(String username, int score, boolean isTest) throws SQLException {
        List<Score> userTopScores = getTopScores(username, 10, isTest);
        List<Score> overallTopScores = getTopScores(null, 10, isTest);
        if ((userTopScores.size() < 10 || score > userTopScores.get(userTopScores.size() - 1)
                .getScore()) || (overallTopScores.size() < 10 || score > overallTopScores.get(overallTopScores.size() - 1)
                .getScore())) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timestamp = LocalDateTime.now().format(formatter);

            insertScore(username, score, timestamp, isTest);
        }
    }

    private void insertScore(String username, int score, String timestamp, boolean isTest) throws SQLException {
        LoggerUtil.logDebug(username);
        String query = "INSERT INTO scores (username, score, timestamp) VALUES (?, ?, ?)";
        LoggerUtil.logDebug(query);
        Connection connection = getConnection(isTest);
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setInt(2, score);
            statement.setString(3, timestamp);
            statement.executeUpdate();
        }
    }

    public List<Score> getTopScores(String username, int limit, boolean isTest) throws SQLException {
        List<Score> scores = new ArrayList<>();
        String query;

        if (username != null) {
            query = "SELECT s.score, s.timestamp FROM scores s JOIN users u ON s.username = u.username " +
                    "WHERE s.username = ? AND u.status != 'banned' ORDER BY s.score DESC LIMIT ?";
        } else {
            query = "SELECT s.username, s.score, s.timestamp FROM scores s JOIN users u ON s.username = u.username " +
                    "WHERE u.status != 'banned' ORDER BY s.score DESC LIMIT ?";
        }

        Connection connection = getConnection(isTest);
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

    public ObservableList<Ranking> getTopPlayers() throws SQLException {
        List<Ranking> rankingList = new ArrayList<>();
        String query = "SELECT s.username, SUM(s.score) AS total_score " +
                "FROM scores s JOIN users u ON s.username = u.username " +
                "WHERE u.status != 'banned' " +
                "GROUP BY s.username " +
                "ORDER BY total_score DESC " +
                "LIMIT 10";
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String username = resultSet.getString("username");
                int totalScore = resultSet.getInt("total_score");
                rankingList.add(new Ranking(username, totalScore));
            }
        }
        return FXCollections.observableArrayList(rankingList);
    }


}
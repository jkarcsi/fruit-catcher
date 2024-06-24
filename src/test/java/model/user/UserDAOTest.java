package model.user;

import model.score.Score;
import model.database.Database;
import model.database.DatabaseInitializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sqlite.SQLiteException;
import util.ConfigUtil;

import java.sql.Connection;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {
    private UserDAO userDAO;
    private Connection connection;

    @BeforeEach
    public void setUp() throws Exception {
        userDAO = new UserDAO();
        ConfigUtil.loadTestConfig();
        DatabaseInitializer.initializeTestDatabase();
        connection = Database.getTestConnection();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(DatabaseInitializer.CREATE_EXTERNAL_USERS_TABLE);
            statement.executeUpdate(DatabaseInitializer.CREATE_EXTERNAL_SCORES_TABLE);
        } catch (SQLSyntaxErrorException | SQLiteException e) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(DatabaseInitializer.CREATE_INTERNAL_USERS_TABLE);
                statement.executeUpdate(DatabaseInitializer.CREATE_INTERNAL_SCORES_TABLE);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @AfterEach
    public void tearDown() throws Exception {
        if (connection != null && !connection.isClosed()) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("DROP TABLE IF EXISTS scores");
                statement.executeUpdate("DROP TABLE IF EXISTS users");
            }
            connection.close();
        }
    }

    @Test
    void testSaveUser() throws Exception {
        User user = new User("testuser", "password", "reminder", "user", "active");
        userDAO.saveUser(user, true);

        User fetchedUser = userDAO.getUser("testuser", true);
        assertNotNull(fetchedUser);
        assertEquals("testuser", fetchedUser.getUsername());
        assertEquals("password", fetchedUser.getPassword());
        assertEquals("reminder", fetchedUser.getPasswordReminder());
        assertEquals("user", fetchedUser.getRole());
        assertEquals("active", fetchedUser.getStatus());
    }

    @Test
    void testBanUser() throws Exception {
        User user = new User("testuser", "password", "reminder", "user", "active");
        userDAO.saveUser(user, true);
        userDAO.banUser("testuser", true);

        User fetchedUser = userDAO.getUser("testuser", true);
        assertNotNull(fetchedUser);
        assertEquals("banned", fetchedUser.getStatus());
    }

    @Test
    void testUnbanUser() throws Exception {
        User user = new User("testuser", "password", "reminder", "user", "banned");
        userDAO.saveUser(user, true);
        userDAO.unbanUser("testuser", true);

        User fetchedUser = userDAO.getUser("testuser", true);
        assertNotNull(fetchedUser);
        assertEquals("active", fetchedUser.getStatus());
    }

    @Test
    void testSaveScore() throws Exception {
        User user = new User("testuser", "password", "reminder", "user", "active");
        userDAO.saveUser(user, true);

        userDAO.saveScore("testuser", 100, true);
        List<Score> scores = userDAO.getTopScores("testuser", 10, true);

        assertNotNull(scores);
        assertEquals(1, scores.size());
        assertEquals(100, scores.get(0).getScore());
    }

    @Test
    void testGetTopScoresExcludingBannedUsers() throws Exception {
        User user1 = new User("activeuser", "password", "reminder", "user", "active");
        User user2 = new User("banneduser", "password", "reminder", "user", "banned");
        userDAO.saveUser(user1, true);
        userDAO.saveUser(user2, true);

        userDAO.saveScore("activeuser", 200, true);
        userDAO.saveScore("banneduser", 300, true);

        List<Score> scores = userDAO.getTopScores(null, 10, true);
        assertNotNull(scores);
        assertEquals(1, scores.size());
        assertEquals("activeuser", scores.get(0).getUsername());
        assertEquals(200, scores.get(0).getScore());
    }

    @Test
    void testDeleteUser() throws Exception {
        User user = new User("testuser", "password", "reminder", "user", "active");
        userDAO.saveUser(user, true);
        userDAO.saveScore("testuser", 100, true);

        userDAO.deleteUser("testuser", true);

        User fetchedUser = userDAO.getUser("testuser", true);
        assertNull(fetchedUser);

        List<Score> scores = userDAO.getTopScores("testuser", 10, true);
        assertTrue(scores.isEmpty());
    }

    @Test
    void testGetAllUsers() throws Exception {
        User user1 = new User("user1", "password1", "reminder1", "user", "active");
        User user2 = new User("user2", "password2", "reminder2", "user", "active");
        userDAO.saveUser(user1, true);
        userDAO.saveUser(user2, true);

        List<User> users = userDAO.getAllUsers(true);

        assertNotNull(users);
        assertEquals(2, users.size());
    }

    @Test
    void testUpdateUser() throws Exception {
        User user = new User("testuser", "password", "reminder", "user", "active");
        userDAO.saveUser(user, true);

        user.setPassword("newpassword");
        user.setPasswordReminder("newreminder");
        userDAO.updateUser(user, true);

        User updatedUser = userDAO.getUser("testuser", true);

        assertNotNull(updatedUser);
        assertEquals("newpassword", updatedUser.getPassword());
        assertEquals("newreminder", updatedUser.getPasswordReminder());
    }
}

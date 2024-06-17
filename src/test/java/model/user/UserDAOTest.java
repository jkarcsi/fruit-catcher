package model.user;

import model.Score;
import model.database.Database;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {
    private UserDAO userDAO;
    private Connection connection;

    @BeforeEach
    public void setUp() throws Exception {
        userDAO = new UserDAO();
        connection = Database.getConnection();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS users (" +
                    "username TEXT NOT NULL, " +
                    "password TEXT NOT NULL, " +
                    "password_reminder TEXT, " +
                    "role TEXT DEFAULT 'user', " +
                    "status TEXT DEFAULT 'active', " +
                    "PRIMARY KEY (username)" +
                    ")");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS scores (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT, " +
                    "score INTEGER, " +
                    "timestamp TEXT DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (username) REFERENCES users (username) ON DELETE CASCADE" +
                    ")");
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
        userDAO.saveUser(user);

        User fetchedUser = userDAO.getUser("testuser");
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
        userDAO.saveUser(user);
        userDAO.banUser("testuser");

        User fetchedUser = userDAO.getUser("testuser");
        assertNotNull(fetchedUser);
        assertEquals("banned", fetchedUser.getStatus());
    }

    @Test
    void testUnbanUser() throws Exception {
        User user = new User("testuser", "password", "reminder", "user", "banned");
        userDAO.saveUser(user);
        userDAO.unbanUser("testuser");

        User fetchedUser = userDAO.getUser("testuser");
        assertNotNull(fetchedUser);
        assertEquals("active", fetchedUser.getStatus());
    }

    @Test
    void testSaveScore() throws Exception {
        User user = new User("testuser", "password", "reminder", "user", "active");
        userDAO.saveUser(user);

        userDAO.saveScore("testuser", 100);
        List<Score> scores = userDAO.getTopScores("testuser", 10);

        assertNotNull(scores);
        assertEquals(1, scores.size());
        assertEquals(100, scores.get(0).getScore());
    }

    @Test
    void testGetTopScoresExcludingBannedUsers() throws Exception {
        User user1 = new User("activeuser", "password", "reminder", "user", "active");
        User user2 = new User("banneduser", "password", "reminder", "user", "banned");
        userDAO.saveUser(user1);
        userDAO.saveUser(user2);

        userDAO.saveScore("activeuser", 200);
        userDAO.saveScore("banneduser", 300);

        List<Score> scores = userDAO.getTopScores(null, 10);
        assertNotNull(scores);
        assertEquals(1, scores.size());
        assertEquals("activeuser", scores.get(0).getUsername());
        assertEquals(200, scores.get(0).getScore());
    }

    @Test
    void testDeleteUser() throws Exception {
        User user = new User("testuser", "password", "reminder", "user", "active");
        userDAO.saveUser(user);
        userDAO.saveScore("testuser", 100);

        userDAO.deleteUser("testuser");

        User fetchedUser = userDAO.getUser("testuser");
        assertNull(fetchedUser);

        List<Score> scores = userDAO.getTopScores("testuser", 10);
        assertTrue(scores.isEmpty());
    }

    @Test
    void testGetAllUsers() throws Exception {
        User user1 = new User("user1", "password1", "reminder1", "user", "active");
        User user2 = new User("user2", "password2", "reminder2", "user", "active");
        userDAO.saveUser(user1);
        userDAO.saveUser(user2);

        List<User> users = userDAO.getAllUsers();

        assertNotNull(users);
        assertEquals(2, users.size());
    }

    @Test
    void testUpdateUser() throws Exception {
        User user = new User("testuser", "password", "reminder", "user", "active");
        userDAO.saveUser(user);

        user.setPassword("newpassword");
        user.setPasswordReminder("newreminder");
        userDAO.updateUser(user);

        User updatedUser = userDAO.getUser("testuser");

        assertNotNull(updatedUser);
        assertEquals("newpassword", updatedUser.getPassword());
        assertEquals("newreminder", updatedUser.getPasswordReminder());
    }
}

package controller;

import javafx.scene.canvas.Canvas;
import model.Basket;
import model.falling.Fruit;
import model.falling.Leaf;
import model.GameLevel;
import model.user.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GameControllerTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private Canvas gameCanvas; // Mocking the Canvas class

    @InjectMocks
    private GameController gameController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize necessary GameController attributes
        gameController.userDAO = userDAO;
        gameController.score = 0;
        gameController.doublePointsActive = false;
        gameController.basket = new Basket(300, 500, 50, 50, false);
        gameController.level = 0;
        gameController.fallingObjects = new ArrayList<>();

        // Mocking the game canvas
        gameController.gameCanvas = gameCanvas;

        // Setting up levels
        gameController.levels = new ArrayList<>();
        gameController.levels.add(new GameLevel(1.0, 1.0, 10.0, 10.0, 0.01, 0.005));
        gameController.levels.add(new GameLevel(1.5, 1.5, 8.0, 12.0, 0.02, 0.01));
    }

    @Test
    void testAddFruitToScore() {
        // Arrange
        Fruit fruit = new Fruit(300, 500, 1, 50, 50);
        fruit.setCaught(false);

        // Act
        gameController.manageColliding(fruit);

        // Assert
        assertEquals(10, gameController.getScore());
    }

    @Test
    void testAddLeafToScore() {
        // Arrange
        Leaf leaf = new Leaf(300, 500, 1, 50, 50);
        leaf.setCaught(false);

        // Act
        gameController.manageColliding(leaf);

        // Assert
        assertEquals(-5, gameController.getScore());
    }

    @Test
    void testActivateDoublePoints() {
        // Arrange
        gameController.doublePointsActive = false;

        // Act
        gameController.activateDoublePoints();

        // Assert
        assertEquals(true, gameController.doublePointsActive);
    }

    @Test
    void testLevelUp() {
        // Arrange
        gameController.level = 0;

        // Act
        gameController.levelUp();

        // Assert
        assertEquals(1, gameController.getLevel());
    }

    @Test
    void testSaveScore() throws SQLException {
        // Arrange
        gameController.setScore(100);
        when(gameController.getUsername()).thenReturn("testuser");

        // Act
        gameController.saveScore();

        // Assert
        verify(userDAO).saveScore("testuser", 100);
    }
}

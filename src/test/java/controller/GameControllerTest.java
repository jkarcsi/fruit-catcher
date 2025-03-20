package controller;

import exception.ConfigException;
import javafx.animation.Animation;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.MediaPlayer;
import model.game.Basket;
import model.game.BlackFruit;
import model.game.Fruit;
import model.game.Leaf;
import model.game.GameLevel;
import model.game.ScoreMultiplier;
import model.user.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import util.ConfigUtil;
import util.UserSession;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class GameControllerTest extends ApplicationTest {

    @Mock
    private UserDAO userDAO;

    @Mock
    private UserSession userSession;

    @InjectMocks
    private GameController gameController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ConfigUtil.loadTestConfig();
        when(userSession.getUsername()).thenReturn("testuser");

        // Initialize necessary GameController attributes
        gameController.userDAO = userDAO;
        gameController.score = 0;
        gameController.doublePointsActive = false;
        gameController.basket = new Basket(300, 500, 50, 50, false);
        gameController.level = 0;
        gameController.fallingObjects = new ArrayList<>();
        gameController.clouds = new ArrayList<>();

        // Mocking the game canvas
        gameController.gameCanvas = new Canvas(800, 600);

        // Initialize FXML elements
        gameController.scoreLabel = new Label();
        gameController.timerLabel = new Label();
        gameController.toggleBackgroundButton = new Button();
        gameController.toggleMusicButton = new Button();
        gameController.pauseButton = new Button();
        gameController.quitButton = new Button();
        gameController.logTextArea = new TextArea();

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
        runLater(() -> gameController.activateDoublePoints());

        // Assert
        assertTrue(gameController.doublePointsActive);
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

        // Act
        gameController.saveScore();

        // Assert
        verify(userDAO).saveScore(null, 100, false);
    }

    private void runLater(Runnable action) {
        try {
            Platform.runLater(action);
            // Wait for the runLater to finish
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new ConfigException("Error while using runLater.", e);
        }
    }

    @Test
    void testUpdateGame() {
        // Arrange
        Fruit fruit = new Fruit(300, 0, 1, 50, 50);
        Leaf leaf = new Leaf(300, 0, 1, 50, 50);
        fruit.setCaught(false);
        leaf.setCaught(false);
        gameController.fallingObjects.add(fruit);
        gameController.fallingObjects.add(leaf);
        gameController.basket = new Basket(300, 500, 50, 50, false);

        // Act
        runLater(() -> gameController.updateGame());

        // Assert
        assertEquals(2, gameController.fallingObjects.size());
        assertTrue(gameController.fallingObjects.contains(fruit));
        assertTrue(gameController.fallingObjects.contains(leaf));
    }

    @Test
    void testSpawnNewFallingObjects() {
        // Temporarily override the shouldSpawn method to always return true
        gameController = spy(gameController);
        doReturn(true).when(gameController).shouldSpawn(anyDouble());

        // Act
        Platform.runLater(() -> {
            // Ensure canvas dimensions are set correctly before spawning objects
            gameController.gameCanvas.setWidth(800);
            gameController.gameCanvas.setHeight(600);

            gameController.spawnNewFallingObjects();

            // Assert
            assertEquals(4, gameController.fallingObjects.size()); // Expecting each of every falling object
            assertTrue(gameController.fallingObjects.get(0) instanceof Fruit);
            assertTrue(gameController.fallingObjects.get(1) instanceof Leaf);
            assertTrue(gameController.fallingObjects.get(2) instanceof ScoreMultiplier);
            assertTrue(gameController.fallingObjects.get(3) instanceof BlackFruit);
        });

        // Wait for the runLater to finish
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testHandleKeyPress() {
        // Arrange
        gameController.basket = spy(new Basket(300, 500, 50, 50, false));
        gameController.loadControlKeys();
        KeyEvent leftKeyEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", gameController.leftKey, false, false, false, false);
        KeyEvent rightKeyEvent = new KeyEvent(KeyEvent.KEY_PRESSED, "", "", gameController.rightKey, false, false, false, false);

        // Act
        runLater(() -> {
            gameController.handleKeyPress(leftKeyEvent);
            gameController.handleKeyPress(rightKeyEvent);
        });

        // Assert
        verify(gameController.basket, times(1)).moveLeft();
        verify(gameController.basket, times(1)).moveRight();
    }

    @Test
    void testHandleKeyRelease() {
        // Arrange
        gameController.basket = spy(new Basket(300, 500, 50, 50, false));
        gameController.loadControlKeys();
        KeyEvent leftKeyEvent = new KeyEvent(KeyEvent.KEY_RELEASED, "", "", gameController.leftKey, false, false, false, false);
        KeyEvent rightKeyEvent = new KeyEvent(KeyEvent.KEY_RELEASED, "", "", gameController.rightKey, false, false, false, false);

        // Act
        runLater(() -> {
            gameController.handleKeyRelease(leftKeyEvent);
            gameController.handleKeyRelease(rightKeyEvent);
        });

        // Assert
        verify(gameController.basket, times(2)).stop();
    }

    @Test
    void testEndGame() {
        // Arrange
        gameController = spy(gameController);
        gameController.setScore(100);
        doNothing().when(gameController).showGameOverScreen(anyInt(), any(Canvas.class));

        // Act
        runLater(() -> {
            gameController.setupMusic(); // Ensure mediaPlayer is initialized
            gameController.startGame(); // Ensure the gameLoop is initialized

            // Wait a short time to ensure the gameLoop is running
            WaitForAsyncUtils.sleep(100, TimeUnit.MILLISECONDS);

            gameController.endGame();
        });

        // Wait for the endGame actions to complete
        WaitForAsyncUtils.waitForFxEvents();

        // Assert
        assertSame(Animation.Status.STOPPED, gameController.gameLoop.getStatus());
        verify(gameController, times(1)).showGameOverScreen(eq(100), any(Canvas.class));
    }

    @Test
    void testHandlePauseButton() {
        // Arrange
        gameController.setScore(100);
        gameController.gamePaused = false;

        // Act
        runLater(() -> {
            gameController.setupMusic(); // Ensure mediaPlayer is initialized
            gameController.startGame(); // Ensure the gameLoop is initialized
            gameController.handlePauseButton();

            WaitForAsyncUtils.sleep(100, TimeUnit.MILLISECONDS);
        });

        // Assert
        assertTrue(gameController.gamePaused);
        assertEquals(Animation.Status.PAUSED, gameController.gameLoop.getStatus());
    }

    @Test
    void testSetupMusic() {
        CountDownLatch latch = new CountDownLatch(1);

        // Act
        runLater(() -> {
            gameController.setupMusic();
            gameController.mediaPlayer.setOnReady(() -> {
                gameController.mediaPlayer.play();
                gameController.mediaPlayer.pause();
                latch.countDown();
            });
        });

        // Wait for the mediaPlayer to reach the PAUSED state
        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new ConfigException("Error while using countdown latch.", e);
        }

        // Retry mechanism to ensure the MediaPlayer reaches the PAUSED state
        boolean pausedStateReached = false;
        for (int i = 0; i < 10; i++) { // Retry 10 times
            if (gameController.mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                pausedStateReached = true;
                break;
            }
            try {
                Thread.sleep(200); // Wait 200 milliseconds before retrying
            } catch (InterruptedException e) {
                throw new ConfigException("Error while testing music.", e);
            }
        }

        // Assert
        assertNotNull(gameController.mediaPlayer);
        assertTrue(pausedStateReached, "MediaPlayer did not reach PAUSED state");
    }


}

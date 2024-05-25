package controller;

import javafx.animation.AnimationTimer;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Basket;
import model.FallingObject;
import model.Fruit;
import model.Leaf;
import model.UserDAO;
import utils.LoggerUtil;
import utils.PreferencesUtil;
import utils.UserSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.prefs.Preferences;

public class GameController {
    @FXML
    private Canvas gameCanvas;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label timerLabel;

    @FXML
    private Button toggleBackgroundButton;

    @FXML
    private Button toggleMusicButton;

    private GraphicsContext gc;
    private AnimationTimer timer;
    private int score;
    private List<FallingObject> fallingObjects;
    private Basket basket;
    private boolean gamePaused;
    private int timeRemaining;
    private Timer countdownTimer;
    private String username = UserSession.getInstance().getUsername();
    private boolean isMusicPlaying = false;
    private ImageView backgroundImageView;
    private TranslateTransition backgroundAnimation;
    private MediaPlayer mediaPlayer;
    private String leftKey;
    private String rightKey;

    @FXML
    public void initialize() {
        gc = gameCanvas.getGraphicsContext2D();
        score = 0;
        timeRemaining = 60;
        fallingObjects = new ArrayList<>();
        basket = new Basket(300, 350, 50, 50);
        gamePaused = false;

        setupBackground();
        setupMusic();

        Platform.runLater(() -> {
            leftKey = PreferencesUtil.getPreference(username, "leftKey", "LEFT");
            rightKey = PreferencesUtil.getPreference(username, "rightKey", "RIGHT");
            startGame();
            gameCanvas.getScene().setOnKeyPressed(this::handleKeyPress);
            gameCanvas.requestFocus();
            startCountdown();
        });
    }

    private void setupBackground() {
        // Setup the background image and animation
        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                "/fruitcatchgame/image/bckgr.gif")));
        backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setFitWidth(gameCanvas.getWidth());
        backgroundImageView.setFitHeight(gameCanvas.getHeight());

        // Add the background image to the scene
        ((Pane) gameCanvas.getParent()).getChildren().add(0, backgroundImageView);

        // Setup animation
        backgroundAnimation = new TranslateTransition(Duration.seconds(3), backgroundImageView);
        backgroundAnimation.setByX(-gameCanvas.getWidth());
        backgroundAnimation.setCycleCount(TranslateTransition.INDEFINITE);
        backgroundAnimation.setAutoReverse(true);
        backgroundAnimation.play();
    }

    private void setupMusic() {
        String musicFile = Objects.requireNonNull(getClass().getResource("/fruitcatchgame/sound/dezert.mp3")).toExternalForm();
        Media media = new Media(musicFile);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    }

    @FXML
    private void handleToggleBackgroundButton() {
        Pane parentPane = (Pane) gameCanvas.getParent();
        if (parentPane.getChildren().contains(backgroundImageView)) {
            parentPane.getChildren().remove(backgroundImageView);
            toggleBackgroundButton.setText("Enable Background");
            LoggerUtil.logInfo("Background disabled");
        } else {
            parentPane.getChildren().add(0, backgroundImageView);
            backgroundAnimation.play(); // Restart the animation when background is re-enabled
            toggleBackgroundButton.setText("Disable Background");
            LoggerUtil.logInfo("Background enabled");
        }
        gameCanvas.requestFocus(); // Ensure the gameCanvas is focused after clicking the button
    }

    @FXML
    private void handleToggleMusicButton() {
        if (isMusicPlaying) {
            mediaPlayer.pause();
            isMusicPlaying = false;
            toggleMusicButton.setText("Play Music");
        } else {
            mediaPlayer.play();
            isMusicPlaying = true;
            toggleMusicButton.setText("Pause Music");
        }
        gameCanvas.requestFocus();
    }

    private void startGame() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!gamePaused) {
                    updateGame();
                    renderGame();
                }
            }
        };
        timer.start();
    }

    private void updateGame() {
        for (FallingObject obj : fallingObjects) {
            obj.update();
            if (obj.collidesWith(basket)) {
                if (obj instanceof Fruit) {
                    score += 10;
                } else if (obj instanceof Leaf) {
                    score -= 5;
                }
                obj.setCaught(true);
            }
        }

        fallingObjects.removeIf(FallingObject::isCaught);
        spawnNewFallingObjects();
        scoreLabel.setText("Score: " + score);
    }

    private void renderGame() {
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        basket.render(gc);
        for (FallingObject obj : fallingObjects) {
            obj.render(gc);
        }
    }

    private void handleKeyPress(KeyEvent event) {
        switch (event.getCode().toString()) {
            case "LEFT":
                basket.moveLeft();
                break;
            case "RIGHT":
                basket.moveRight();
                break;
            default:
                if (event.getCode().toString().equals(leftKey)) {
                    basket.moveLeft();
                } else if (event.getCode().toString().equals(rightKey)) {
                    basket.moveRight();
                }
                break;
        }
    }

    private void spawnNewFallingObjects() {
        Random random = new Random();
        if (random.nextInt(100) < 5) { // 5% chance to spawn a new object
            if (random.nextBoolean()) {
                fallingObjects.add(new Fruit(random.nextInt((int) gameCanvas.getWidth()), 0));
            } else {
                fallingObjects.add(new Leaf(random.nextInt((int) gameCanvas.getWidth()), 0));
            }
        }
    }

    private void startCountdown() {
        countdownTimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    timeRemaining--;
                    timerLabel.setText("Time: " + timeRemaining);
                    if (timeRemaining <= 0) {
                        endGame();
                    }
                });
            }
        };
        countdownTimer.scheduleAtFixedRate(task, 1000, 1000);
    }

    @FXML
    private void handlePauseButton() {
        gamePaused = !gamePaused;
        if (gamePaused) {
            timer.stop();
            countdownTimer.cancel();
            mediaPlayer.pause();
            LoggerUtil.logInfo("Game paused");
        } else {
            startCountdown();
            timer.start();
            mediaPlayer.play();
            LoggerUtil.logInfo("Game resumed");
        }
        gameCanvas.requestFocus();
    }

    private void endGame() {
        timer.stop();
        countdownTimer.cancel();
        mediaPlayer.pause();
        saveScore();
        showGameOverScreen();
    }

    private void saveScore() {
        try {
            UserDAO userDAO = new UserDAO();
            userDAO.saveScore(username, score);
            LoggerUtil.logInfo("Score saved for user: " + username + ", score: " + score);
        } catch (SQLException e) {
            LoggerUtil.logSevere("Error saving score for user: " + username);
            e.printStackTrace();
        }
    }

    private void showGameOverScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fruitcatchgame/view/gameOver.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            GameOverController controller = loader.getController();
            controller.setUsername(username);
            controller.setScore(score);
            Stage stage = (Stage) gameCanvas.getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            LoggerUtil.logInfo("Game over screen displayed");
        } catch (IOException e) {
            LoggerUtil.logSevere("Error loading game over screen");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleQuitButton() {
        timer.stop();
        countdownTimer.cancel();
        mediaPlayer.pause();
        navigateToMainMenu();
    }

    private void navigateToMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fruitcatchgame/view/mainMenu.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) gameCanvas.getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            LoggerUtil.logInfo("Navigated back to main menu");
        } catch (IOException e) {
            LoggerUtil.logSevere("Error navigating back to main menu");
            e.printStackTrace();
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

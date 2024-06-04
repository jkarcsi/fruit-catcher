package controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.*;
import model.falling.BlackFruit;
import model.falling.BonusTime;
import model.falling.FallingObject;
import model.falling.Fruit;
import model.falling.Leaf;
import model.falling.ScoreMultiplier;
import model.user.UserDAO;
import utils.LoggerUtil;
import utils.PreferencesUtil;
import utils.UserSession;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;

import static utils.FXMLPaths.GAME_OVER;
import static utils.FXMLPaths.MAIN_MENU;

public class GameController extends BaseController implements Initializable {
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

    @FXML
    private Button pauseButton;

    @FXML
    private Button quitButton;

    @FXML
    private TextArea logTextArea;

    private GraphicsContext gc;
    private Timeline gameLoop;
    private int score;
    private List<FallingObject> fallingObjects;
    private Basket basket;
    private boolean gamePaused;
    private int timeRemaining;
    private Timer countdownTimer;
    private boolean isMusicPlaying = false;
    private ImageView backgroundImageView;
    private MediaPlayer mediaPlayer;
    private KeyCode leftKey;
    private KeyCode rightKey;
    private int level;
    private List<GameLevel> levels;
    private boolean doublePointsActive;
    private Timeline doublePointsTimer;
    private boolean isFreeplayMode;
    private ResourceBundle bundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String language = PreferencesUtil.getPreference(UserSession.getInstance().getUsername(), "language", "en");
        Locale locale = new Locale(language);
        bundle = ResourceBundle.getBundle("messages", locale);
        updateTexts();

        gc = gameCanvas.getGraphicsContext2D();
        score = 0;
        timeRemaining = 60;
        level = 0;
        fallingObjects = new ArrayList<>();
        basket = new Basket(300, 500, 50, 50);
        gamePaused = false;
        doublePointsActive = false;

        String gameMode = PreferencesUtil.getPreference(getUsername(), "gameMode", "normal");
        isFreeplayMode = "Freeplay".equals(gameMode);
        if (isFreeplayMode) {
            timerLabel.setVisible(false);
        }

        setupInitialLevels();
        setupBackground();
        setupMusic();
        setupLogTextArea();

        Platform.runLater(() -> {
            adjustCanvasSize();
            loadControlKeys();
            startGame();
            gameCanvas.getScene().setOnKeyPressed(this::handleKeyPress);
            gameCanvas.getScene().setOnKeyReleased(this::handleKeyRelease);
            gameCanvas.requestFocus();
            if (!isFreeplayMode) {
                startCountdown();
            }
        });
    }

    private void updateTexts() {
        scoreLabel.setText(bundle.getString("score"));
        timerLabel.setText(bundle.getString("timer"));
        toggleBackgroundButton.setText(bundle.getString("background"));
        toggleMusicButton.setText(bundle.getString("music"));
        pauseButton.setText(bundle.getString("pause"));
        quitButton.setText(bundle.getString("quit"));
    }

    private void handleKeyRelease(KeyEvent event) {
        if (event.getCode() == leftKey || event.getCode() == rightKey) {
            basket.stop();
        }
    }

    private void loadControlKeys() {
        leftKey = KeyCode.valueOf(PreferencesUtil.getPreference(getUsername(), "leftKey", "LEFT"));
        rightKey = KeyCode.valueOf(PreferencesUtil.getPreference(getUsername(), "rightKey", "RIGHT"));
    }

    private void adjustCanvasSize() {
        Stage stage = (Stage) gameCanvas.getScene().getWindow();
        gameCanvas.setWidth(stage.getWidth());
        gameCanvas.setHeight(stage.getHeight());
        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            gameCanvas.setWidth(newVal.doubleValue());
            backgroundImageView.setFitWidth(newVal.doubleValue());
        });
        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            gameCanvas.setHeight(newVal.doubleValue());
            backgroundImageView.setFitHeight(newVal.doubleValue());
        });
    }

    private void setupLogTextArea() {
        LoggerUtil.setLogTextArea(logTextArea);
        logTextArea.setStyle("-fx-background-color: transparent; -fx-control-inner-background: transparent; -fx-text-fill: #2e8b57; -fx-border-color: transparent;");
    }

    private void setupInitialLevels() {
        levels = new ArrayList<>();
        double fruitSpeed = 2.0;
        double leafSpeed = 1.5;
        double fruitSize = 30;
        double leafSize = 30;
        double fruitSpawnRate = 0.01;
        double leafSpawnRate = 0.005;

        for (int i = 0; i < 15; i++) { // Pre-define levels up to 15
            levels.add(new GameLevel(fruitSpeed, leafSpeed, fruitSize, leafSize, fruitSpawnRate, leafSpawnRate));
            fruitSpeed += 0.25;
            leafSpeed += 0.25;
            fruitSize = Math.max(fruitSize - 1, 1); // Ensure fruit size doesn't go below 1
            leafSize += 1;
            fruitSpawnRate += 0.005;
            leafSpawnRate += 0.005;
        }
    }

    private void addLevel() {
        double fruitSpeed = levels.get(levels.size() - 1).getFruitSpeed() + 0.25;
        double leafSpeed = levels.get(levels.size() - 1).getLeafSpeed() + 0.25;
        double fruitSize = Math.max(levels.get(levels.size() - 1).getFruitSize() - 1, 1);
        double leafSize = levels.get(levels.size() - 1).getLeafSize() + 1;
        double fruitSpawnRate = levels.get(levels.size() - 1).getFruitSpawnRate() + 0.005;
        double leafSpawnRate = levels.get(levels.size() - 1).getLeafSpawnRate() + 0.005;
        levels.add(new GameLevel(fruitSpeed, leafSpeed, fruitSize, leafSize, fruitSpawnRate, leafSpawnRate));
    }

    private void setupBackground() {
        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(
                "/fruitcatchgame/image/bckgr.gif")));
        backgroundImageView = new ImageView(backgroundImage);
        backgroundImageView.setFitWidth(gameCanvas.getWidth());
        backgroundImageView.setFitHeight(gameCanvas.getHeight());

        ((Pane) gameCanvas.getParent()).getChildren().add(0, backgroundImageView);
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
            toggleBackgroundButton.setText(bundle.getString("enableBackground"));
            LoggerUtil.logInfo("Background disabled");
        } else {
            parentPane.getChildren().add(0, backgroundImageView);
            adjustBackgroundSize();
            toggleBackgroundButton.setText(bundle.getString("disableBackground"));
            LoggerUtil.logInfo("Background enabled");
        }
        gameCanvas.requestFocus();
    }

    @FXML
    private void handleToggleMusicButton() {
        if (isMusicPlaying) {
            mediaPlayer.pause();
            isMusicPlaying = false;
            toggleMusicButton.setText(bundle.getString("playMusic"));
        } else {
            mediaPlayer.play();
            isMusicPlaying = true;
            toggleMusicButton.setText(bundle.getString("pauseMusic"));
        }
        gameCanvas.requestFocus();
    }

    private void startGame() {
        gameLoop = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            if (!gamePaused) {
                updateGame();
                renderGame();
            }
        }));
        gameLoop.setCycleCount(Animation.INDEFINITE);
        gameLoop.play();
    }

    private void updateGame() {
        basket.update(gameCanvas.getWidth(), gameCanvas.getHeight());

        for (FallingObject obj : fallingObjects) {
            obj.update();
            if (obj.collidesWith(basket)) {
                if (obj instanceof Fruit) {
                    score += doublePointsActive ? 20 : 10;
                } else if (obj instanceof Leaf) {
                    score -= 5;
                } else if (obj instanceof ScoreMultiplier) {
                    activateDoublePoints();
                } else if (obj instanceof BonusTime) {
                    timeRemaining += 10;
                } else if (obj instanceof BlackFruit) {
                    endGame();
                }
                obj.setCaught(true);
            }
        }

        fallingObjects.removeIf(FallingObject::isCaught);
        spawnNewFallingObjects();
        scoreLabel.setText("Score: " + score);

        if (score > (level + 1) * 150) {
            levelUp();
        }
    }

    private void renderGame() {
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        basket.render(gc);
        for (FallingObject obj : fallingObjects) {
            obj.render(gc);
        }
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == leftKey) {
            basket.moveLeft();
        } else if (event.getCode() == rightKey) {
            basket.moveRight();
        }
    }

    private void spawnNewFallingObjects() {
        Random random = new Random();
        GameLevel currentLevel = levels.get(level);
        if (level == levels.size() - 2) {
            addLevel();
        }

        if (random.nextDouble() < currentLevel.getFruitSpawnRate()) {
            fallingObjects.add(new Fruit(random.nextInt((int) gameCanvas.getWidth()), 0, currentLevel.getFruitSpeed(), currentLevel.getFruitSize(), currentLevel.getFruitSize()));
        }

        if (random.nextDouble() < currentLevel.getLeafSpawnRate()) {
            fallingObjects.add(new Leaf(random.nextInt((int) gameCanvas.getWidth()), 0, currentLevel.getLeafSpeed(), currentLevel.getLeafSize(), currentLevel.getLeafSize()));
        }

        if (random.nextDouble() < 0.001) { // Spawn a ScoreMultiplier with a 0.1% chance
            fallingObjects.add(new ScoreMultiplier(random.nextInt((int) gameCanvas.getWidth()), 0, currentLevel.getFruitSpeed(), currentLevel.getFruitSize(), currentLevel.getFruitSize()));
        }

        if (random.nextDouble() < 0.001 && !isFreeplayMode) { // Spawn a BonusTime, in normal mode, with a 0.1% chance
            fallingObjects.add(new BonusTime(random.nextInt((int) gameCanvas.getWidth()), 0, currentLevel.getFruitSpeed(), currentLevel.getFruitSize(), currentLevel.getFruitSize()));
        }

        if (random.nextDouble() < 0.002) { // Spawn a BlackFruit with a 0.2% chance
            fallingObjects.add(new BlackFruit(random.nextInt((int) gameCanvas.getWidth()), 0, currentLevel.getFruitSpeed(), currentLevel.getFruitSize(), currentLevel.getFruitSize()));
        }
    }

    private void activateDoublePoints() {
        doublePointsActive = true;
        LoggerUtil.logInfo("Double points activated!");

        if (doublePointsTimer != null) {
            doublePointsTimer.stop();
        }

        doublePointsTimer = new Timeline(new KeyFrame(Duration.seconds(10), e -> {
            doublePointsActive = false;
            LoggerUtil.logInfo("Double points deactivated.");
        }));
        doublePointsTimer.play();
    }

    private void levelUp() {
        if (level < levels.size() - 1) {
            level++;
            LoggerUtil.logInfo("Level up! New level: " + (level + 1));
        }
    }

    private void startCountdown() {
        countdownTimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    timeRemaining--;
                    timerLabel.setText(bundle.getString("timer") + ": " + timeRemaining);
                    if (timeRemaining <= 0) {
                        endGame();
                    }
                });
            }
        };
        countdownTimer.scheduleAtFixedRate(task, 1000, 1000);
    }

    private void adjustBackgroundSize() {
        backgroundImageView.setFitWidth(gameCanvas.getWidth());
        backgroundImageView.setFitHeight(gameCanvas.getHeight());
    }

    @FXML
    private void handlePauseButton() {
        gamePaused = !gamePaused;
        if (gamePaused) {
            gameLoop.pause();
            if (!isFreeplayMode) {
                countdownTimer.cancel();
            }
            mediaPlayer.pause();
            LoggerUtil.logInfo("Game paused");
        } else {
            if (!isFreeplayMode) {
                startCountdown();
            }
            gameLoop.play();
            if (isMusicPlaying) {
                mediaPlayer.play();
            }
            LoggerUtil.logInfo("Game resumed");
        }
        gameCanvas.requestFocus();
    }

    private void endGame() {
        gameLoop.stop();
        if (!isFreeplayMode) {
            countdownTimer.cancel();
        }
        mediaPlayer.pause();
        if (!isFreeplayMode) {
            saveScore();
        }
        showGameOverScreen();
    }

    private void saveScore() {
        try {
            UserDAO userDAO = new UserDAO();
            userDAO.saveScore(getUsername(), score);
            LoggerUtil.logInfo("Score saved for user: " + getUsername() + ", score: " + score);
        } catch (SQLException e) {
            LoggerUtil.logSevere("Error saving score for user: " + getUsername());
            e.printStackTrace();
        }
    }

    private void showGameOverScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(GAME_OVER));
            Scene scene = new Scene(loader.load(), 800, 600);
            GameOverController controller = loader.getController();
            controller.setScore(score);
            Stage stage = (Stage) gameCanvas.getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleQuitButton() {
        Platform.exit();
        System.exit(0);
    }
}

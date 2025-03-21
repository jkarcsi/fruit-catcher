package controller;

import exception.ConfigException;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.game.Basket;
import model.game.BlackFruit;
import model.game.BonusTime;
import model.game.FallingObject;
import model.game.Fruit;
import model.game.GameLevel;
import model.game.Leaf;
import model.game.MovingCloud;
import model.game.ScoreMultiplier;
import model.user.UserDAO;
import util.Difficulty;
import util.GameMode;
import util.LoggerUtil;
import util.PreferencesUtil;

import java.net.URL;
import java.sql.SQLException;
import java.util.*;

import static util.ResourcePaths.IMAGE_CLOUD_1_PNG;
import static util.ResourcePaths.IMAGE_CLOUD_2_PNG;
import static util.ResourcePaths.IMAGE_CLOUD_3_PNG;
import static util.ResourcePaths.SOUND_DEZERT_MP_3;
import static util.SceneConstants.BACKGROUND;
import static util.SceneConstants.DIFFICULTY;
import static util.SceneConstants.DISABLE_BACKGROUND;
import static util.SceneConstants.ENABLE_BACKGROUND;
import static util.SceneConstants.GAME_MODE;
import static util.SceneConstants.LEFT;
import static util.SceneConstants.LEFT_ARROW;
import static util.SceneConstants.LEFT_KEY;
import static util.SceneConstants.MUSIC;
import static util.SceneConstants.PAUSE;
import static util.SceneConstants.PAUSE_MUSIC;
import static util.SceneConstants.PLAY_MUSIC;
import static util.SceneConstants.QUIT;
import static util.SceneConstants.RIGHT;
import static util.SceneConstants.RIGHT_ARROW;
import static util.SceneConstants.RIGHT_KEY;
import static util.SceneConstants.SCORE;
import static util.SceneConstants.TIMER;
import static util.FXMLPaths.MAIN_MENU;

public class GameController extends BaseController implements Initializable {

    @FXML
    Canvas gameCanvas;

    @FXML
    Label scoreLabel;

    @FXML
    Label timerLabel;

    @FXML
    Button toggleBackgroundButton;

    @FXML
    Button toggleMusicButton;

    @FXML
    Button pauseButton;

    @FXML
    Button quitButton;

    @FXML
    TextArea logTextArea;

    public static final Random RANDOM = new Random();
    public UserDAO userDAO;
    private GraphicsContext gc;
    Timeline gameLoop;
    int score;
    List<FallingObject> fallingObjects;
    Basket basket;
    boolean gamePaused;
    private int timeRemaining;
    private Timer countdownTimer;
    private boolean isMusicPlaying = false;
    MediaPlayer mediaPlayer;
    KeyCode leftKey;
    KeyCode rightKey;
    int level;
    List<GameLevel> levels;
    boolean doublePointsActive;
    private Timeline doublePointsTimer;
    private boolean isNormalMode;
    private boolean isPlaygroundMode;
    List<MovingCloud> clouds;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateTexts();

        gc = gameCanvas.getGraphicsContext2D();
        score = 0;
        timeRemaining = 60;
        fallingObjects = new ArrayList<>();
        basket = new Basket(300, 500, 50, 50);
        userDAO = new UserDAO();
        gamePaused = false;
        doublePointsActive = false;
        clouds = new ArrayList<>();

        initialSetup();

        Platform.runLater(() -> {
            adjustCanvasSize();
            loadControlKeys();
            startGame();
            gameCanvas.getScene().setOnKeyPressed(this::handleKeyPress);
            gameCanvas.getScene().setOnKeyReleased(this::handleKeyRelease);
            gameCanvas.requestFocus();
            if (isNormalMode) {
                startCountdown();
            }
        });
    }

    private void initialSetup() {
        setupGameMode();
        setupInitialLevels();
        setupDifficulty();
        setupBackground();
        setupMusic();
        setupLogTextArea();
    }

    private void setupGameMode() {
        String gameMode = PreferencesUtil.getPreference(GAME_MODE, GameMode.NORMAL.getValue());
        isNormalMode = GameMode.NORMAL.getValue().equalsIgnoreCase(gameMode);
        isPlaygroundMode = GameMode.PLAYGROUND.getValue().equalsIgnoreCase(gameMode);
        if (!isNormalMode) {
            timerLabel.setVisible(false);
        }
    }

    private void setupDifficulty() {
        String difficulty = PreferencesUtil.getPreference(DIFFICULTY, Difficulty.EASY.getValue().toLowerCase());
        switch (difficulty) {
            case "Medium" -> level = 5;
            case "Hard" -> level = 10;
            default -> level = 0;
        }
    }

    private void updateTexts() {
        setMultilingualElement(scoreLabel, SCORE);
        setMultilingualElement(timerLabel, TIMER);
        setMultilingualElement(toggleBackgroundButton, BACKGROUND);
        setMultilingualElement(toggleMusicButton, MUSIC);
        setMultilingualElement(pauseButton, PAUSE);
        setMultilingualElement(quitButton, QUIT);
    }

    void handleKeyRelease(KeyEvent event) {
        if (event.getCode() == leftKey || event.getCode() == rightKey) {
            basket.stop();
        }
    }

    void loadControlKeys() {
        String leftKeyPref = PreferencesUtil.getPreference(LEFT_KEY, LEFT_ARROW);
        leftKey = KeyCode.valueOf(leftKeyPref.equals(LEFT_ARROW) ? LEFT : leftKeyPref);

        String rightKeyPref = PreferencesUtil.getPreference(RIGHT_KEY, RIGHT_ARROW);
        rightKey = KeyCode.valueOf(rightKeyPref.equals(RIGHT_ARROW) ? RIGHT : rightKeyPref);
    }

    private void adjustCanvasSize() {
        Stage stage = (Stage) gameCanvas.getScene().getWindow();
        gameCanvas.setWidth(stage.getWidth() - 20);
        gameCanvas.setHeight(stage.getHeight());
        stage.widthProperty().addListener((obs, oldVal, newVal) -> gameCanvas.setWidth(newVal.doubleValue()));
        stage.heightProperty().addListener((obs, oldVal, newVal) -> gameCanvas.setHeight(newVal.doubleValue()));
    }

    private void setupLogTextArea() {
        LoggerUtil.setLogTextArea(logTextArea);
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
            fruitSize = Math.max(fruitSize - 1, 4); // Ensure fruit size doesn't go below a visible barrier
            leafSize += 1;
            fruitSpawnRate += 0.005;
            leafSpawnRate += 0.005;
        }
    }

    private void addLevel() {
        double fruitSpeed = levels.get(levels.size() - 1).getFruitSpeed() + 0.25;
        double leafSpeed = levels.get(levels.size() - 1).getLeafSpeed() + 0.25;
        double fruitSize = Math.max(levels.get(levels.size() - 1).getFruitSize() - 1, 4);
        double leafSize = levels.get(levels.size() - 1).getLeafSize() + 1;
        double fruitSpawnRate = levels.get(levels.size() - 1).getFruitSpawnRate() + 0.005;
        double leafSpawnRate = levels.get(levels.size() - 1).getLeafSpawnRate() + 0.005;
        levels.add(new GameLevel(fruitSpeed, leafSpeed, fruitSize, leafSize, fruitSpawnRate, leafSpawnRate));
    }

    private void setupBackground() {
        Image cloud1 = loadImage(IMAGE_CLOUD_1_PNG);
        Image cloud2 = loadImage(IMAGE_CLOUD_2_PNG);
        Image cloud3 = loadImage(IMAGE_CLOUD_3_PNG);

        double scaleFactor = 0.1;
        for (int i = 0; i < 20; i++) {
            Image selectedCloud = switch (RANDOM.nextInt(3)) {
                case 0 -> cloud1;
                case 1 -> cloud2;
                default -> cloud3;
            };
            double y = RANDOM.nextDouble() * gameCanvas.getHeight() * 0.6;
            double speed = 0.5 + RANDOM.nextDouble() * 0.5;
            boolean moveRight = RANDOM.nextBoolean();
            clouds.add(new MovingCloud(RANDOM.nextDouble() * gameCanvas.getWidth(),
                    y,
                    speed,
                    moveRight,
                    selectedCloud,
                    scaleFactor));
        }
    }

    void setupMusic() {
        String musicFile = Objects.requireNonNull(getClass().getResource(SOUND_DEZERT_MP_3)).toExternalForm();
        Media media = new Media(musicFile);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    }

    @FXML
    private void handleToggleBackgroundButton() {
        if (clouds.isEmpty()) {
            setupBackground();
            setMultilingualElement(toggleBackgroundButton, DISABLE_BACKGROUND);
            LoggerUtil.logInfo("Background enabled");
        } else {
            clouds.clear();
            setMultilingualElement(toggleBackgroundButton, ENABLE_BACKGROUND);
            LoggerUtil.logInfo("Background disabled");
        }
        gameCanvas.requestFocus();
    }

    @FXML
    private void handleToggleMusicButton() {
        if (isMusicPlaying) {
            mediaPlayer.pause();
            isMusicPlaying = false;
            setMultilingualElement(toggleMusicButton, PLAY_MUSIC);
        } else {
            mediaPlayer.play();
            isMusicPlaying = true;
            setMultilingualElement(toggleMusicButton, PAUSE_MUSIC);
        }
        gameCanvas.requestFocus();
    }

    void startGame() {
        gameLoop = new Timeline(new KeyFrame(Duration.millis(16), e -> {
            if (!gamePaused) {
                updateGame();
                renderGame();
            }
        }));
        gameLoop.setCycleCount(Animation.INDEFINITE);
        gameLoop.play();
    }

    void updateGame() {
        basket.update(gameCanvas.getWidth(), gameCanvas.getHeight());

        for (FallingObject obj : fallingObjects) {
            obj.update();
            manageColliding(obj);

            if (obj.getY() > basket.getY() + basket.getHeight()) {
                obj.setCaught(true);
            }
        }

        fallingObjects.removeIf(FallingObject::isCaught);
        spawnNewFallingObjects();
        score = Math.max(score, 0);
        setMultilingualElement(scoreLabel, SCORE, ": " + score);

        if (!isPlaygroundMode && (score > (level + 1) * 150)) {
            levelUp();
        }

        for (MovingCloud cloud : clouds) {
            cloud.update(gameCanvas.getWidth());
        }
    }

    void manageColliding(FallingObject obj) {
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


    private void renderGame() {
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        for (MovingCloud cloud : clouds) {
            cloud.render(gc);
        }

        basket.render(gc);
        for (FallingObject obj : fallingObjects) {
            obj.render(gc);
        }
    }

    void handleKeyPress(KeyEvent event) {
        if (event.getCode() == leftKey) {
            basket.moveLeft();
        } else if (event.getCode() == rightKey) {
            basket.moveRight();
        }
    }

    void spawnNewFallingObjects() {
        GameLevel currentLevel = levels.get(level);
        if (level == levels.size() - 2) {
            addLevel();
        }

        double canvasWidth = gameCanvas.getWidth() - 20;
        if (canvasWidth <= 0) {
            canvasWidth = 1; // Ensure that the bound for nextInt is positive
        }

        if (shouldSpawn(currentLevel.getFruitSpawnRate())) {
            fallingObjects.add(new Fruit(RANDOM.nextInt((int) canvasWidth),
                    0,
                    currentLevel.getFruitSpeed(),
                    currentLevel.getFruitSize(),
                    currentLevel.getFruitSize()));
        }

        if (shouldSpawn(currentLevel.getLeafSpawnRate())) {
            fallingObjects.add(new Leaf(RANDOM.nextInt((int) canvasWidth),
                    0,
                    currentLevel.getLeafSpeed(),
                    currentLevel.getLeafSize(),
                    currentLevel.getLeafSize()));
        }

        if (shouldSpawn(0.001)) { // Spawn a ScoreMultiplier with a 0.1% chance
            fallingObjects.add(new ScoreMultiplier(RANDOM.nextInt((int) canvasWidth),
                    0,
                    currentLevel.getFruitSpeed(),
                    currentLevel.getFruitSize(),
                    currentLevel.getFruitSize()));
        }

        if (shouldSpawn(0.001) && isNormalMode) { // Spawn a BonusTime, in normal mode, with a 0.1% chance
            fallingObjects.add(new BonusTime(RANDOM.nextInt((int) canvasWidth),
                    0,
                    currentLevel.getFruitSpeed(),
                    currentLevel.getFruitSize(),
                    currentLevel.getFruitSize()));
        }

        if (shouldSpawn(0.002)) { // Spawn a BlackFruit with a 0.2% chance
            fallingObjects.add(new BlackFruit(RANDOM.nextInt((int) canvasWidth),
                    0,
                    currentLevel.getFruitSpeed(),
                    currentLevel.getFruitSize(),
                    currentLevel.getFruitSize()));
        }
    }


    boolean shouldSpawn(double rate) {
        return RANDOM.nextDouble() < rate;
    }


    void activateDoublePoints() {
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

    void levelUp() {
        if (!isPlaygroundMode && level < levels.size() - 1) {
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
                    setMultilingualElement(timerLabel, TIMER, ": " + timeRemaining);
                    if (timeRemaining <= 0) {
                        endGame();
                    }
                });
            }
        };
        countdownTimer.scheduleAtFixedRate(task, 1000, 1000);
    }

    @FXML
    void handlePauseButton() {
        gamePaused = !gamePaused;
        if (gamePaused) {
            gameLoop.pause();
            if (isNormalMode) {
                countdownTimer.cancel();
            }
            mediaPlayer.pause();
            LoggerUtil.logInfo("Game paused");
        } else {
            if (isNormalMode) {
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

    void endGame() {
        gameLoop.stop();
        if (isNormalMode) {
            countdownTimer.cancel();
        }
        mediaPlayer.pause();
        if (isNormalMode && score > 0) {
            saveScore();
        }
        showGameOverScreen(score, gameCanvas);
    }

    void saveScore() {
        try {
            userDAO.saveScore(getUsername(), score, false);
            LoggerUtil.logInfo("Score saved for user: " + getUsername() + ", score: " + score);
        } catch (SQLException e) {
            LoggerUtil.logSevere("Error saving score for user: " + getUsername());
            throw new ConfigException("Error while saving user score.", e);
        }
    }

    @FXML
    private void handleQuitButton() {
        gameLoop.stop();
        if (isNormalMode) {
            countdownTimer.cancel();
        }
        mediaPlayer.pause();
        navigateTo(MAIN_MENU, gameCanvas);
    }

    public int getScore() {
        return this.score;
    }

    public void setScore(int i) {
        this.score = i;
    }

    public int getLevel() {
        return this.level;
    }

}

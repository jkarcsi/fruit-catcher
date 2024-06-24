package controller;

import exception.HashException;
import exception.ResourceNotFoundException;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Labeled;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import model.user.User;
import util.LoggerUtil;
import util.PreferencesUtil;
import util.Texture;
import util.UserRole;
import util.UserSession;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.ResourceBundle;

import static util.FXMLPaths.ADMIN;
import static util.FXMLPaths.GAME_OVER;
import static util.FXMLPaths.MAIN_MENU;
import static util.ResourcePaths.BASE_STYLE_FOLDER;
import static util.ResourcePaths.IMAGE_ICON_PNG;
import static util.SceneConstants.ENGLISH;
import static util.SceneConstants.LANGUAGE;
import static util.SceneConstants.MESSAGES;

public abstract class BaseController implements Initializable {

    private final String username = UserSession.getInstance().getUsername();

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        applyUserStylesheet();
    }

    protected void applyUserStylesheet() {
        Texture texture = PreferencesUtil.getTexture();
        LoggerUtil.logDebug("Applying texture: " + texture.getValue());
        for (Window window : Window.getWindows()) {
            if (window instanceof Stage stage) {
                Scene scene = stage.getScene();
                // Skip the StartController stage
                if (scene != null && scene.getRoot().getUserData() != null && scene.getRoot().getUserData().equals("StartPage")) {
                    LoggerUtil.logDebug("Skipping StartPage scene");
                    continue;
                }
                if (scene != null) {
                    LoggerUtil.logDebug("Applying stylesheet to scene: " + scene);
                    applyUserStylesheet(scene, texture);
                }
            }
        }
    }

    private void applyUserStylesheet(Scene scene, Texture texture) {
        LoggerUtil.logDebug("Clearing current stylesheets");
        scene.getStylesheets().clear();
        URL cssFile = getClass().getResource(BASE_STYLE_FOLDER + texture.getCssFile());
        if (cssFile != null) {
            LoggerUtil.logDebug("Found CSS file: " + cssFile.toExternalForm());
            scene.getStylesheets().add(cssFile.toExternalForm());
            LoggerUtil.logDebug("Applied stylesheet: " + cssFile.toExternalForm());
            forceSceneUpdate(scene);
        } else {
            LoggerUtil.logSevere("CSS file not found: " + texture.getCssFile());
        }
    }

    private void forceSceneUpdate(Scene scene) {
        // This forces a re-render of the scene, which can sometimes help with style changes.
        scene.getRoot().applyCss();
        scene.getRoot().layout();
    }

    protected String getUsername() {
        return username;
    }

    protected void navigateTo(String fxmlFile, Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.getIcons().add(loadImage(IMAGE_ICON_PNG));
            stage.setScene(scene);
            stage.setResizable(false);
            LoggerUtil.logDebug("Navigated to: " + fxmlFile);
            applyUserStylesheet(scene, PreferencesUtil.getTexture()); // Apply styles on navigation
        } catch (IOException e) {
            LoggerUtil.logSevere("Failed to navigate to: " + fxmlFile);
            e.printStackTrace();
        }
    }

    protected void navigateTo(String fxmlFile, Canvas gameCanvas) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) gameCanvas.getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            LoggerUtil.logDebug("Navigated to: " + fxmlFile);
            applyUserStylesheet(scene, PreferencesUtil.getTexture()); // Apply styles on navigation
        } catch (IOException e) {
            LoggerUtil.logSevere("Failed to navigate to: " + fxmlFile);
            e.printStackTrace();
        }
    }

    protected FXMLLoader navigateToDialog(String fxmlFile, ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Stage dialogStage = new Stage();
        dialogStage.getIcons().add(loadImage(IMAGE_ICON_PNG));
        dialogStage.setScene(new Scene(loader.load()));
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(((javafx.scene.Node) event.getSource()).getScene().getWindow());
        dialogStage.setResizable(false);
        applyUserStylesheet(dialogStage.getScene(), PreferencesUtil.getTexture()); // Apply styles on navigation
        dialogStage.showAndWait();
        return loader;
    }

    protected void navigateByRole(User user, TextField usernameField) throws IOException {
        FXMLLoader loader;
        if (user.getRole().equals(UserRole.ADMIN.value())) {
            loader = new FXMLLoader(getClass().getResource(ADMIN));
        } else {
            loader = new FXMLLoader(getClass().getResource(MAIN_MENU));
        }
        Scene scene = new Scene(loader.load(), 800, 600);

        Stage stage = (Stage) usernameField.getScene().getWindow();
        stage.setScene(scene);
        stage.setResizable(false);
        applyUserStylesheet(scene, PreferencesUtil.getTexture()); // Apply styles on navigation
        LoggerUtil.logInfo("User logged in: " + user.getUsername());
    }

    protected void showGameOverScreen(int score, Canvas gameCanvas) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(GAME_OVER));
            Scene scene = new Scene(loader.load(), 800, 600);
            GameOverController controller = loader.getController();
            controller.setScore(score);
            Stage stage = (Stage) gameCanvas.getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            applyUserStylesheet(scene, PreferencesUtil.getTexture()); // Apply styles on navigation
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String hashPassword(String password) throws HashException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            LoggerUtil.logSevere("Error hashing password");
            throw new HashException(e.getMessage());
        }
    }

    protected Image loadImage(String path) {
        InputStream inputStream = getClass().getResourceAsStream(path);
        if (inputStream == null) {
            throw new ResourceNotFoundException("Resource not found: " + path + " in classpath: " + System.getProperty(
                    "java.class.path"));
        }
        return new Image(inputStream);
    }

    protected void setMultilingualElement(Labeled element, String text) {
        element.setText(getBundle().getString(text));
    }

    protected void setMultilingualElement(TableColumn<?, ?> column, String text) {
        String string = getBundle().getString(text);
        column.setText(string);
    }

    protected void setMultilingualPromptElement(PasswordField element, String text) {
        element.setPromptText(getBundle().getString(text));
    }

    protected void setMultilingualElement(Labeled element, String text, String additional) {
        element.setText(getBundle().getString(text) + additional);
    }

    protected void setMultilingualElement(DirectoryChooser directoryChooser, String text) {
        directoryChooser.setTitle(getBundle().getString(text));
    }

    ResourceBundle getBundle() {
            String language = PreferencesUtil.getPreference(LANGUAGE, ENGLISH);
            String languageCode = getLanguageCode(language);
            Locale locale = new Locale(languageCode);
            return ResourceBundle.getBundle(MESSAGES, locale);
    }

    private String getLanguageCode(String language) {
        return switch (language.toLowerCase()) {
            case "hungarian", "magyar" -> "hu";
            default -> "en";
        };
    }
}

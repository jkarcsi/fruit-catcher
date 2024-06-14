package controller;

import exceptions.HashException;
import exceptions.ResourceNotFoundException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Labeled;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import utils.LoggerUtil;
import utils.PreferencesUtil;
import utils.Texture;
import utils.UserSession;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

import static utils.SceneConstants.ENGLISH;
import static utils.SceneConstants.LANGUAGE;
import static utils.SceneConstants.MESSAGES;

public abstract class BaseController implements Initializable {

    private final String username = UserSession.getInstance().getUsername();

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        applyUserStylesheet();
    }

    protected void applyUserStylesheet() {
        Texture texture = PreferencesUtil.getTexture(getUsername());
        LoggerUtil.logDebug("Applying texture: " + texture.getTextureName());
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
        URL cssFile = getClass().getResource("/view/" + texture.getCssFile());
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
        LoggerUtil.logInfo("FORCE");
        LoggerUtil.logInfo(scene.getStylesheets().toString());
        scene.getRoot().setVisible(false);
        scene.getRoot().setVisible(true);
    }

    protected String getUsername() {
        return username;
    }

    protected void navigateTo(String fxmlFile, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.getIcons().add(loadImage("/image/icon.png"));
            stage.setScene(scene);
            stage.setResizable(false);
            LoggerUtil.logDebug("Navigated to: " + fxmlFile);
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
        } catch (IOException e) {
            LoggerUtil.logSevere("Failed to navigate to: " + fxmlFile);
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
            throw new ResourceNotFoundException("Resource not found: " + path + " in classpath: " + System.getProperty("java.class.path"));
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

    private ResourceBundle getBundle() {
        String language = PreferencesUtil.getPreference(getUsername(), LANGUAGE, ENGLISH);
        Locale locale = new Locale(language);
        return ResourceBundle.getBundle(MESSAGES, locale);
    }
}

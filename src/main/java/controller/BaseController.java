package controller;

import exceptions.HashException;
import exceptions.ResourceNotFoundException;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Labeled;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import utils.LoggerUtil;
import utils.PreferencesUtil;
import utils.UserSession;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.ResourceBundle;

import static utils.SceneConstants.ENGLISH;
import static utils.SceneConstants.LANGUAGE;
import static utils.SceneConstants.MESSAGES;

public abstract class BaseController {

    private final String username = UserSession.getInstance().getUsername();

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
        } catch (IOException e) {
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
            throw new ResourceNotFoundException("Resource not found: " + path + " in classpath: " + System.getProperty("java.class.path"));
        }
        return new Image(inputStream);
    }

    protected void setMultilingualElement(Labeled element, String text) {
        element.setText(getBundle().getString(text));
    }

    protected void setMultilingualElement(TableColumn<?, ?> column, String text) {
        String string = getBundle().getString(text);
        LoggerUtil.logDebug(string);
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

    protected String setMultilingualElement(String text) {

        String string = getBundle().getString(text);
        LoggerUtil.logDebug(string);
        return string;
    }

    private ResourceBundle getBundle() {
        String language = PreferencesUtil.getPreference(username, LANGUAGE, ENGLISH);
        Locale locale = new Locale(language);
        return ResourceBundle.getBundle(MESSAGES, locale);
    }
}

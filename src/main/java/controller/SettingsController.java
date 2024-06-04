package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import utils.PreferencesUtil;

import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import static utils.FXMLPaths.MAIN_MENU;

public class SettingsController extends BaseController implements Initializable {

    @FXML
    private ComboBox<String> gameModeComboBox;

    @FXML
    private ComboBox<String> difficultyComboBox;

    @FXML
    private ComboBox<String> textureComboBox;

    @FXML
    private TextField logFilePathTextField;

    @FXML
    private ComboBox<String> languageComboBox;

    @FXML
    private ComboBox<String> leftKeyComboBox;

    @FXML
    private ComboBox<String> rightKeyComboBox;

    @FXML
    private Label gameModeLabel;

    @FXML
    private Label difficultyLabel;

    @FXML
    private Label textureLabel;

    @FXML
    private Label logFilePathLabel;

    @FXML
    private Label languageLabel;

    @FXML
    private Label leftKeyLabel;

    @FXML
    private Label rightKeyLabel;

    @FXML
    private Button saveButton;

    @FXML
    private Button backToMainMenuButton;

    @FXML
    private Button chooseDirectoryButton;

    private ResourceBundle bundle;
    private Preferences prefs;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        prefs = Preferences.userNodeForPackage(SettingsController.class);

        String language = PreferencesUtil.getPreference(getUsername(), "language", "English");
        Locale locale = new Locale(language);
        bundle = ResourceBundle.getBundle("messages", locale);

        gameModeComboBox.getItems().addAll("Normal", "Freeplay");
        leftKeyComboBox.getItems().addAll("LEFT", "A", "J");
        rightKeyComboBox.getItems().addAll("RIGHT", "D", "L");
        languageComboBox.getItems().addAll("English", "Magyar");
        difficultyComboBox.getItems().addAll("Easy", "Medium", "Hard");
        textureComboBox.getItems().addAll("Classic", "Retro", "Futuristic");

        loadSettings();
        updateTexts();
    }

    private void loadSettings() {
        gameModeComboBox.setValue(PreferencesUtil.getPreference(getUsername(), "gameMode", "Normal"));
        difficultyComboBox.setValue(PreferencesUtil.getPreference(getUsername(), "difficulty", "Easy"));
        textureComboBox.setValue(PreferencesUtil.getPreference(getUsername(), "texture", "Classic"));
        logFilePathTextField.setText(PreferencesUtil.getPreference(getUsername(), "logFilePath", ""));
        languageComboBox.setValue(PreferencesUtil.getPreference(getUsername(), "language", "English"));
        leftKeyComboBox.setValue(PreferencesUtil.getPreference(getUsername(), "leftKey", "LEFT"));
        rightKeyComboBox.setValue(PreferencesUtil.getPreference(getUsername(), "rightKey", "RIGHT"));
    }

    private void updateTexts() {
        gameModeLabel.setText(bundle.getString("gameMode"));
        difficultyLabel.setText(bundle.getString("difficulty"));
        textureLabel.setText(bundle.getString("texture"));
        logFilePathLabel.setText(bundle.getString("logFilePath"));
        languageLabel.setText(bundle.getString("language"));
        leftKeyLabel.setText(bundle.getString("leftKey"));
        rightKeyLabel.setText(bundle.getString("rightKey"));
        saveButton.setText(bundle.getString("save"));
        backToMainMenuButton.setText(bundle.getString("backToMainMenu"));
        chooseDirectoryButton.setText(bundle.getString("chooseDirectory"));
    }

    @FXML
    private void handleSaveButton(ActionEvent event) {
        PreferencesUtil.setPreference(getUsername(), "gameMode", gameModeComboBox.getValue());
        PreferencesUtil.setPreference(getUsername(), "difficulty", difficultyComboBox.getValue());
        PreferencesUtil.setPreference(getUsername(), "texture", textureComboBox.getValue());
        PreferencesUtil.setPreference(getUsername(), "logFilePath", logFilePathTextField.getText());
        PreferencesUtil.setPreference(getUsername(), "language", languageComboBox.getValue());
        PreferencesUtil.setPreference(getUsername(), "leftKey", leftKeyComboBox.getValue());
        PreferencesUtil.setPreference(getUsername(), "rightKey", rightKeyComboBox.getValue());
        handleBackToMainMenuButton(event);
    }

    @FXML
    private void handleChooseLogDirectoryButton(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(bundle.getString("chooseDirectory"));
        File selectedDirectory = directoryChooser.showDialog(logFilePathTextField.getScene().getWindow());
        if (selectedDirectory != null) {
            logFilePathTextField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    @FXML
    private void handleBackToMainMenuButton(ActionEvent event) {
        navigateTo(MAIN_MENU, event);
    }
}

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

import static utils.SceneConstants.BACK_TO_MAIN_MENU;
import static utils.SceneConstants.CHOOSE_DIRECTORY;
import static utils.SceneConstants.DIFFICULTY;
import static utils.SceneConstants.GAME_MODE;
import static utils.SceneConstants.LANGUAGE;
import static utils.SceneConstants.LEFT_KEY;
import static utils.SceneConstants.LOG_FILE_PATH;
import static utils.SceneConstants.RIGHT_KEY;
import static utils.SceneConstants.SAVE;
import static utils.SceneConstants.TEXTURE;
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

        String language = PreferencesUtil.getPreference(getUsername(), LANGUAGE, "English");
        Locale locale = new Locale(language);
        bundle = ResourceBundle.getBundle("messages", locale);

        gameModeComboBox.getItems().addAll("Normal", "Freeplay");
        leftKeyComboBox.getItems().addAll("<", "A", "J");
        rightKeyComboBox.getItems().addAll(">", "D", "L");
        languageComboBox.getItems().addAll("English", "Magyar");
        difficultyComboBox.getItems().addAll("Easy", "Medium", "Hard");
        textureComboBox.getItems().addAll("Classic", "Retro", "Futuristic");

        loadSettings();
        updateTexts();
    }

    private void loadSettings() {
        gameModeComboBox.setValue(PreferencesUtil.getPreference(getUsername(), GAME_MODE, "Normal"));
        difficultyComboBox.setValue(PreferencesUtil.getPreference(getUsername(), DIFFICULTY, "Easy"));
        textureComboBox.setValue(PreferencesUtil.getPreference(getUsername(), TEXTURE, "Classic"));
        logFilePathTextField.setText(PreferencesUtil.getPreference(getUsername(), LOG_FILE_PATH, ""));
        languageComboBox.setValue(PreferencesUtil.getPreference(getUsername(), LANGUAGE, "English"));
        leftKeyComboBox.setValue(PreferencesUtil.getPreference(getUsername(), LEFT_KEY, "<"));
        rightKeyComboBox.setValue(PreferencesUtil.getPreference(getUsername(), RIGHT_KEY, ">"));
    }

    private void updateTexts() {
        gameModeLabel.setText(bundle.getString(GAME_MODE));
        difficultyLabel.setText(bundle.getString(DIFFICULTY));
        textureLabel.setText(bundle.getString(TEXTURE));
        logFilePathLabel.setText(bundle.getString(LOG_FILE_PATH));
        languageLabel.setText(bundle.getString(LANGUAGE));
        leftKeyLabel.setText(bundle.getString(LEFT_KEY));
        rightKeyLabel.setText(bundle.getString(RIGHT_KEY));
        saveButton.setText(bundle.getString(SAVE));
        backToMainMenuButton.setText(bundle.getString(BACK_TO_MAIN_MENU));
        chooseDirectoryButton.setText(bundle.getString(CHOOSE_DIRECTORY));
    }

    @FXML
    private void handleSaveButton(ActionEvent event) {
        PreferencesUtil.setPreference(getUsername(), GAME_MODE, gameModeComboBox.getValue());
        PreferencesUtil.setPreference(getUsername(), DIFFICULTY, difficultyComboBox.getValue());
        PreferencesUtil.setPreference(getUsername(), TEXTURE, textureComboBox.getValue());
        PreferencesUtil.setPreference(getUsername(), LOG_FILE_PATH, logFilePathTextField.getText());
        PreferencesUtil.setPreference(getUsername(), LANGUAGE, languageComboBox.getValue());
        PreferencesUtil.setPreference(getUsername(), LEFT_KEY, leftKeyComboBox.getValue());
        PreferencesUtil.setPreference(getUsername(), RIGHT_KEY, rightKeyComboBox.getValue());
        handleBackToMainMenuButton(event);
    }

    @FXML
    private void handleChooseLogDirectoryButton(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(bundle.getString(CHOOSE_DIRECTORY));
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

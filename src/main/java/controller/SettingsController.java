package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import utils.LoggerUtil;
import utils.PreferencesUtil;
import utils.Texture;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import static utils.FXMLPaths.MAIN_MENU;
import static utils.SceneConstants.*;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        gameModeComboBox.getItems().addAll("Normal", "Freeplay");
        leftKeyComboBox.getItems().addAll("<", "A", "J");
        rightKeyComboBox.getItems().addAll(">", "D", "L");
        languageComboBox.getItems().addAll("English", "Magyar");
        difficultyComboBox.getItems().addAll("Easy", "Medium", "Hard");
        textureComboBox.getItems().addAll("Forest", "Retro", "Futuristic");

        loadSettings();
        updateTexts();
    }

    private void loadSettings() {
        gameModeComboBox.setValue(PreferencesUtil.getPreference(getUsername(), GAME_MODE, "Normal"));
        difficultyComboBox.setValue(PreferencesUtil.getPreference(getUsername(), DIFFICULTY, "Easy"));
        textureComboBox.setValue(PreferencesUtil.getTexture(getUsername()).getTextureName());
        logFilePathTextField.setText(PreferencesUtil.getPreference(getUsername(), LOG_FILE_PATH, ""));
        languageComboBox.setValue(PreferencesUtil.getPreference(getUsername(), LANGUAGE, ENGLISH));
        leftKeyComboBox.setValue(PreferencesUtil.getPreference(getUsername(), LEFT_KEY, "<"));
        rightKeyComboBox.setValue(PreferencesUtil.getPreference(getUsername(), RIGHT_KEY, ">"));
    }

    private void updateTexts() {
        setMultilingualElement(gameModeLabel, GAME_MODE);
        setMultilingualElement(difficultyLabel, DIFFICULTY);
        setMultilingualElement(textureLabel, TEXTURE);
        setMultilingualElement(logFilePathLabel, LOG_FILE_PATH);
        setMultilingualElement(languageLabel, LANGUAGE);
        setMultilingualElement(leftKeyLabel, LEFT_KEY);
        setMultilingualElement(rightKeyLabel, RIGHT_KEY);
        setMultilingualElement(saveButton, SAVE);
        setMultilingualElement(backToMainMenuButton, BACK_TO_MAIN_MENU);
        setMultilingualElement(chooseDirectoryButton, CHOOSE_DIRECTORY);
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

        applyTexture();

        handleBackToMainMenuButton(event);
    }

    private void applyTexture() {
        String selectedTexture = textureComboBox.getValue();
        Texture texture = Texture.valueOf(selectedTexture.toUpperCase());

        PreferencesUtil.setTexture(getUsername(), texture);
        applyUserStylesheet();
    }

    @FXML
    private void handleChooseLogDirectoryButton() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        setMultilingualElement(directoryChooser, CHOOSE_DIRECTORY);
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

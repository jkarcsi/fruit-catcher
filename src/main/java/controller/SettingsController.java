package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import utils.Difficulty;
import utils.GameMode;
import utils.Language;
import utils.PreferencesUtil;
import utils.Texture;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import static utils.FXMLPaths.MAIN_MENU;
import static utils.SceneConstants.*;

public class SettingsController extends BaseController implements Initializable {

    @FXML
    ComboBox<String> gameModeComboBox;

    @FXML
    ComboBox<String> difficultyComboBox;

    @FXML
    ComboBox<String> textureComboBox;

    @FXML
    TextField logFilePathTextField;

    @FXML
    ComboBox<String> languageComboBox;

    @FXML
    ComboBox<String> leftKeyComboBox;

    @FXML
    ComboBox<String> rightKeyComboBox;

    @FXML
    Label gameModeLabel;

    @FXML
    Label difficultyLabel;

    @FXML
    Label textureLabel;

    @FXML
    Label logFilePathLabel;

    @FXML
    Label languageLabel;

    @FXML
    Label leftKeyLabel;

    @FXML
    Label rightKeyLabel;

    @FXML
    Button saveButton;

    @FXML
    Button backToMainMenuButton;

    @FXML
    Button chooseDirectoryButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadSettings();
        updateTexts();
        loadComboBoxItems();
        addListeners();
    }

    private void addListeners() {
        textureComboBox.valueProperty().addListener((observable, oldValue, newValue) -> applyTexture());
    }

    private void loadSettings() {
        gameModeComboBox.setValue(PreferencesUtil.getPreference(GAME_MODE, GameMode.NORMAL.getValue()));
        difficultyComboBox.setValue(PreferencesUtil.getPreference(DIFFICULTY, Difficulty.EASY.getValue()));
        textureComboBox.setValue(PreferencesUtil.getTexture().getTextureName());
        logFilePathTextField.setText(PreferencesUtil.getPreference(LOG_FILE_PATH, ""));
        languageComboBox.setValue(PreferencesUtil.getPreference(LANGUAGE, ENGLISH));
        leftKeyComboBox.setValue(PreferencesUtil.getPreference(LEFT_KEY, LEFT_ARROW));
        rightKeyComboBox.setValue(PreferencesUtil.getPreference(RIGHT_KEY, RIGHT_ARROW));
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

    private void loadComboBoxItems() {
        gameModeComboBox.getItems().addAll(Arrays.stream(GameMode.values()).map(GameMode::getValue).toList());
        difficultyComboBox.getItems().addAll(Arrays.stream(Difficulty.values()).map(Difficulty::getValue).toList());
        textureComboBox.getItems().addAll(Arrays.stream(Texture.values()).map(Texture::getTextureName).toList());
        languageComboBox.getItems().addAll(Arrays.stream(Language.values()).map(Language::getValue).toList());
        leftKeyComboBox.getItems().addAll("<", "A", "J");
        rightKeyComboBox.getItems().addAll(">", "D", "L");
    }

    @FXML
    private void handleSaveButton(ActionEvent event) {
        PreferencesUtil.setPreference(GAME_MODE, gameModeComboBox.getValue());
        PreferencesUtil.setPreference(DIFFICULTY, difficultyComboBox.getValue());
        PreferencesUtil.setPreference(TEXTURE, textureComboBox.getValue());
        PreferencesUtil.setPreference(LOG_FILE_PATH, logFilePathTextField.getText());
        PreferencesUtil.setPreference(LANGUAGE, languageComboBox.getValue());
        PreferencesUtil.setPreference(LEFT_KEY, leftKeyComboBox.getValue());
        PreferencesUtil.setPreference(RIGHT_KEY, rightKeyComboBox.getValue());

        applyTexture();

        handleBackToMainMenuButton(event);
    }

    private void applyTexture() {
        String selectedTexture = textureComboBox.getValue();
        Texture texture = Texture.valueOf(selectedTexture.toUpperCase());

        PreferencesUtil.setTexture(texture);
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

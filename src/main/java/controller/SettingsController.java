package controller;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import util.Difficulty;
import util.GameMode;
import util.Language;
import util.Localizable;
import util.PreferencesUtil;
import util.Texture;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import static util.FXMLPaths.MAIN_MENU;
import static util.PreferencesUtil.setDefaultLanguagePreference;
import static util.ResourcePaths.DEFAULT_LOG_DIRECTORY;
import static util.SceneConstants.*;

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

    private ChangeListener<String> languageChangeListener;
    private Map<String, String> localizedToEnumTextureMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadSettings();
        initializeLocalizedToEnumTextureMap();
        updateTexts();
        loadComboBoxItems();
        addListeners();
    }

    private void initializeLocalizedToEnumTextureMap() {
        localizedToEnumTextureMap = new HashMap<>();
        ResourceBundle bundle = getBundle();
        for (Texture texture : Texture.values()) {
            String localizedTextureName = bundle.getString(texture.getKey().toLowerCase());
            localizedToEnumTextureMap.put(capitalize(localizedTextureName), texture.name());
        }
    }

    private void addListeners() {
        textureComboBox.valueProperty().addListener((observable, oldValue, newValue) -> applyTexture());

        languageChangeListener = (observable, oldValue, newValue) -> switchLanguageAndUpdateComboBoxDisplay(newValue);
        languageComboBox.valueProperty().addListener(languageChangeListener);
    }

    private void loadSettings() {
        ResourceBundle bundle = getBundle();
        gameModeComboBox.setValue(capitalize(bundle.getString(GAME_MODE + DOT + PreferencesUtil.getPreference(GAME_MODE,
                GameMode.NORMAL.getValue()).toLowerCase())));
        difficultyComboBox.setValue(capitalize(bundle.getString(DIFFICULTY + DOT + PreferencesUtil.getPreference(
                DIFFICULTY,
                Difficulty.EASY.getValue()).toLowerCase())));
        textureComboBox.setValue(capitalize(bundle.getString(TEXTURE + DOT + PreferencesUtil.getTexture()
                .getValue()
                .toLowerCase())));
        languageComboBox.setValue(capitalize(bundle.getString(LANGUAGE + DOT + PreferencesUtil.getPreference(LANGUAGE,
                ENGLISH).toLowerCase())));

        String logPath = PreferencesUtil.getPreference(LOG_FILE_PATH, DEFAULT_LOG_DIRECTORY);
        logFilePathTextField.setText(("".equals(logPath) || DEFAULT_LOG_DIRECTORY.equals(logPath)) ? PreferencesUtil.getDefaultLogPath() : logPath);

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
        ResourceBundle bundle = getBundle();

        gameModeComboBox.getItems()
                .addAll(Arrays.stream(GameMode.values())
                        .map(mode -> capitalize(bundle.getString(GAME_MODE + DOT + mode.getValue().toLowerCase())))
                        .toList());
        difficultyComboBox.getItems()
                .addAll(Arrays.stream(Difficulty.values())
                        .map(difficulty -> capitalize(bundle.getString(DIFFICULTY + DOT + difficulty.getValue()
                                .toLowerCase())))
                        .toList());
        textureComboBox.getItems()
                .addAll(Arrays.stream(Texture.values())
                        .map(texture -> capitalize(bundle.getString(TEXTURE + DOT + texture.getValue().toLowerCase())))
                        .toList());
        languageComboBox.getItems()
                .addAll(Arrays.stream(Language.values())
                        .map(language -> capitalize(bundle.getString(LANGUAGE + DOT + language.getValue()
                                .toLowerCase())))
                        .toList());
        leftKeyComboBox.getItems().addAll("<", "A", "J");
        rightKeyComboBox.getItems().addAll(">", "D", "L");
    }

    @FXML
    private void handleSaveButton(ActionEvent event) {
        PreferencesUtil.setPreference(GAME_MODE, getEnglishValue(gameModeComboBox.getValue(), GameMode.values()));
        PreferencesUtil.setPreference(DIFFICULTY, getEnglishValue(difficultyComboBox.getValue(), Difficulty.values()));
        PreferencesUtil.setPreference(TEXTURE, getEnglishValue(textureComboBox.getValue(), Texture.values()));
        PreferencesUtil.setPreference(LOG_FILE_PATH, logFilePathTextField.getText());
        PreferencesUtil.setPreference(LANGUAGE, getEnglishValue(languageComboBox.getValue(), Language.values()));
        PreferencesUtil.setPreference(LEFT_KEY, leftKeyComboBox.getValue());
        PreferencesUtil.setPreference(RIGHT_KEY, rightKeyComboBox.getValue());

        applyTexture();
        setDefaultLanguagePreference();

        handleBackToMainMenuButton(event);
    }

    private <T extends Enum<T> & Localizable> String getEnglishValue(String localizedValue, T[] values) {
        ResourceBundle currentBundle = getBundle();
        ResourceBundle englishBundle = ResourceBundle.getBundle(MESSAGES, new Locale("en"));

        // First, it looks at the localized value in the current language resource file and looks for the corresponding key
        for (T value : values) {
            String localizedKey = value.getKey();
            if (currentBundle.containsKey(localizedKey) && capitalize(currentBundle.getString(localizedKey)).equals(
                    localizedValue)) {
                // Use this key in the English resource file to return the English value
                return englishBundle.getString(localizedKey);
            }
        }

        // If this is not found in the current language resource file, look for it in the English resource file
        for (T value : values) {
            String localizedKey = value.getKey();
            if (englishBundle.containsKey(localizedKey) && capitalize(englishBundle.getString(localizedKey)).equals(
                    localizedValue)) {
                return englishBundle.getString(localizedKey);
            }
        }

        return localizedValue; // Returns the original value if there is no match at all
    }

    private void applyTexture() {
        initializeLocalizedToEnumTextureMap();
        String selectedTexture = textureComboBox.getValue();
        if (selectedTexture != null) {
            try {
                String textureEnumName = getTextureEnumName(selectedTexture);
                Texture texture = Texture.valueOf(textureEnumName);
                PreferencesUtil.setTexture(texture);
                applyUserStylesheet();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
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

    public void switchLanguageAndUpdateComboBoxDisplay(String newLanguage) {
        // Update the language preference
        PreferencesUtil.setPreference(LANGUAGE, capitalize(getEnglishValue(newLanguage, Language.values())));

        // Reload the resource bundle to reflect the new language
        ResourceBundle.clearCache();
        getBundle();

        // Temporarily disable the listener
        if (languageChangeListener != null) {
            languageComboBox.valueProperty().removeListener(languageChangeListener);
        }

        // Update the UI texts
        updateTexts();

        // Update the ComboBox items
        updateComboBoxItems();

        // Re-enable the listener
        languageComboBox.valueProperty().addListener(languageChangeListener);
    }

    private void updateComboBoxItems() {
        ResourceBundle bundle = getBundle();

        gameModeComboBox.getItems().clear();
        gameModeComboBox.getItems()
                .addAll(Arrays.stream(GameMode.values())
                        .map(mode -> capitalize(bundle.getString(GAME_MODE + DOT + mode.getValue().toLowerCase())))
                        .toList());
        String gameModePreference = PreferencesUtil.getPreference(GAME_MODE, GameMode.NORMAL.getValue().toLowerCase())
                .toLowerCase();
        gameModeComboBox.setValue(capitalize(bundle.getString(GAME_MODE + DOT + gameModePreference)));

        difficultyComboBox.getItems().clear();
        difficultyComboBox.getItems()
                .addAll(Arrays.stream(Difficulty.values())
                        .map(difficulty -> capitalize(bundle.getString(DIFFICULTY + DOT + difficulty.getValue()
                                .toLowerCase())))
                        .toList());
        String difficultyPreference = PreferencesUtil.getPreference(DIFFICULTY,
                Difficulty.EASY.getValue().toLowerCase()).toLowerCase();
        difficultyComboBox.setValue(capitalize(bundle.getString(DIFFICULTY + DOT + difficultyPreference)));

        textureComboBox.getItems().clear();
        textureComboBox.getItems()
                .addAll(Arrays.stream(Texture.values())
                        .map(texture -> capitalize(bundle.getString(TEXTURE + DOT + texture.getValue().toLowerCase())))
                        .toList());
        String texturePreference = PreferencesUtil.getTexture().getValue().toLowerCase();
        textureComboBox.setValue(capitalize(bundle.getString(TEXTURE + DOT + texturePreference)));

        languageComboBox.getItems().clear();
        languageComboBox.getItems()
                .addAll(Arrays.stream(Language.values())
                        .map(language -> capitalize(bundle.getString(LANGUAGE + DOT + language.getValue()
                                .toLowerCase())))
                        .toList());
        String languagePreference = PreferencesUtil.getPreference(LANGUAGE, ENGLISH.toLowerCase()).toLowerCase();
        languageComboBox.setValue(capitalize(bundle.getString(LANGUAGE + DOT + languagePreference)));
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private String getTextureEnumName(String localizedName) {
        String enumName = localizedToEnumTextureMap.get(localizedName);
        if (enumName == null) {
            throw new IllegalArgumentException("No matching texture enum found for: " + localizedName);
        }
        return enumName;
    }
}

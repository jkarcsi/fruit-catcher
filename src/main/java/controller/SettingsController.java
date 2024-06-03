package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import utils.PreferencesUtil;
import utils.UserSession;

import java.io.File;

import static utils.FXMLPaths.MAIN_MENU;

public class SettingsController extends BaseController {

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
    public void initialize() {
        String username = UserSession.getInstance().getUsername();
        gameModeComboBox.getItems().addAll("Normal", "Freeplay");
        leftKeyComboBox.getItems().addAll("LEFT", "A", "J");
        rightKeyComboBox.getItems().addAll("RIGHT", "D", "L");
        languageComboBox.getItems().addAll("Magyar", "English");
        difficultyComboBox.getItems().addAll("Easy", "Medium", "Hard");
        textureComboBox.getItems().addAll("Classic", "Retro", "Futuristic");
        loadSettings(username);
    }

    private void loadSettings(String username) {
        gameModeComboBox.setValue(PreferencesUtil.getPreference(username, "gameMode", "normal"));
        difficultyComboBox.setValue(PreferencesUtil.getPreference(username, "difficulty", "Beginner"));
        textureComboBox.setValue(PreferencesUtil.getPreference(username, "texture", "classic"));
        logFilePathTextField.setText(PreferencesUtil.getPreference(username, "logFilePath", ""));
        languageComboBox.setValue(PreferencesUtil.getPreference(username, "language", "English"));
        leftKeyComboBox.setValue(PreferencesUtil.getPreference(username, "leftKey", "LEFT"));
        rightKeyComboBox.setValue(PreferencesUtil.getPreference(username, "rightKey", "RIGHT"));
    }

    @FXML
    private void handleSaveButton(ActionEvent event) {
        String username = UserSession.getInstance().getUsername();
        PreferencesUtil.setPreference(username, "gameMode", gameModeComboBox.getValue());
        PreferencesUtil.setPreference(username, "difficulty", difficultyComboBox.getValue());
        PreferencesUtil.setPreference(username, "texture", textureComboBox.getValue());
        PreferencesUtil.setPreference(username, "logFilePath", logFilePathTextField.getText());
        PreferencesUtil.setPreference(username, "language", languageComboBox.getValue());
        PreferencesUtil.setPreference(username, "leftKey", leftKeyComboBox.getValue());
        PreferencesUtil.setPreference(username, "rightKey", rightKeyComboBox.getValue());
        handleBackToMainMenuButton(event);
    }

    @FXML
    private void handleChooseLogDirectoryButton(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Log File Directory");
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

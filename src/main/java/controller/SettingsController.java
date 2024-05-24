package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.prefs.Preferences;

public class SettingsController {

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

    private Preferences prefs;

    @FXML
    public void initialize() {
        prefs = Preferences.userNodeForPackage(SettingsController.class);
        loadSettings();

        leftKeyComboBox.getItems().addAll("LEFT", "A", "J");
        rightKeyComboBox.getItems().addAll("RIGHT", "D", "L");
    }

    private void loadSettings() {
        gameModeComboBox.setValue(prefs.get("gameMode", "Easy"));
        difficultyComboBox.setValue(prefs.get("difficulty", "Beginner"));
        textureComboBox.setValue(prefs.get("texture", "Default"));
        logFilePathTextField.setText(prefs.get("logFilePath", ""));
        languageComboBox.setValue(prefs.get("language", "English"));
        leftKeyComboBox.setValue(prefs.get("leftKey", "LEFT"));
        rightKeyComboBox.setValue(prefs.get("rightKey", "RIGHT"));
    }

    @FXML
    private void handleSaveButton(ActionEvent event) {
        prefs.put("gameMode", gameModeComboBox.getValue());
        prefs.put("difficulty", difficultyComboBox.getValue());
        prefs.put("texture", textureComboBox.getValue());
        prefs.put("logFilePath", logFilePathTextField.getText());
        prefs.put("language", languageComboBox.getValue());
        prefs.put("leftKey", leftKeyComboBox.getValue());
        prefs.put("rightKey", rightKeyComboBox.getValue());
    }

    @FXML
    private void handleBackToMainMenuButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fruitcatchgame/view/mainMenu.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

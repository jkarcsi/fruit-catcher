package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DescriptionController extends BaseController {
    @FXML
    private void handleBackToMainMenuButton(ActionEvent event) {
        navigateTo("/fruitcatchgame/view/MainMenu.fxml", event);
    }
}

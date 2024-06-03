package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class StartController extends BaseController {
    @FXML
    private void handleStartButton(ActionEvent event) {
        navigateTo("/fruitcatchgame/view/login.fxml", event);
    }
}

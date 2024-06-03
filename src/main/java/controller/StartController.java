package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import static utils.FXMLPaths.LOGIN;

public class StartController extends BaseController {
    @FXML
    private void handleStartButton(ActionEvent event) {
        navigateTo(LOGIN, event);
    }
}

package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import static utils.FXMLPaths.MAIN_MENU;

public class DescriptionController extends BaseController {
    @FXML
    private void handleBackToMainMenuButton(ActionEvent event) {
        navigateTo(MAIN_MENU, event);
    }
}

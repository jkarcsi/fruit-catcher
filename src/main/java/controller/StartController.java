package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

import static utils.FXMLPaths.LOGIN;

public class StartController extends BaseController implements Initializable {

    @FXML
    public BorderPane startPageRoot;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startPageRoot.setUserData("StartPage");
    }

    @FXML
    private void handleStartButton(ActionEvent event) {
        navigateTo(LOGIN, event);
    }
}

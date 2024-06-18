package controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
        startPageRoot.setOnKeyPressed(this::handleKeyPressed);
    }

    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleStartButton(event);
        }
    }

    @FXML
    private void handleStartButton(Event event) {
        navigateTo(LOGIN, event);
    }
}

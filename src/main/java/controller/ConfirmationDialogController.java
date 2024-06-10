package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

import static utils.SceneConstants.NO;
import static utils.SceneConstants.YES;

public class ConfirmationDialogController extends BaseController implements Initializable {

    @FXML
    private Button yesButton;

    @FXML
    private Button noButton;
    private boolean confirmed = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setMultilingualElement(yesButton, YES);
        setMultilingualElement(noButton, NO);
    }

    @FXML
    private void handleYesButton(ActionEvent event) {
        confirmed = true;
        closeDialog(event);
    }

    @FXML
    private void handleNoButton(ActionEvent event) {
        confirmed = false;
        closeDialog(event);
    }

    private void closeDialog(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}

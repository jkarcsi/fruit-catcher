package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class ConfirmationDialogController {

    private boolean confirmed = false;

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

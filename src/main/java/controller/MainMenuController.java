package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.UserDAO;
import utils.LoggerUtil;
import utils.UserSession;

import java.io.IOException;
import java.sql.SQLException;

public class MainMenuController extends BaseController {

    private final String username = UserSession.getInstance().getUsername();

    @FXML
    private Label usernameLabel;

    @FXML
    public void initialize() {
        usernameLabel.setText(username);
    }

    @FXML
    private void handleStartGameButton(ActionEvent event) {
        navigateTo("/fruitcatchgame/view/game.fxml", event);
    }

    @FXML
    private void handleDescriptionButton(ActionEvent event) {
        navigateTo("/fruitcatchgame/view/description.fxml", event);
    }

    @FXML
    private void handlePlayerResultsButton(ActionEvent event) {
        navigateTo("/fruitcatchgame/view/playerResults.fxml", event);
    }

    @FXML
    private void handlePlayerRankingsButton(ActionEvent event) {
        navigateTo("/fruitcatchgame/view/playerRankings.fxml", event);
    }

    @FXML
    private void handleSettingsButton(ActionEvent event) {
        navigateTo("/fruitcatchgame/view/settings.fxml", event);
    }

    @FXML
    private void handleChangePasswordButton(ActionEvent event) {
        navigateTo("/fruitcatchgame/view/changePassword.fxml", event);
    }

    @FXML
    private void handleLogoutButton(ActionEvent event) {
        navigateTo("/fruitcatchgame/view/login.fxml", event);
    }

    @FXML
    private void handleDeleteAccountButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fruitcatchgame/view/confirmationDialog.fxml"));
            Stage dialogStage = new Stage();
            dialogStage.setScene(new Scene(loader.load()));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(((javafx.scene.Node) event.getSource()).getScene().getWindow());
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            ConfirmationDialogController controller = loader.getController();
            if (controller.isConfirmed()) {
                deleteUserAccount(event);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void deleteUserAccount(ActionEvent event) {
        try {
            UserDAO userDAO = new UserDAO();
            userDAO.deleteUser(username);
            LoggerUtil.logInfo("Account deleted for user: " + username);
            // Log out and navigate to login screen
            UserSession.getInstance().setUsername(null);
            navigateTo("/fruitcatchgame/view/login.fxml", event);
        } catch (SQLException e) {
            LoggerUtil.logSevere("Error deleting account for user: " + username);
            e.printStackTrace();
        }
    }

}

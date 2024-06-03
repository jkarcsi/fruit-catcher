package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.user.UserDAO;
import utils.LoggerUtil;
import utils.UserSession;

import java.io.IOException;
import java.sql.SQLException;

import static utils.FXMLPaths.CHANGE_PASSWORD;
import static utils.FXMLPaths.DELETE_ACCOUNT_CONFIRMATION;
import static utils.FXMLPaths.DESCRIPTION;
import static utils.FXMLPaths.GAME;
import static utils.FXMLPaths.LOGIN;
import static utils.FXMLPaths.PLAYER_RANKINGS;
import static utils.FXMLPaths.PLAYER_RESULTS;
import static utils.FXMLPaths.SETTINGS;

public class MainMenuController extends BaseController {

    @FXML
    private Label usernameLabel;

    @FXML
    public void initialize() {
        usernameLabel.setText(getUsername());
    }

    @FXML
    private void handleStartGameButton(ActionEvent event) {
        navigateTo(GAME, event);
    }

    @FXML
    private void handleDescriptionButton(ActionEvent event) {
        navigateTo(DESCRIPTION, event);
    }

    @FXML
    private void handlePlayerResultsButton(ActionEvent event) {
        navigateTo(PLAYER_RESULTS, event);
    }

    @FXML
    private void handlePlayerRankingsButton(ActionEvent event) {
        navigateTo(PLAYER_RANKINGS, event);
    }

    @FXML
    private void handleSettingsButton(ActionEvent event) {
        navigateTo(SETTINGS, event);
    }

    @FXML
    private void handleChangePasswordButton(ActionEvent event) {
        navigateTo(CHANGE_PASSWORD, event);
    }

    @FXML
    private void handleLogoutButton(ActionEvent event) {
        navigateTo(LOGIN, event);
    }

    @FXML
    private void handleDeleteAccountButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(DELETE_ACCOUNT_CONFIRMATION));
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
            userDAO.deleteUser(getUsername());
            LoggerUtil.logInfo("Account deleted for user: " + getUsername());
            // Log out and navigate to login screen
            UserSession.getInstance().setUsername(null);
            navigateTo(LOGIN, event);
        } catch (SQLException e) {
            LoggerUtil.logSevere("Error deleting account for user: " + getUsername());
            e.printStackTrace();
        }
    }

}

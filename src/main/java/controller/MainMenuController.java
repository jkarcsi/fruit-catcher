package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.user.UserDAO;
import util.FXMLPaths;
import util.LoggerUtil;
import util.UserSession;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static util.SceneConstants.CHANGE_PASSWORD;
import static util.FXMLPaths.DELETE_ACCOUNT_CONFIRMATION;
import static util.FXMLPaths.GAME;
import static util.FXMLPaths.LOGIN;
import static util.SceneConstants.SETTINGS;
import static util.SceneConstants.DELETE_ACCOUNT;
import static util.SceneConstants.DESCRIPTION;
import static util.SceneConstants.LOGGED_IN_AS;
import static util.SceneConstants.LOGOUT;
import static util.SceneConstants.PLAYER_RANKINGS;
import static util.SceneConstants.PLAYER_RESULTS;
import static util.SceneConstants.QUIT;
import static util.SceneConstants.START_GAME;

public class MainMenuController extends BaseController implements Initializable {


    @FXML
    private Label loggedInAsLabel;

    @FXML
    private Label usernameLabel;

    @FXML
    private Button startGameButton;

    @FXML
    private Button descriptionButton;

    @FXML
    private Button playerResultsButton;

    @FXML
    private Button playerRankingsButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button changePasswordButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button quitButton;

    @FXML
    private Button deleteAccountButton;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        setMultilingualElement(loggedInAsLabel, LOGGED_IN_AS);
        setMultilingualElement(startGameButton, START_GAME);
        setMultilingualElement(descriptionButton, DESCRIPTION);
        setMultilingualElement(playerResultsButton, PLAYER_RESULTS);
        setMultilingualElement(playerRankingsButton, PLAYER_RANKINGS);
        setMultilingualElement(settingsButton, SETTINGS);
        setMultilingualElement(changePasswordButton, CHANGE_PASSWORD);
        setMultilingualElement(logoutButton, LOGOUT);
        setMultilingualElement(quitButton, QUIT);
        setMultilingualElement(deleteAccountButton, DELETE_ACCOUNT);
        usernameLabel.setText(getUsername());
    }

    @FXML
    private void handleStartGameButton(ActionEvent event) {
        navigateTo(GAME, event);
    }

    @FXML
    private void handleDescriptionButton(ActionEvent event) {
        navigateTo(FXMLPaths.DESCRIPTION, event);
    }

    @FXML
    private void handlePlayerResultsButton(ActionEvent event) {
        navigateTo(FXMLPaths.PLAYER_RESULTS, event);
    }

    @FXML
    private void handlePlayerRankingsButton(ActionEvent event) {
        navigateTo(FXMLPaths.PLAYER_RANKINGS, event);
    }

    @FXML
    private void handleSettingsButton(ActionEvent event) {
        navigateTo(FXMLPaths.SETTINGS, event);
    }

    @FXML
    private void handleQuitButton(ActionEvent event) {
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void handleChangePasswordButton(ActionEvent event) {
        navigateTo(FXMLPaths.CHANGE_PASSWORD, event);
    }

    @FXML
    private void handleLogoutButton(ActionEvent event) {
        navigateTo(LOGIN, event);
    }

    @FXML
    private void handleDeleteAccountButton(ActionEvent event) {
        try {
            FXMLLoader loader = navigateToDialog(DELETE_ACCOUNT_CONFIRMATION, event);

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
            userDAO.deleteUser(getUsername(), false);
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

package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.UserDAO;
import utils.UserSession;

import java.io.IOException;
import java.sql.SQLException;

public class MainMenuController {

    private String username = UserSession.getInstance().getUsername();

    @FXML
    private void handleStartGameButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fruitcatchgame/view/game.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);

            GameController gameController = loader.getController();
            gameController.setUsername(username); // Beállítja a felhasználónevet a GameController-ben

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDescriptionButton(ActionEvent event) {
        navigateTo("/fruitcatchgame/view/description.fxml", event);
    }

    @FXML
    private void handlePlayerResultsButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fruitcatchgame/view/playerResults.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);

            PlayerResultsController playerResultsController = loader.getController();
            playerResultsController.setUsername(username);

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fruitcatchgame/view/changePassword.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            ChangePasswordController controller = loader.getController();
            controller.setUsername(username);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogoutButton(ActionEvent event) {
        navigateTo("/fruitcatchgame/view/login.fxml", event);
    }

    @FXML
    private void handleDeleteAccountButton(ActionEvent event) {
        try {
            UserDAO userDAO = new UserDAO();
            userDAO.deleteUser(username);
            navigateTo("/fruitcatchgame/view/login.fxml", event);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void navigateTo(String fxmlFile, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

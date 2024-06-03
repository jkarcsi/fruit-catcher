package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

import model.user.User;
import model.user.UserDAO;
import utils.LoggerUtil;
import utils.UserSession;

import static utils.FXMLPaths.MAIN_MENU;

public class ChangePasswordController extends BaseController {

    @FXML
    private PasswordField oldPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label errorMessage;

    private final String username = UserSession.getInstance().getUsername();

    @FXML
    private void handleChangePasswordButton(ActionEvent event) {
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        LoggerUtil.logInfo("Current password: " + oldPassword);
        LoggerUtil.logInfo("New password: " + newPassword);
        LoggerUtil.logInfo("Confirm password: " + confirmPassword);

        if (!newPassword.equals(confirmPassword)) {
            errorMessage.setText("New passwords do not match");
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUser(username);

            if (user == null) {
                LoggerUtil.logInfo("User not found: " + username);
                return;
            }

            String hashedOldPassword = hashPassword(oldPassword);

            if (user.getPassword().equals(hashedOldPassword)) {
                String hashedNewPassword = hashPassword(newPassword);
                user.setPassword(hashedNewPassword);
                userDAO.updateUser(user);
                LoggerUtil.logInfo("Password changed successfully");

                // Navigate back to main menu
                navigateTo(MAIN_MENU, event);
            } else {
                errorMessage.setText("Current password is incorrect");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToMainMenuButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(MAIN_MENU));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

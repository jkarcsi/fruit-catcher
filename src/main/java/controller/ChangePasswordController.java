package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

import java.sql.SQLException;

import model.user.User;
import model.user.UserDAO;
import utils.LoggerUtil;

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
            User user = userDAO.getUser(getUsername());

            if (user == null) {
                LoggerUtil.logInfo("User not found: " + getUsername());
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
        navigateTo(MAIN_MENU, event);
    }

}

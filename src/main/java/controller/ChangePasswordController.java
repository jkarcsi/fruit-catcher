package controller;

import exceptions.HashException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import model.User;
import model.UserDAO;
import utils.LoggerUtil;
import utils.UserSession;

public class ChangePasswordController {

    @FXML
    private PasswordField oldPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label errorMessage;

    private String username = UserSession.getInstance().getUsername();

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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fruitcatchgame/view/mainMenu.fxml"));
                Scene scene = new Scene(loader.load(), 800, 600);
                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.setResizable(false);
            } else {
                errorMessage.setText("Current password is incorrect");
            }
        } catch (SQLException | IOException | HashException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToMainMenuButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fruitcatchgame/view/mainMenu.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String hashPassword(String password) throws HashException {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new HashException(e.getMessage());
        }
    }
}

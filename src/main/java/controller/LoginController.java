package controller;

import exceptions.HashException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import model.User;
import model.UserDAO;
import utils.LoggerUtil;
import utils.UserSession;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label passwordReminderLabel;

    @FXML
    private Label errorMessage;

    @FXML
    private void handleLoginButton(ActionEvent event) throws HashException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String hashedPassword = hashPassword(password);

        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUser(username);

            if (user == null || !user.getPassword().equals(hashedPassword)) {
                errorMessage.setText("Invalid username or password.");
                LoggerUtil.logWarning("Invalid login attempt: " + username);
            } else if (user.getStatus().equals("banned")) {
                errorMessage.setText("User is banned.");
                LoggerUtil.logWarning("Banned user login attempt: " + username);
            } else {
                UserSession.getInstance().setUsername(username); // Set the username in UserSession
                FXMLLoader loader;
                if (user.getRole().equals("admin")) {
                    loader = new FXMLLoader(getClass().getResource("/fruitcatchgame/view/admin.fxml"));
                } else {
                    loader = new FXMLLoader(getClass().getResource("/fruitcatchgame/view/mainMenu.fxml"));
                }
                Scene scene = new Scene(loader.load(), 800, 600);

                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.setResizable(false);
                LoggerUtil.logInfo("User logged in: " + username);
            }
        } catch (SQLException | IOException e) {
            LoggerUtil.logSevere("Error during login attempt for user: " + username);
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRegisterButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fruitcatchgame/view/register.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
            LoggerUtil.logInfo("Navigated to registration screen");
        } catch (IOException e) {
            LoggerUtil.logSevere("Error navigating to registration screen");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleForgotPasswordButton(ActionEvent event) throws SQLException {
        String username = usernameField.getText();
        if (username.isEmpty()) {
            passwordReminderLabel.setText("Please enter your username.");
            LoggerUtil.logWarning("Forgot password attempt with empty username");
            return;
        }

        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUser(username);

        if (user != null) {
            passwordReminderLabel.setText("Password reminder: " + user.getPasswordReminder());
            LoggerUtil.logInfo("Password reminder shown for user: " + username);
        } else {
            passwordReminderLabel.setText("Username not found.");
            LoggerUtil.logWarning("Forgot password attempt with non-existent username: " + username);
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
            LoggerUtil.logSevere("Error hashing password");
            throw new HashException(e.getMessage());
        }
    }
}

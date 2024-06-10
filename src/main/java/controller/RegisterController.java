package controller;

import exceptions.HashException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;

import model.user.User;
import model.user.UserDAO;
import utils.LoggerUtil;

import static utils.FXMLPaths.LOGIN;

public class RegisterController extends BaseController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label passwordStrengthLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField passwordReminderField;

    @FXML
    private void handleRegisterButton(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String passwordReminder = passwordReminderField.getText();

        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match");
            return;
        }

        if (username.isEmpty() || password.isEmpty() || passwordReminder.isEmpty()) {
            errorLabel.setText("Username, password, and password reminder cannot be empty");
            return;
        }

        if (!isPasswordStrong(password)) {
            errorLabel.setText("Password is not strong enough");
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();

            if (null != userDAO.getUser(username)) {
                errorLabel.setText("Username already exists.");
                return;
            }

            String hashedPassword = hashPassword(password);
            userDAO.saveUser(new User(username,
                    hashedPassword,
                    passwordReminder,
                    "admin".equals(username) ? "admin" : "user",
                    "active"));
            LoggerUtil.logInfo("User registered successfully");

            // Navigate back to login screen
            navigateTo(LOGIN, event);

        } catch (SQLException | HashException e) {
            e.printStackTrace();
            errorLabel.setText("An error occurred. Please try again.");
        }
    }

    @FXML
    private void handleBackToLoginButton(ActionEvent event) {
        navigateTo(LOGIN, event);
    }

    @FXML
    private void checkPasswordStrength() {
        String password = passwordField.getText();
        String strength = getPasswordStrength(password);
        passwordStrengthLabel.setText("Password strength: " + strength);
    }

    private boolean isPasswordStrong(String password) {
        // Implement your own password strength criteria here
        return password.length() >= 8 && password.matches(".*\\d.*") && password.matches(".*[a-zA-Z].*");
    }

    private String getPasswordStrength(String password) {
        if (password.length() < 8) {
            return "Weak";
        } else if (password.matches(".*\\d.*") && password.matches(".*[a-zA-Z].*")) {
            return "Strong";
        } else {
            return "Medium";
        }
    }

}

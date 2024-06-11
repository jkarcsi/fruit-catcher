package controller;

import exceptions.HashException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import model.user.User;
import model.user.UserDAO;
import utils.LoggerUtil;

import static utils.FXMLPaths.LOGIN;

public class RegisterController extends BaseController implements Initializable {

    public static final String USERNAME_PASSWORD_AND_PASSWORD_REMINDER_CANNOT_BE_EMPTY = "Username, password, and password reminder cannot be empty";
    public static final String PASSWORD_IS_NOT_STRONG_ENOUGH = "Password is not strong enough";
    public static final String USERNAME_ALREADY_EXISTS = "Username already exists.";
    public static final String AN_ERROR_OCCURRED_PLEASE_TRY_AGAIN = "An error occurred. Please try again.";
    @FXML
    public Button registerButton;

    @FXML
    public Button backToLoginButton;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

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
            errorLabel.setText(USERNAME_PASSWORD_AND_PASSWORD_REMINDER_CANNOT_BE_EMPTY);
            return;
        }

        if (!isPasswordStrong(password)) {
            errorLabel.setText(PASSWORD_IS_NOT_STRONG_ENOUGH);
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();

            if (null != userDAO.getUser(username)) {
                errorLabel.setText(USERNAME_ALREADY_EXISTS);
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
            errorLabel.setText(AN_ERROR_OCCURRED_PLEASE_TRY_AGAIN);
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

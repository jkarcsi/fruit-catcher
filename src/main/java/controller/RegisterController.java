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
import utils.PasswordStrength;
import utils.UserRole;

import static utils.FXMLPaths.LOGIN;
import static utils.SceneConstants.AN_ERROR_OCCURRED_PLEASE_TRY_AGAIN;
import static utils.SceneConstants.BACK_TO_LOGIN;
import static utils.SceneConstants.CONFIRM_PASSWORD;
import static utils.SceneConstants.PASSWORD;
import static utils.SceneConstants.PASSWORDS_DO_NOT_MATCH;
import static utils.SceneConstants.PASSWORD_IS_NOT_STRONG_ENOUGH;
import static utils.SceneConstants.PASSWORD_STRENGTH;
import static utils.SceneConstants.REGISTER;
import static utils.SceneConstants.USERNAME_ALREADY_EXISTS;
import static utils.SceneConstants.USERNAME_PASSWORD_AND_PASSWORD_REMINDER_CANNOT_BE_EMPTY;

public class RegisterController extends BaseController implements Initializable {
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
    public void initialize(URL location, ResourceBundle resources) {
        setMultilingualElement(registerButton, REGISTER);
        setMultilingualElement(backToLoginButton, BACK_TO_LOGIN);
        setMultilingualElement(passwordStrengthLabel, PASSWORD_STRENGTH);
        setMultilingualPromptElement(passwordField, PASSWORD);
        setMultilingualPromptElement(confirmPasswordField, CONFIRM_PASSWORD);
    }

    @FXML
    private void handleRegisterButton(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        String passwordReminder = passwordReminderField.getText();

        if (!password.equals(confirmPassword)) {
            errorLabel.setText(PASSWORDS_DO_NOT_MATCH);
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
                    UserRole.ADMIN.value().equals(username) ? UserRole.ADMIN.value() : UserRole.USER.value(),
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
            return PasswordStrength.WEAK.value();
        } else if (password.matches(".*\\d.*") && password.matches(".*[a-zA-Z].*")) {
            return PasswordStrength.STRONG.value();
        } else {
            return PasswordStrength.MEDIUM.value();
        }
    }
}

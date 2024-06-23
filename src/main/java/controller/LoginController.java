package controller;

import exceptions.HashException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import model.user.User;
import model.user.UserDAO;
import utils.LoggerUtil;
import utils.PreferencesUtil;
import utils.UserRole;
import utils.UserSession;

import static utils.FXMLPaths.REGISTER;
import static utils.LoggerUtil.configureLogger;

public class LoginController extends BaseController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label passwordReminderLabel;

    @FXML
    private Label errorMessage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        passwordField.setOnKeyPressed(this::handleKeyPressed);
    }

    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLoginButton();
        }
    }

    @FXML
    private void handleLoginButton() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            String hashedPassword = hashPassword(password);
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUser(username, false);

            if (user == null || !user.getPassword().equals(hashedPassword)) {
                errorMessage.setText("Invalid username or password.");
                LoggerUtil.logWarning("Invalid login attempt: " + username);
            } else if (user.getStatus().equals("banned")) {
                errorMessage.setText("User is banned.");
                LoggerUtil.logWarning("Banned user login attempt: " + username);
            } else {
                if (!user.getRole().equals(UserRole.ADMIN.value())) {
                    UserSession.getInstance().setUsername(username); // Set the username in UserSession
                    PreferencesUtil.setDefaultPreferences(username); // Set default settings for new users
                    configureLogger();
                }
                navigateByRole(user, usernameField);
            }
        } catch (SQLException | IOException | HashException e) {
            LoggerUtil.logSevere("Error during login attempt for user: " + username);
            e.printStackTrace();
        }
    }


    @FXML
    private void handleRegisterButton(ActionEvent event) {
        navigateTo(REGISTER, event);
    }

    @FXML
    private void handleForgotPasswordButton() throws SQLException {
        String username = usernameField.getText();
        if (username.isEmpty()) {
            passwordReminderLabel.setText("Please enter your username.");
            LoggerUtil.logWarning("Forgot password attempt with empty username");
            return;
        }

        UserDAO userDAO = new UserDAO();
        User user = userDAO.getUser(username, false);

        if (user != null) {
            passwordReminderLabel.setText("Password reminder: " + user.getPasswordReminder());
            LoggerUtil.logInfo("Password reminder shown for user: " + username);
        } else {
            passwordReminderLabel.setText("Username not found.");
            LoggerUtil.logWarning("Forgot password attempt with non-existent username: " + username);
        }
    }

}

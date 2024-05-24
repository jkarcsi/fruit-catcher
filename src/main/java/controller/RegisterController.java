package controller;

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

public class RegisterController {

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
            String hashedPassword = hashPassword(password);
            userDAO.saveUser(new User(username, hashedPassword, passwordReminder, "admin".equals(username) ? "admin" : "user", "active"));
            System.out.println("User registered successfully");

            // Navigate back to login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fruitcatchgame/view/login.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);

        } catch (SQLException | IOException e) {
            e.printStackTrace();
            errorLabel.setText("An error occurred. Please try again.");
        }
    }

    @FXML
    private void handleBackToLoginButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fruitcatchgame/view/login.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private String hashPassword(String password) {
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
            throw new RuntimeException(e);
        }
    }
}

package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

import model.User;
import model.UserDAO;

public class ChangePasswordController {

    @FXML
    private PasswordField oldPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    private String username;

    public void setUsername(String username) {
        this.username = username;
    }

    @FXML
    private void handleChangePasswordButton(ActionEvent event) {
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        System.out.println("Old password: " + oldPassword);
        System.out.println("New password: " + newPassword);
        System.out.println("Confirm password: " + confirmPassword);

        if (!newPassword.equals(confirmPassword)) {
            System.out.println("New passwords do not match");
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUser(username);

            if (user == null) {
                System.out.println("User not found: " + username);
                return;
            }

            String hashedOldPassword = hashPassword(oldPassword);
            System.out.println("Hashed old password: " + hashedOldPassword);

            if (user != null && user.getPassword().equals(hashedOldPassword)) {
                String hashedNewPassword = hashPassword(newPassword);
                user.setPassword(hashedNewPassword);
                userDAO.updateUser(user);
                System.out.println("Password changed successfully");

                // Navigate back to main menu
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fruitcatchgame/view/mainMenu.fxml"));
                Scene scene = new Scene(loader.load(), 800, 600);
                MainMenuController controller = loader.getController();
                controller.setUsername(username);
                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
            stage.setResizable(false);
            } else {
                System.out.println("Old password is incorrect");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToMainMenuButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fruitcatchgame/view/mainMenu.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);
            MainMenuController controller = loader.getController();
            controller.setUsername(username);
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setResizable(false);
        } catch (IOException e) {
            e.printStackTrace();
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

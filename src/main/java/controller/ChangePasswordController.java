package controller;

import exception.HashException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import model.user.User;
import model.user.UserDAO;
import util.LoggerUtil;

import static util.FXMLPaths.MAIN_MENU;
import static util.SceneConstants.BACK_TO_MAIN_MENU;
import static util.SceneConstants.CHANGE_PASSWORD;
import static util.SceneConstants.CURRENT_PASSWORD;
import static util.SceneConstants.CURRENT_PASSWORD_IS_INCORRECT;
import static util.SceneConstants.NEW_PASSWORD;
import static util.SceneConstants.CONFIRM_NEW_PASSWORD;
import static util.SceneConstants.PASSWORDS_DO_NOT_MATCH;
import static util.SceneConstants.PASSWORD_IS_NOT_STRONG_ENOUGH;
import static util.SceneConstants.PASSWORD_STRENGTH;

public class ChangePasswordController extends BaseController implements Initializable {

    @FXML
    private Button changePasswordButton;

    @FXML
    private Button backToMainMenuButton;

    @FXML
    private PasswordField oldPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private Label newPasswordStrengthLabel;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label errorMessage;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setMultilingualPromptElement(oldPasswordField, CURRENT_PASSWORD);
        setMultilingualPromptElement(newPasswordField, NEW_PASSWORD);
        setMultilingualPromptElement(confirmPasswordField, CONFIRM_NEW_PASSWORD);
        setMultilingualElement(changePasswordButton, CHANGE_PASSWORD);
        setMultilingualElement(backToMainMenuButton, BACK_TO_MAIN_MENU);
    }

    @FXML
    private void checkPasswordStrength() {
        String password = newPasswordField.getText();
        String strength = getMultilingualText(getPasswordStrength(password));
        setMultilingualElement(newPasswordStrengthLabel, PASSWORD_STRENGTH);
        newPasswordStrengthLabel.setText(newPasswordStrengthLabel.getText() + ": " + strength);
    }

    @FXML
    private void handleChangePasswordButton(ActionEvent event) {
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!newPassword.equals(confirmPassword)) {
            setMultilingualElement(errorMessage, PASSWORDS_DO_NOT_MATCH);
            return;
        }

        if (!isPasswordStrong(newPassword)) {
            setMultilingualElement(errorMessage, PASSWORD_IS_NOT_STRONG_ENOUGH);
            return;
        }

        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUser(getUsername(), false);

            if (user == null) {
                LoggerUtil.logInfo("User not found: " + getUsername());
                return;
            }

            String hashedOldPassword = hashPassword(oldPassword);

            if (user.getPassword().equals(hashedOldPassword)) {
                String hashedNewPassword = hashPassword(newPassword);
                user.setPassword(hashedNewPassword);
                userDAO.updateUser(user, false);
                LoggerUtil.logInfo("Password changed successfully");

                // Navigate back to main menu
                navigateTo(MAIN_MENU, event);
            } else {
                setMultilingualElement(errorMessage,  CURRENT_PASSWORD_IS_INCORRECT);
            }
        } catch (SQLException | HashException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToMainMenuButton(ActionEvent event) {
        navigateTo(MAIN_MENU, event);
    }
}

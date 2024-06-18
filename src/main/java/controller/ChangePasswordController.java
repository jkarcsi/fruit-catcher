package controller;

import exceptions.HashException;
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
import utils.LoggerUtil;

import static utils.FXMLPaths.MAIN_MENU;
import static utils.SceneConstants.BACK_TO_MAIN_MENU;
import static utils.SceneConstants.CHANGE_PASSWORD;
import static utils.SceneConstants.CURRENT_PASSWORD;
import static utils.SceneConstants.CURRENT_PASSWORD_IS_INCORRECT;
import static utils.SceneConstants.NEW_PASSWORD;
import static utils.SceneConstants.CONFIRM_NEW_PASSWORD;
import static utils.SceneConstants.PASSWORDS_DO_NOT_MATCH;

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
    private void handleChangePasswordButton(ActionEvent event) {
        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!newPassword.equals(confirmPassword)) {
            setMultilingualElement(errorMessage, PASSWORDS_DO_NOT_MATCH);
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

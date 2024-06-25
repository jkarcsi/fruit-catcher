package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.user.User;
import model.user.UserDAO;
import util.Language;
import util.LoggerUtil;
import util.PreferencesUtil;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static util.FXMLPaths.LOGIN;
import static util.SceneConstants.ACTIVE;
import static util.SceneConstants.BAN;
import static util.SceneConstants.BANNED;
import static util.SceneConstants.CHANGE_LANGUAGE;
import static util.SceneConstants.ENGLISH;
import static util.SceneConstants.LANGUAGE;
import static util.SceneConstants.LOGOUT;
import static util.SceneConstants.STATUS;
import static util.SceneConstants.UNBAN;
import static util.SceneConstants.USERNAME;

public class AdminController extends BaseController implements Initializable {

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> statusColumn;

    private ObservableList<User> usersList;

    @FXML
    private Button banUserButton;

    @FXML
    private Button unbanUserButton;

    @FXML
    private Button changeLanguageButton;

    @FXML
    private Button logoutButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        updateTexts();
        updateTableColumns();
        updateTableValues();
        super.initialize(url, resourceBundle);
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>(USERNAME));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>(STATUS));
        usersList = FXCollections.observableArrayList();
        loadUsers();
    }

    private void loadUsers() {
        try {
            UserDAO userDAO = new UserDAO();
            usersList.setAll(userDAO.getAllUsers(false));
            usersTable.setItems(usersList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBanUserButton() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null && !selectedUser.getUsername().equals(getUsername())) {
            try {
                UserDAO userDAO = new UserDAO();
                userDAO.banUser(selectedUser.getUsername(), false);
                loadUsers(); // Refresh the users list
                usersTable.getSelectionModel().select(selectedUser); // Retain the selection
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            // Show error message if the admin tries to ban themselves
            LoggerUtil.logWarning("Admin cannot ban themselves.");
        }
    }

    @FXML
    private void handleUnbanUserButton() {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try {
                UserDAO userDAO = new UserDAO();
                userDAO.unbanUser(selectedUser.getUsername(), false);
                loadUsers(); // Refresh the users list
                usersTable.getSelectionModel().select(selectedUser); // Retain the selection
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleLogoutButton(ActionEvent event) {
        navigateTo(LOGIN, event);
    }

    @FXML
    private void handleChangeLanguageButton(ActionEvent event) {
        String currentLanguage = PreferencesUtil.getPreference(LANGUAGE, ENGLISH);
        String newLanguage = currentLanguage.equals(ENGLISH) ? Language.HUNGARIAN.getValue() : Language.ENGLISH.getValue();
        PreferencesUtil.setPreference(LANGUAGE, newLanguage);

        // Reload the resource bundle to reflect the new language
        ResourceBundle.clearCache();
        getBundle();

        // Update the UI texts
        updateTexts();

        // Update the table column headers
        updateTableColumns();

        // Update table column 'Status' values
        updateTableValues();
    }

    private void updateTexts() {
        setMultilingualElement(banUserButton, BAN);
        setMultilingualElement(unbanUserButton, UNBAN);
        setMultilingualElement(changeLanguageButton, CHANGE_LANGUAGE);
        setMultilingualElement(logoutButton, LOGOUT);
    }

    private void updateTableColumns() {
        setMultilingualElement(usernameColumn, USERNAME);
        setMultilingualElement(statusColumn, STATUS);
    }

    private void updateTableValues() {
        statusColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    switch (item.toLowerCase()) {
                        case ACTIVE -> setText(getBundle().getString(ACTIVE));
                        case BANNED -> setText(getBundle().getString(BANNED));
                        default -> setText(item);
                    }
                }
            }
        });
    }
}

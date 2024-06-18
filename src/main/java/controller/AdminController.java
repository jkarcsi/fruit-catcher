package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.user.User;
import model.user.UserDAO;
import utils.LoggerUtil;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static utils.FXMLPaths.LOGIN;

public class AdminController extends BaseController {

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> statusColumn;

    private ObservableList<User> usersList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        super.initialize(url, resourceBundle);
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
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
    private void handleBanUserButton(ActionEvent event) {
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
    private void handleUnbanUserButton(ActionEvent event) {
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
}

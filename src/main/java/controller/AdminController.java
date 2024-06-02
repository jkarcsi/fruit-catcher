package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.User;
import model.UserDAO;
import utils.UserSession;

import java.io.IOException;
import java.sql.SQLException;

public class AdminController {

    @FXML
    private TableView<User> usersTable;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> statusColumn;

    private ObservableList<User> usersList;
    private String adminUsername = UserSession.getInstance().getUsername();

    @FXML
    public void initialize() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        usersList = FXCollections.observableArrayList();
        loadUsers();
    }

    private void loadUsers() {
        try {
            UserDAO userDAO = new UserDAO();
            usersList.setAll(userDAO.getAllUsers());
            usersTable.setItems(usersList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBanUserButton(ActionEvent event) {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null && !selectedUser.getUsername().equals(adminUsername)) {
            try {
                UserDAO userDAO = new UserDAO();
                userDAO.banUser(selectedUser.getUsername());
                loadUsers(); // Refresh the users list
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            // Show error message if the admin tries to ban themselves
            System.out.println("Admin cannot ban themselves.");
        }
    }

    @FXML
    private void handleUnbanUserButton(ActionEvent event) {
        User selectedUser = usersTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try {
                UserDAO userDAO = new UserDAO();
                userDAO.unbanUser(selectedUser.getUsername());
                loadUsers(); // Refresh the users list
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleLogoutButton(ActionEvent event) {
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
}

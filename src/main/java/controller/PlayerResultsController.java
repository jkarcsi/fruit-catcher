package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.database.Database;
import model.Score;
import utils.UserSession;

public class PlayerResultsController extends BaseController {

    @FXML
    private TableView<Score> resultsTable;

    @FXML
    private TableColumn<Score, Integer> scoreColumn;

    @FXML
    private TableColumn<Score, String> dateColumn;

    private String username = UserSession.getInstance().getUsername();

    @FXML
    public void initialize() {
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        loadScores();
    }

    private void loadScores() {
        try {
            resultsTable.setItems(getTopScores(username));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToMainMenuButton(ActionEvent event) {
        navigateTo("/fruitcatchgame/view/mainMenu.fxml", event);
    }

    private ObservableList<Score> getTopScores(String username) throws SQLException {
        List<Score> scoreList = new ArrayList<>();
        String query = "SELECT score, timestamp FROM scores WHERE username = ? ORDER BY score DESC LIMIT 10";
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int score = resultSet.getInt("score");
                String date = resultSet.getString("timestamp");
                scoreList.add(new Score(username, score, date));
            }
        }
        return FXCollections.observableArrayList(scoreList);
    }
}

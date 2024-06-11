package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import model.database.Database;
import model.Score;
import utils.SceneConstants;

import static utils.FXMLPaths.MAIN_MENU;
import static utils.SceneConstants.SCORE;
import static utils.SceneConstants.TIMER;
import static utils.SceneConstants.TIMESTAMP;

public class PlayerResultsController extends BaseController implements Initializable {

    @FXML
    public Label topScores;
    @FXML
    private TableView<Score> resultsTable;

    @FXML
    private TableColumn<Score, Integer> scoreColumn;

    @FXML
    private TableColumn<Score, String> dateColumn;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        setMultilingualElement(topScores, SceneConstants.TOP_SCORES);
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>(SCORE));
        setMultilingualElement(scoreColumn, SCORE);
        dateColumn.setCellValueFactory(new PropertyValueFactory<>(TIMESTAMP));
        setMultilingualElement(dateColumn, TIMER);
        loadScores();
    }

    private void loadScores() {
        try {
            resultsTable.setItems(getTopScores(getUsername()));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToMainMenuButton(ActionEvent event) {
        navigateTo(MAIN_MENU, event);
    }

    private ObservableList<Score> getTopScores(String username) throws SQLException {
        List<Score> scoreList = new ArrayList<>();
        String query = "SELECT score, timestamp FROM scores WHERE username = ? ORDER BY score DESC LIMIT 10";
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int score = resultSet.getInt(SCORE);
                String date = resultSet.getString(TIMESTAMP);
                scoreList.add(new Score(username, score, date));
            }
        }
        return FXCollections.observableArrayList(scoreList);
    }
}

package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
import model.ranking.Ranking;

import static utils.FXMLPaths.MAIN_MENU;
import static utils.SceneConstants.SCORE;
import static utils.SceneConstants.USERNAME;

public class PlayerRankingsController extends BaseController implements Initializable {


    @FXML
    private TableView<Ranking> rankingsTable;

    @FXML
    private TableColumn<Ranking, String> usernameColumn;

    @FXML
    private TableColumn<Ranking, Integer> scoreColumn;

    @FXML
    public void initialize(URL location, ResourceBundle resources) {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>(setMultilingualElement(USERNAME)));
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>(setMultilingualElement(SCORE)));

        try {
            rankingsTable.setItems(getTopPlayers());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToMainMenuButton(ActionEvent event) {
        navigateTo(MAIN_MENU, event);
    }

    private ObservableList<Ranking> getTopPlayers() throws SQLException {
        List<Ranking> rankingList = new ArrayList<>();
        String query = "SELECT username, SUM(score) AS total_score FROM scores GROUP BY username ORDER BY total_score DESC LIMIT 10";
        Connection connection = Database.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String username = resultSet.getString(USERNAME);
                int totalScore = resultSet.getInt("total_score");
                rankingList.add(new Ranking(username, totalScore));
            }
        }
        return FXCollections.observableArrayList(rankingList);
    }
}

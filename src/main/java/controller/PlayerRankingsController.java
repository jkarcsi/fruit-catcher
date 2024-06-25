package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import model.score.Ranking;
import model.user.UserDAO;
import util.SceneConstants;

import static util.FXMLPaths.MAIN_MENU;
import static util.SceneConstants.BACK_TO_MAIN_MENU;
import static util.SceneConstants.SCORE;
import static util.SceneConstants.USERNAME;

public class PlayerRankingsController extends BaseController implements Initializable {

    @FXML
    public Label rankings;

    @FXML
    private TableView<Ranking> rankingsTable;

    @FXML
    private TableColumn<Ranking, String> usernameColumn;

    @FXML
    private TableColumn<Ranking, Integer> scoreColumn;

    @FXML
    private Button backToMainMenuButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        UserDAO userDAO = new UserDAO();
        setMultilingualElement(backToMainMenuButton, BACK_TO_MAIN_MENU);
        setMultilingualElement(rankings, SceneConstants.PLAYER_RANKINGS);
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>(USERNAME));
        setMultilingualElement(usernameColumn, USERNAME);
        scoreColumn.setCellValueFactory(new PropertyValueFactory<>(SCORE));
        setMultilingualElement(scoreColumn, SCORE);

        try {
            rankingsTable.setItems(userDAO.getTopPlayers());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToMainMenuButton(ActionEvent event) {
        navigateTo(MAIN_MENU, event);
    }


}

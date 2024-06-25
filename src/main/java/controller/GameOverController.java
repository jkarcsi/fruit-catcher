package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

import static util.FXMLPaths.GAME;
import static util.FXMLPaths.MAIN_MENU;
import static util.SceneConstants.BACK_TO_MAIN_MENU;
import static util.SceneConstants.NEW_GAME;
import static util.SceneConstants.YOUR_SCORE;

public class GameOverController extends BaseController implements Initializable {

    @FXML
    private Label scoreLabel;

    @FXML
    private Button backToMainMenuButton;

    @FXML
    private Button newGameButton;

    private int score;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setMultilingualElement(backToMainMenuButton, BACK_TO_MAIN_MENU);
        setMultilingualElement(newGameButton, NEW_GAME);
    }

    public void setScore(int score) {
        this.score = score;
        showScore();
    }

    private void showScore() {
        setMultilingualElement(scoreLabel, YOUR_SCORE);
        scoreLabel.setText(scoreLabel.getText() + score);
    }

    @FXML
    private void handleNewGameButton(ActionEvent event) {
        navigateTo(GAME, event);
    }

    @FXML
    private void handleMainMenuButton(ActionEvent event) {
        navigateTo(MAIN_MENU, event);
    }
}

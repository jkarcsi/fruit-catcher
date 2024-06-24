package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import static util.FXMLPaths.GAME;
import static util.FXMLPaths.MAIN_MENU;

public class GameOverController extends BaseController {

    @FXML
    private Label scoreLabel;

    private int score;

    public void setScore(int score) {
        this.score = score;
        showScore();
    }

    private void showScore() {
        scoreLabel.setText("Your Score: " + score);
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

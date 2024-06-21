package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

import static utils.FXMLPaths.MAIN_MENU;
import static utils.SceneConstants.BACK_TO_MAIN_MENU;
import static utils.SceneConstants.DESC_FIRST;
import static utils.SceneConstants.DESC_SECOND;
import static utils.SceneConstants.DESC_THIRD;

public class DescriptionController extends BaseController implements Initializable {

    @FXML
    private Label descFirst;

    @FXML
    private Label descSecond;

    @FXML
    private Label descThird;

    @FXML
    private Button backToMainMenu;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setMultilingualElement(descFirst, DESC_FIRST);
        setMultilingualElement(descSecond, DESC_SECOND);
        setMultilingualElement(descThird, DESC_THIRD);
        setMultilingualElement(backToMainMenu, BACK_TO_MAIN_MENU);
    }

    @FXML
    private void handleBackToMainMenuButton(ActionEvent event) {
        navigateTo(MAIN_MENU, event);
    }
}

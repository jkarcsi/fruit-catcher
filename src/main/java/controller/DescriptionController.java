package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

import static utils.FXMLPaths.MAIN_MENU;

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
        setMultilingualElement(descFirst, "descFirst");
        setMultilingualElement(descSecond, "descSecond");
        setMultilingualElement(descThird, "descThird");
        setMultilingualElement(backToMainMenu, "backToMainMenu");
    }

    @FXML
    private void handleBackToMainMenuButton(ActionEvent event) {
        navigateTo(MAIN_MENU, event);
    }
}

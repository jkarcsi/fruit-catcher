package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import utils.PreferencesUtil;
import utils.UserSession;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

import static utils.FXMLPaths.MAIN_MENU;
import static utils.SceneConstants.ENGLISH;
import static utils.SceneConstants.LANGUAGE;

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

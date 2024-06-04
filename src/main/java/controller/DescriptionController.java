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

public class DescriptionController extends BaseController implements Initializable {

    @FXML
    private Label descFirst;

    @FXML
    private Label descSecond;

    @FXML
    private Label descThird;

    @FXML
    private Button backToMainMenu;

    private ResourceBundle bundle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String language = PreferencesUtil.getPreference(UserSession.getInstance().getUsername(), "language", "en");
        Locale locale = new Locale(language);
        bundle = ResourceBundle.getBundle("messages", locale);
        updateTexts();
    }

    private void updateTexts() {
        descFirst.setText(bundle.getString("descFirst"));
        descSecond.setText(bundle.getString("descSecond"));
        descThird.setText(bundle.getString("descThird"));
        backToMainMenu.setText(bundle.getString("backToMainMenu"));
    }


    @FXML
    private void handleBackToMainMenuButton(ActionEvent event) {
        navigateTo(MAIN_MENU, event);
    }
}

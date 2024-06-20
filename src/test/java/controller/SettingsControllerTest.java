package controller;

import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.ListResourceBundle;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@ExtendWith(ApplicationExtension.class)
public class SettingsControllerTest {

    @InjectMocks
    private SettingsController settingsController;

    private ResourceBundle resourceBundleEn;
    private ResourceBundle resourceBundleHu;

    static class TestResourceBundleEn extends ListResourceBundle {
        @Override
        protected Object[][] getContents() {
            return new Object[][] {
                    {"gameMode.normal", "Normal"},
                    {"gameMode.freeplay", "Freeplay"},
                    {"gameMode.playground", "Playground"},
                    {"difficulty.easy", "Easy"},
                    {"difficulty.medium", "Medium"},
                    {"difficulty.hard", "Hard"},
                    {"texture.forest", "Forest"},
                    {"texture.retro", "Retro"},
                    {"texture.futuristic", "Futuristic"},
                    {"language.english", "English"},
                    {"language.hungarian", "Hungarian"},
            };
        }
    }

    static class TestResourceBundleHu extends ListResourceBundle {
        @Override
        protected Object[][] getContents() {
            return new Object[][] {
                    {"gameMode.normal", "Normál"},
                    {"gameMode.freeplay", "Szabadjáték"},
                    {"gameMode.playground", "Játszótér"},
                    {"difficulty.easy", "Könnyű"},
                    {"difficulty.medium", "Közepes"},
                    {"difficulty.hard", "Nehéz"},
                    {"texture.forest", "Erdö"},
                    {"texture.retro", "Retró"},
                    {"texture.futuristic", "Futurisztikus"},
                    {"language.english", "Angol"},
                    {"language.hungarian", "Magyar"},
            };
        }
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        resourceBundleEn = new TestResourceBundleEn();
        resourceBundleHu = new TestResourceBundleHu();
    }

    @Start
    public void start(Stage stage) {
        settingsController = spy(new SettingsController());

        settingsController.gameModeComboBox = new ComboBox<>(FXCollections.observableArrayList());
        settingsController.difficultyComboBox = new ComboBox<>(FXCollections.observableArrayList());
        settingsController.textureComboBox = new ComboBox<>(FXCollections.observableArrayList());
        settingsController.languageComboBox = new ComboBox<>(FXCollections.observableArrayList());
        settingsController.leftKeyComboBox = new ComboBox<>(FXCollections.observableArrayList());
        settingsController.rightKeyComboBox = new ComboBox<>(FXCollections.observableArrayList());
        settingsController.logFilePathTextField = new TextField();

        settingsController.gameModeLabel = new Label();
        settingsController.difficultyLabel = new Label();
        settingsController.textureLabel = new Label();
        settingsController.logFilePathLabel = new Label();
        settingsController.languageLabel = new Label();
        settingsController.leftKeyLabel = new Label();
        settingsController.rightKeyLabel = new Label();
        settingsController.saveButton = new Button();
        settingsController.backToMainMenuButton = new Button();
        settingsController.chooseDirectoryButton = new Button();

        // Initialize the controller
        settingsController.initialize(null, resourceBundleEn);
    }

    @Test
    public void testUpdateComboBoxDisplay_English() {
        doReturn(resourceBundleEn).when(settingsController).getBundle();
        settingsController.updateComboBoxDisplay();
        assertTrue(settingsController.gameModeComboBox.getItems().contains("Normal"));
    }

    @Test
    public void testUpdateComboBoxDisplay_Hungarian() {
        doReturn(resourceBundleHu).when(settingsController).getBundle();
        settingsController.updateComboBoxDisplay();
        assertTrue(settingsController.gameModeComboBox.getItems().contains("Normál"));
    }
}

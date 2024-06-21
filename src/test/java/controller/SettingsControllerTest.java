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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

@ExtendWith(ApplicationExtension.class)
class SettingsControllerTest {

    @InjectMocks
    private SettingsController settingsController;

    private ResourceBundle resourceBundleEn;
    private ResourceBundle resourceBundleHu;

    static class TestResourceBundleEnglish extends ListResourceBundle {
        @Override
        protected Object[][] getContents() {
            return new Object[][] {
                    {"gameMode", "Game Mode"},
                    {"gameMode.normal", "Normal"},
                    {"gameMode.freeplay", "Freeplay"},
                    {"gameMode.playground", "Playground"},
                    {"difficulty", "Difficulty"},
                    {"difficulty.easy", "Easy"},
                    {"difficulty.medium", "Medium"},
                    {"difficulty.hard", "Hard"},
                    {"logFilePath", "Log File Path"},
                    {"texture", "Texture"},
                    {"texture.forest", "Forest"},
                    {"texture.retro", "Retro"},
                    {"texture.futuristic", "Futuristic"},
                    {"language", "Language"},
                    {"language.english", "English"},
                    {"language.hungarian", "Hungarian"},
                    {"leftKey", "Left Key"},
                    {"rightKey", "Right Key"},
                    {"save", "Right Key"},
                    {"chooseDirectory", "Right Key"},
                    {"backToMainMenu", "Right Key"}
            };
        }
    }

    static class TestResourceBundleHungarian extends ListResourceBundle {
        @Override
        protected Object[][] getContents() {
            return new Object[][] {
                    {"gameMode", "Játék mód"},
                    {"gameMode.normal", "Normál"},
                    {"gameMode.freeplay", "Szabadjáték"},
                    {"gameMode.playground", "Játszótér"},
                    {"difficulty", "Nehézség"},
                    {"difficulty.easy", "Könnyű"},
                    {"difficulty.medium", "Közepes"},
                    {"difficulty.hard", "Nehéz"},
                    {"logFilePath", "Log File Elérése"},
                    {"texture", "Textúra"},
                    {"texture.forest", "Erdö"},
                    {"texture.retro", "Retró"},
                    {"texture.futuristic", "Futurisztikus"},
                    {"language", "Nyelv"},
                    {"language.english", "Angol"},
                    {"language.hungarian", "Magyar"},
                    {"leftKey", "Bal irány"},
                    {"rightKey", "Jobb irány"},
                    {"save", "Jobb irány"},
                    {"chooseDirectory", "Jobb irány"},
                    {"backToMainMenu", "Jobb irány"}
            };
        }
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        resourceBundleEn = new TestResourceBundleEnglish();
        resourceBundleHu = new TestResourceBundleHungarian();
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
    void testUpdateComboBoxDisplay_English() {
        doReturn(resourceBundleEn).when(settingsController).getBundle();
        settingsController.switchLanguageAndUpdateComboBoxDisplay("English");
        assertTrue(settingsController.gameModeComboBox.getItems().contains("Normal"));
    }

    @Test
    void testUpdateComboBoxDisplay_Hungarian() {
        doReturn(resourceBundleHu).when(settingsController).getBundle();
        settingsController.switchLanguageAndUpdateComboBoxDisplay("Hungarian");
        assertTrue(settingsController.gameModeComboBox.getItems().contains("Normál"));
    }

    @Test
    void testSwitchLanguageFromEnglishToHungarianTwo() {
        // Set initial language to English
        doReturn(resourceBundleEn).when(settingsController).getBundle();
        settingsController.switchLanguageAndUpdateComboBoxDisplay("english");
        settingsController.languageComboBox.setValue("English");
        settingsController.textureComboBox.setValue("Forest");

        // Switch to Hungarian
        doReturn(resourceBundleHu).when(settingsController).getBundle();
        settingsController.languageComboBox.setValue("Magyar");
        settingsController.switchLanguageAndUpdateComboBoxDisplay("hungarian");

        // Verify the items and selected value are updated correctly
        assertTrue(settingsController.textureComboBox.getItems().contains("Erdö"));
        assertEquals("Erdö", settingsController.textureComboBox.getValue());
        assertEquals("Magyar", settingsController.languageComboBox.getValue());
    }
}

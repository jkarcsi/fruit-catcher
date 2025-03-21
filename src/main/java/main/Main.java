package main;

import exception.ConfigException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.database.DatabaseInitializer;

import java.sql.SQLException;
import java.util.Objects;

import static util.FXMLPaths.START;
import static util.ResourcePaths.IMAGE_ICON_PNG;
import static util.SceneConstants.FRUIT_CATCHER;

public class Main extends Application {
        @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(START));
        Scene scene = new Scene(loader.load(), 800, 600);

        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream(IMAGE_ICON_PNG))));
        primaryStage.setScene(scene);
        primaryStage.setTitle(FRUIT_CATCHER);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        try {
            DatabaseInitializer.initializeDatabase();
        } catch (SQLException e) {
            throw new ConfigException("Error while reaching database.", e);
        }
        launch(args);
    }
}

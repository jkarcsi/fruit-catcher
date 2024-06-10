package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import model.database.DatabaseInitializer;

import java.sql.SQLException;
import java.util.Objects;

import static utils.FXMLPaths.START;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(START));
        Scene scene = new Scene(loader.load(), 800, 600);
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/image/icon.png"))));
        primaryStage.setScene(scene);
        primaryStage.setTitle("Fruit Catcher");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        try {
            DatabaseInitializer.initializeDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        launch(args);
    }
}

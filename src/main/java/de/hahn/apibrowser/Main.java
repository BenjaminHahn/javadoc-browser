package de.hahn.apibrowser;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main extends Application {
    private static final Logger log = LogManager.getLogger(Main.class.getName());

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("splitview.fxml"));

        primaryStage.setTitle("API Browser");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        scene.focusOwnerProperty().addListener((observable, oldValue, newValue) -> {
            log.trace("focus change" +  newValue);
        });

        scene.getStylesheets().add("style.css");
        primaryStage.getIcons().add(new Image("icon.png"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}

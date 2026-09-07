package com.example.personalityquest.Applications;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static com.example.personalityquest.MainApplication.fxmlPrefix;

public class SettingsApplication {
    public static void launch(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                SettingsApplication.class.getResource(fxmlPrefix + "Settings.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1180, 760);
        stage.setTitle("Settings");
        stage.setMinWidth(680);
        stage.setMinHeight(520);
        stage.setScene(scene);
        stage.show();
    }
}

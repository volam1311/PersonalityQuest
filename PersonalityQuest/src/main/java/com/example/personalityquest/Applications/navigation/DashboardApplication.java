package com.example.personalityquest.Applications.navigation;

import com.example.personalityquest.AppFonts;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/** Launches the dashboard screen */
public class DashboardApplication extends Application {
    /** Loads the dashboard screen into the primary stage
     * @param stage the primary JavaFX stage
     * @throws IOException if the screen resource cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        AppFonts.load();
        stage.setTitle("Dashboard");
        stage.setMinWidth(680);
        stage.setMinHeight(520);
        stage.show();

    }
}

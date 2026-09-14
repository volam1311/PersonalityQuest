package com.example.personalityquest.Applications;

import com.example.personalityquest.AppFonts;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        AppFonts.load();
        stage.setTitle("Dashboard");
        stage.setMinWidth(680);
        stage.setMinHeight(520);
        stage.show();

    }
}

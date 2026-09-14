package com.example.personalityquest.Applications.navigation;

import com.example.personalityquest.AppFonts;
import com.example.personalityquest.ScreenEnum;
import com.example.personalityquest.Services.navigation.NavigationService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SettingsApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        AppFonts.load();

        NavigationService.LoadScreen(ScreenEnum.SETTINGS);
        stage.setTitle("Settings");
        stage.setMinWidth(680);
        stage.setMinHeight(520);
    }
}

package com.example.personalityquest.Applications.profile;

import com.example.personalityquest.AppFonts;
import com.example.personalityquest.ScreenEnum;
import com.example.personalityquest.Services.navigation.NavigationService;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class ProfileApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        AppFonts.load();
        NavigationService.Init(stage);
        NavigationService.LoadScreen(ScreenEnum.PROFILE);
        stage.setMinWidth(680);
        stage.setMinHeight(520);
    }
}

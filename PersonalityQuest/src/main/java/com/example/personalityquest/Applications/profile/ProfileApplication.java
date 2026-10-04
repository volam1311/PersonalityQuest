package com.example.personalityquest.Applications.profile;

import com.example.personalityquest.AppFonts;
import com.example.personalityquest.ScreenEnum;
import com.example.personalityquest.Services.navigation.NavigationService;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

/** Launches the profile screen */
public class ProfileApplication extends Application {
    /** Loads the profile screen into the primary stage
     * @param stage the primary JavaFX stage
     * @throws IOException if the screen resource cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        AppFonts.load();
        NavigationService.Init(stage);
        NavigationService.LoadScreen(ScreenEnum.PROFILE);
        stage.setMinWidth(680);
        stage.setMinHeight(520);
    }
}

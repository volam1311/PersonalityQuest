package com.example.personalityquest.Applications.quest;

import com.example.personalityquest.Model.quest.WeeklyTask;
import com.example.personalityquest.ScreenEnum;
import com.example.personalityquest.Services.navigation.NavigationService;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

/** Launches the weekly-reflection screen */
public class WeeklyTaskReflectionApplication extends Application {
    /** Loads the weekly-reflection screen into the primary stage
     * @param stage the primary JavaFX stage
     * @throws IOException if the screen resource cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        NavigationService.LoadScreen(ScreenEnum.TASKS);
    }
}

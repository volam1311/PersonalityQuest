package com.example.personalityquest.Applications.quest;

import com.example.personalityquest.Model.quest.WeeklyTask;
import com.example.personalityquest.ScreenEnum;
import com.example.personalityquest.Services.navigation.NavigationService;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class WeeklyTaskReflectionApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        NavigationService.LoadScreen(ScreenEnum.TASKS);
    }
}

package com.example.personalityquest.Applications;

import com.example.personalityquest.Model.WeeklyTask;
import com.example.personalityquest.ScreenEnum;
import com.example.personalityquest.Services.NavigationService;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class WeeklyTaskReflectionApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        NavigationService.LoadScreen(ScreenEnum.TASKS);
    }
}

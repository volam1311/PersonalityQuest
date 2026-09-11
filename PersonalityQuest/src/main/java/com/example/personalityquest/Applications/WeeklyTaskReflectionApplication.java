package com.example.personalityquest.Applications;

import com.example.personalityquest.Model.WeeklyTask;
import javafx.stage.Stage;

import java.io.IOException;

public class WeeklyTaskReflectionApplication {
    public static void launch(Stage stage, WeeklyTask taskLookingAt) throws IOException {
        TasksApplication.launch(stage, taskLookingAt);
    }
}

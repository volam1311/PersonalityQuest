package com.example.personalityquest.Applications;

import com.example.personalityquest.AppFonts;
import com.example.personalityquest.Controllers.TasksController;
import com.example.personalityquest.Model.WeeklyTask;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static com.example.personalityquest.ApplicationManager.SceneInfo.SCENEHEIGHT;
import static com.example.personalityquest.ApplicationManager.SceneInfo.SCENEWIDTH;

public class TasksApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        AppFonts.load();

        stage.setTitle("Tasks");
        stage.setMinWidth(680);
        stage.setMinHeight(520);
        stage.show();
    }
}

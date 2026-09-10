package com.example.personalityquest.Applications;

import com.example.personalityquest.Controllers.WeeklyTaskReflectionController;
import com.example.personalityquest.Model.WeeklyTask;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static com.example.personalityquest.MainApplication.fxmlPrefix;
import static com.example.personalityquest.ApplicationManager.SceneInfo.SCENEHEIGHT;
import static com.example.personalityquest.ApplicationManager.SceneInfo.SCENEWIDTH;

public class WeeklyTaskReflectionApplication {
    public static void launch(Stage stage, WeeklyTask taskLoookingAt) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WeeklyTaskReflectionApplication.class.getResource(fxmlPrefix + "WeeklyTaskReflection.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), SCENEWIDTH, SCENEHEIGHT);
        stage.setTitle("Task Reflection");

        // Set Task
        WeeklyTaskReflectionController controller = fxmlLoader.getController();
        controller.setTask(taskLoookingAt);

        stage.setScene(scene);
        stage.show();
    }
}

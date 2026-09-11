package com.example.personalityquest.Applications;

import com.example.personalityquest.AppFonts;
import com.example.personalityquest.Controllers.TasksController;
import com.example.personalityquest.Model.WeeklyTask;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static com.example.personalityquest.ApplicationManager.SceneInfo.SCENEHEIGHT;
import static com.example.personalityquest.ApplicationManager.SceneInfo.SCENEWIDTH;
import static com.example.personalityquest.MainApplication.fxmlPrefix;

public class TasksApplication {
    public static void launch(Stage stage) throws IOException {
        launch(stage, null);
    }

    public static void launch(Stage stage, WeeklyTask selectedTask) throws IOException {
        AppFonts.load();
        FXMLLoader fxmlLoader = new FXMLLoader(
                TasksApplication.class.getResource(fxmlPrefix + "Tasks.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), SCENEWIDTH, SCENEHEIGHT);
        TasksController controller = fxmlLoader.getController();
        if (selectedTask != null) {
            controller.selectTask(selectedTask);
        }

        stage.setTitle("Tasks");
        stage.setMinWidth(680);
        stage.setMinHeight(520);
        stage.setScene(scene);
        stage.show();
    }
}

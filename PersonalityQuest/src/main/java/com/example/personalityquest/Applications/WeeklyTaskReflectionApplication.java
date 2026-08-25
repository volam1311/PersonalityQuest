package com.example.personalityquest.Applications;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static com.example.personalityquest.MainApplication.fxmlPrefix;
import static com.example.personalityquest.Managers.SystemManager.SceneInfo.SCENEHEIGHT;
import static com.example.personalityquest.Managers.SystemManager.SceneInfo.SCENEWIDTH;

public class WeeklyTaskReflectionApplication {
    public static void launch(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(WeeklyTaskReflectionApplication.class.getResource(fxmlPrefix + "WeeklyTaskReflection.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), SCENEWIDTH, SCENEHEIGHT);
        stage.setTitle("Update Details");
        stage.setScene(scene);
        stage.show();
    }
}

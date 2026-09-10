package com.example.personalityquest.Applications;

import com.example.personalityquest.AppFonts;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static com.example.personalityquest.ApplicationManager.SceneInfo.SCENEHEIGHT;
import static com.example.personalityquest.ApplicationManager.SceneInfo.SCENEWIDTH;
import static com.example.personalityquest.MainApplication.fxmlPrefix;

public class QuestApplication {
    public static void launch(Stage stage) throws IOException {
        AppFonts.load();
        FXMLLoader fxmlLoader = new FXMLLoader(
                QuestApplication.class.getResource(fxmlPrefix + "Quest.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), SCENEWIDTH, SCENEHEIGHT);
        stage.setTitle("Questline");
        stage.setMinWidth(680);
        stage.setMinHeight(520);
        stage.setScene(scene);
        stage.show();
    }
}

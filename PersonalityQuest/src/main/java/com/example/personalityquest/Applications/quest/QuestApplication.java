package com.example.personalityquest.Applications.quest;

import com.example.personalityquest.AppFonts;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static com.example.personalityquest.ApplicationManager.SceneInfo.SCENEHEIGHT;
import static com.example.personalityquest.ApplicationManager.SceneInfo.SCENEWIDTH;

/** Launches the quest screen */
public class QuestApplication extends Application {
    /** Loads the quest screen into the primary stage
     * @param stage the primary JavaFX stage
     * @throws IOException if the screen resource cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        AppFonts.load();
        stage.setTitle("Questline");
        stage.setMinWidth(680);
        stage.setMinHeight(520);
        stage.show();
    }
}

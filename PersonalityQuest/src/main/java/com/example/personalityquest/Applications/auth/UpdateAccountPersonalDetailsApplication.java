package com.example.personalityquest.Applications.auth;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static com.example.personalityquest.ApplicationManager.SceneInfo.SCENEHEIGHT;
import static com.example.personalityquest.ApplicationManager.SceneInfo.SCENEWIDTH;

/** Opens the account personal-details screen */
public class UpdateAccountPersonalDetailsApplication {
    /** Opens the personal-details screen in the supplied stage
     * @param stage the stage that displays the screen
     * @throws IOException if the screen resource cannot be loaded
     */
    public static void launch(Stage stage) throws IOException {
        stage.setTitle("Update Details");
        stage.show();
    }
}

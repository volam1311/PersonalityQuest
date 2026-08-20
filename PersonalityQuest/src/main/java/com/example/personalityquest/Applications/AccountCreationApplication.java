package com.example.personalityquest.Applications;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static com.example.personalityquest.MainApplication.fxmlPrefix;
import static com.example.personalityquest.SystemManager.SceneInfo.SCENEHEIGHT;
import static com.example.personalityquest.SystemManager.SceneInfo.SCENEWIDTH;

public class AccountCreationApplication{
    public static void launch(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AccountCreationApplication.class.getResource(fxmlPrefix + "AccountCreation.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), SCENEWIDTH, SCENEHEIGHT);
        stage.setTitle("Account Creation");
        stage.setScene(scene);
        stage.show();
    }
}

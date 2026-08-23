package com.example.personalityquest.Applications;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static com.example.personalityquest.MainApplication.fxmlPrefix;
import static com.example.personalityquest.Managers.SystemManager.SceneInfo.SCENEHEIGHT;
import static com.example.personalityquest.Managers.SystemManager.SceneInfo.SCENEWIDTH;

public class DashboardApplication {
    public static void launch(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AccountCreationApplication.class.getResource(fxmlPrefix +  "Dashboard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), SCENEWIDTH, SCENEHEIGHT);
        stage.setTitle("Dashboard");
        stage.setScene(scene);
        stage.show();

    }
}

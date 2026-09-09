package com.example.personalityquest.Applications;

import com.example.personalityquest.AppFonts;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static com.example.personalityquest.MainApplication.fxmlPrefix;
import static com.example.personalityquest.Managers.SystemManager.SceneInfo.SCENEHEIGHT;
import static com.example.personalityquest.Managers.SystemManager.SceneInfo.SCENEWIDTH;

public class DashboardApplication {
    public static void launch(Stage stage) throws IOException {
        AppFonts.load();
        FXMLLoader fxmlLoader = new FXMLLoader(AccountCreationApplication.class.getResource(fxmlPrefix +  "Dashboard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1100, 720);
        stage.setTitle("Dashboard");
        stage.setMinWidth(680);
        stage.setMinHeight(520);
        stage.setScene(scene);
        stage.show();

    }
}

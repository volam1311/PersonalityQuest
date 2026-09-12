package com.example.personalityquest.Services;

import com.example.personalityquest.Applications.UpdateAccountPersonalDetailsApplication;
import com.example.personalityquest.ScreenEnum;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static com.example.personalityquest.ApplicationManager.SceneInfo.SCENEHEIGHT;
import static com.example.personalityquest.ApplicationManager.SceneInfo.SCENEWIDTH;

public class NavigationService {
    public final static String fxmlPrefix = "/com/example/personalityquest/";

    public static Stage stage;

    public static void Init(Stage currentStage){
        stage = currentStage;
    }

    public static void LoadScreen(ScreenEnum screen) throws IOException {
        String fxmlString = SetFxmlDoc(screen);

        FXMLLoader fxmlLoader = new FXMLLoader(UpdateAccountPersonalDetailsApplication.class.getResource(fxmlString));
        Scene scene = new Scene(fxmlLoader.load(), SCENEWIDTH, SCENEHEIGHT);
        stage.setTitle("Update Details");
        stage.setScene(scene);
        stage.show();
    }

    public static String SetFxmlDoc(ScreenEnum screenEnum){
        return fxmlPrefix + switch (screenEnum){
            case ScreenEnum.ACCOUNT_CREATION -> "AccountCreation.fxml";
            case ScreenEnum.ACCOUNT_SIGN_IN -> "AccountSignIn.fxml";
            case ScreenEnum.DASHBOARD -> "Dashboard.fxml";
            case ScreenEnum.QUESTS -> "Quest.fxml";
            case ScreenEnum.SETTINGS -> "Settings.fxml";
            case ScreenEnum.TASKS -> "Tasks.fxml";
            case ScreenEnum.UPDATE_ACCOUNT_PASSWORD -> "UpdateAccountPassword.fxml";
            case ScreenEnum.UPDATE_PERSONAL_DETAILS -> "UpdateAccountPersonalDetails.fxml";
            case ScreenEnum.WEEKLY_TASK_REFLECTION -> "abcd.fxml";
        };
    }
}

package com.example.personalityquest.Services.navigation;

import java.io.IOException;

import static com.example.personalityquest.ApplicationManager.SceneInfo.SCENEHEIGHT;
import static com.example.personalityquest.ApplicationManager.SceneInfo.SCENEWIDTH;
import com.example.personalityquest.Applications.auth.UpdateAccountPersonalDetailsApplication;
import com.example.personalityquest.ScreenEnum;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
        stage.setTitle(TitleFor(screen));
        stage.setScene(scene);
        stage.show();
    }

    public static String SetFxmlDoc(ScreenEnum screenEnum){
        return fxmlPrefix + switch (screenEnum){
            case ScreenEnum.ACCOUNT_CREATION -> "AccountCreation.fxml";
            case ScreenEnum.ACCOUNT_SIGN_IN -> "AccountSignIn.fxml";
            case ScreenEnum.DASHBOARD -> "Dashboard.fxml";
            case ScreenEnum.PROFILE -> "Profile.fxml";
            case ScreenEnum.ARCHETYPE -> "Archetype.fxml";
            case ScreenEnum.QUESTS -> "Quest.fxml";
            case ScreenEnum.QUEST_VIEWER ->  "QuestViewer.fxml";
            case ScreenEnum.QUIZ -> "Quiz.fxml";
            case ScreenEnum.QUIZ_RESULT -> "QuizResult.fxml";
            case ScreenEnum.SETTINGS -> "Settings.fxml";
            case ScreenEnum.TASKS -> "Tasks.fxml";
            case ScreenEnum.UPDATE_ACCOUNT_PASSWORD -> "UpdateAccountPassword.fxml";
            case ScreenEnum.UPDATE_PERSONAL_DETAILS -> "UpdateAccountPersonalDetails.fxml";
            case ScreenEnum.WEEKLY_TASK_REFLECTION -> "WeeklyTaskReflection.fxml";
        };
    }

    private static String TitleFor(ScreenEnum screen) {
        return switch (screen) {
            case ACCOUNT_CREATION -> "Create Account";
            case ACCOUNT_SIGN_IN -> "Account Login";
            case DASHBOARD -> "Dashboard";
            case PROFILE -> "Profile";
            case ARCHETYPE -> "Archetype";
            case QUESTS -> "Questline";
            case QUEST_VIEWER -> "Quest Viewer";
            case QUIZ -> "Archetype Quiz";
            case QUIZ_RESULT -> "Your Archetype";
            case SETTINGS -> "Settings";
            case TASKS -> "Journal";
            case UPDATE_ACCOUNT_PASSWORD -> "Update Password";
            case UPDATE_PERSONAL_DETAILS -> "Update Details";
            case WEEKLY_TASK_REFLECTION -> "Weekly Reflection";
        };
    }
}

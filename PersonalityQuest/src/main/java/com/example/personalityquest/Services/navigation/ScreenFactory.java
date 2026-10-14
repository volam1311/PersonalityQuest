package com.example.personalityquest.Services.navigation;

import com.example.personalityquest.ScreenEnum;

/**
 * Creates the FXML file name and window title for a screen.
 */
public final class ScreenFactory {
    /**
     * FXML resource name and window title for one screen.
     * @param fxmlFileName the file name under the application resource root
     * @param title the window title
     */
    public record ScreenDefinition(String fxmlFileName, String title) {
    }

    /**
     * @param screen the screen to describe
     * @return the FXML file and title used to open that screen
     */
    public ScreenDefinition Create(ScreenEnum screen) {
        return switch (screen) {
            case ACCOUNT_CREATION -> new ScreenDefinition("AccountCreation.fxml", "Create Account");
            case ACCOUNT_SIGN_IN -> new ScreenDefinition("AccountSignIn.fxml", "Account Login");
            case DASHBOARD -> new ScreenDefinition("Dashboard.fxml", "Dashboard");
            case PROFILE -> new ScreenDefinition("Profile.fxml", "Profile");
            case ARCHETYPE -> new ScreenDefinition("Archetype.fxml", "Archetype");
            case QUESTS -> new ScreenDefinition("Quest.fxml", "Questline");
            case QUEST_VIEWER -> new ScreenDefinition("QuestViewer.fxml", "Quest Viewer");
            case QUIZ -> new ScreenDefinition("Quiz.fxml", "Archetype Quiz");
            case QUIZ_RESULT -> new ScreenDefinition("QuizResult.fxml", "Your Archetype");
            case SETTINGS -> new ScreenDefinition("Settings.fxml", "Settings");
            case TASKS -> new ScreenDefinition("Tasks.fxml", "Journal");
            case UPDATE_ACCOUNT_PASSWORD -> new ScreenDefinition("UpdateAccountPassword.fxml", "Update Password");
            case UPDATE_PERSONAL_DETAILS -> new ScreenDefinition("UpdateAccountPersonalDetails.fxml", "Update Details");
            case WEEKLY_TASK_REFLECTION -> new ScreenDefinition("WeeklyTaskReflection.fxml", "Weekly Reflection");
        };
    }
}

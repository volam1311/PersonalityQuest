package com.example.personalityquest.Managers;

import com.example.personalityquest.DataClasses.Quest;
import com.example.personalityquest.DataClasses.UserQuest;
import jdk.jshell.spi.ExecutionControl;

public class UserQuestManager {
    public static UserQuest GetCurrentUserQuestForEmail(String email) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("GetCurrentUserQuestForEmail is not implemented");
    }
    public static UserQuest SetUserQuesStatusAsActive(UserQuest quest, String email) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("SetUserQuestAsActive is not implemented");
    }
    public static UserQuest InsertNewQuestForEmail(Quest quest, String email) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("InsertNewQuestForEmailis not implemented");
    }
    public static UserQuest InsertNewQuestForEmail(Quest[] quest, String email) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("InsertNewQuestForEmail is not implemented");
    }
    public static void SetUserQuestStatusAsComplete(UserQuest quest, String email) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("SetQuestToComplete is not implemented");
    }
    public static void SetUserQuestToPercentageComplete(UserQuest quest, String email, float percentage)throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("SetQuestToPercentageComplete Complete is not implemented");
    }


}

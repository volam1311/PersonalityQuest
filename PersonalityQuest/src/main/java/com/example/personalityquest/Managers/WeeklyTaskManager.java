package com.example.personalityquest.Managers;

public class WeeklyTaskManager {

    private final static String SQL_UPDATE_DRAFT = "UPDATE TaskCompletions" +
            " SET reflection = ?, SET status = "+ "Started" +
            " WHERE accountEmail = ? AND taskId = ?";

    private final static String SQL_FINISH_TASK = "UPDATE TaskCompletions" +
            " SET reflection = ?, SET status = " + "Finished" +
            " WHERE accountEmail = ? AND taskId = ?";


    public void UpdateGivenTaskToDraft(int taskId){

    }
}

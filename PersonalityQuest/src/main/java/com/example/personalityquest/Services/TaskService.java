package com.example.personalityquest.Services;

import com.example.personalityquest.DAO.TaskDAO;
import com.example.personalityquest.Model.Task;
import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.SQLite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskService {
    private final static int AMOUNT_OF_TASKS = ApplicationManager.TaskConfig.getAmountOfTasks();

    /**
     * Gets a Task from the database that matches the given id
     * @param taskId The taskId you of the Task you want to retrieve
     * @return A Task objecting containing the given taskId's information of the
     * taskId, name, description and labourId(QuestId)
     * @throws IllegalArgumentException If the given taskId is equal to 0
     * @throws Exception If there is a Database Access Failure OR user doesn't have separate tasks
     */
    public static Task GetTaskForId(int taskId) throws Exception {
        if (IsTaskIdNull(taskId)){
            throw new IllegalArgumentException("TaskId is null");
        }

        return TaskDAO.GetTaskForId(taskId);
    }


    /**
     * Gets an array of taskIds that link to a given labourId
     * @param labourId The labourId that you will get taskId's for
     * @return A populated Array of taskId's that have a matching labourId to the one given
     * @throws Exception If the labourId is null OR a Database Access Failure
     */
    public static int[] GetRandomAmountOfTaskIdsForLabourId(int labourId) throws Exception {
        if (IsLabourIdNull(labourId)){
            throw new IllegalArgumentException("Labour Id is null");
        }

        List<Integer> taskIds = TaskDAO.GetTaskIdsForLabourID(labourId);
        Collections.shuffle(taskIds);


        int[] returnedTaskIds = new int[AMOUNT_OF_TASKS];
        for (int i = 0; i < AMOUNT_OF_TASKS; i++){
            if (i == taskIds.size()) {
                System.out.println("Early returned");
                return returnedTaskIds;
            }
            returnedTaskIds[i] = taskIds.get(i);
        }
        return returnedTaskIds;
    }

    /// NULL CHECKING
    /**
     * Checks if the given labourId is equal to 0
     * @param labourId The labourId you want checked
     * @return Whether the labourId is equal to 0
     */
    private static boolean IsLabourIdNull(int labourId){
        return labourId == 0;
    }
    /**
     * Checks if the given taskId is equal to 0
     * @param taskId The taskId you want checked
     * @return Whether the taskId is equal to 0
     */
    private static boolean IsTaskIdNull(int taskId){
        return taskId == 0;
    }
}

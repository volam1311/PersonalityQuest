package com.example.personalityquest.Managers;

import com.example.personalityquest.DataClasses.Task;
import com.example.personalityquest.SQLite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TaskManager {
    private final static int AMOUNT_OF_TASKS = SystemManager.TaskConfig.getAmountOfTasks();

    private final static String FIND_TASK_INFO = """
            SELECT * FROM Tasks
            WHERE taskId = ?;
            """;

    private final static String GET_TASKS_FOR_LABOURID = """
            SELECT * FROM Tasks
            WHERE labourId = ?;
            """;

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
        // appends all task ids to FindTaskInfo Query and then
        // executes it find all task info
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(FIND_TASK_INFO);
        // assign parameters
        // assigns all task info to an array
        Task task = null;

        statement.setInt(1, taskId);
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            System.out.println("Makes new task");
            task = new Task(
                    rs.getInt("taskId"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getInt("labourId")
            );
        }


        if (task == null){
            throw new Exception("User does not have separate Tasks. Check Db To See Why.");
        }
        return task;
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
        // appends all task ids to FindTaskInfo Query and then
        // executes it find all task info
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_TASKS_FOR_LABOURID);
        // assign parameters
        // assigns all task info to an array
        List<Integer> taskIds = new ArrayList<Integer>();

        statement.setInt(1, labourId);
        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            taskIds.add(rs.getInt(1));
        }

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

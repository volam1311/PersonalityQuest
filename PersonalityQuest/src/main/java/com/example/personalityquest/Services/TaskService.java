package com.example.personalityquest.Services;

import com.example.personalityquest.DAO.TaskDAO;
import com.example.personalityquest.Model.Task;
import com.example.personalityquest.ApplicationManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TaskService {
    private final static int AMOUNT_OF_TASKS = ApplicationManager.TaskConfig.getAmountOfTasks();

    /**
     * Gets a Task from the database that matches the given id
     * @param taskId The taskId you of the Task you want to retrieve
     * @return A Task objecting containing the given taskId's information of the
     * taskId, name, description and labourId(QuestId) or Null if that taskID doesn't exist
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
     * Gets every Task that belongs to a labour.
     * @param labourId The labourId you want the tasks for
     * @return A list of Task objects, empty if none exist
     * @throws Exception If the labourId is invalid or a Database Access Failure occurs
     */
    public static List<Task> GetTasksForLabourId(int labourId) throws Exception {
        if (IsLabourIdNull(labourId)) {
            throw new IllegalArgumentException("Labour Id is null");
        }

        return TaskDAO.GetTasksForLabourId(labourId);
    }

    /**
     * Joins task names for compact UI labels.
     */
    public static String JoinTaskNames(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return "";
        }

        StringBuilder names = new StringBuilder();
        for (Task task : tasks) {
            if (!names.isEmpty()) {
                names.append(" · ");
            }
            names.append(task.getName());
        }
        return names.toString();
    }

    /**
     * Joins task names and descriptions for labour detail copy.
     */
    public static String JoinTaskDetails(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return "";
        }

        StringBuilder details = new StringBuilder();
        for (Task task : tasks) {
            if (!details.isEmpty()) {
                details.append("\n\n");
            }
            details.append(task.getName());
            if (!ApplicationManager.isEmpty(task.getDescription())) {
                details.append("\n").append(task.getDescription());
            }
        }
        return details.toString();
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
                return Arrays.copyOf(returnedTaskIds, i);
            }
            returnedTaskIds[i] = taskIds.get(i);
        }

        return returnedTaskIds;
    }

    /**
     * Gets the three task ids for a labour week. Week 1 is the first three tasks, week 2 the next three, and so on.
     * @param labourId The labour whose task pool to read
     * @param weekNumber The 1-based week of the labour
     * @return Task ids for that week, empty if none exist
     */
    public static int[] GetTaskIdsForLabourWeek(int labourId, int weekNumber) throws Exception {
        if (IsLabourIdNull(labourId)) {
            throw new IllegalArgumentException("Labour Id is null");
        }
        if (weekNumber <= 0) {
            throw new IllegalArgumentException("Week number is invalid");
        }

        List<Integer> taskIds = TaskDAO.GetTaskIdsForLabourID(labourId);
        if (taskIds.isEmpty()) {
            return new int[0];
        }

        int weeks = Math.max(1, (int) Math.ceil(taskIds.size() / (double) AMOUNT_OF_TASKS));
        int week = ((weekNumber - 1) % weeks) + 1;
        int start = (week - 1) * AMOUNT_OF_TASKS;
        if (start >= taskIds.size()) {
            start = 0;
        }

        int count = Math.min(AMOUNT_OF_TASKS, taskIds.size() - start);
        int[] returnedTaskIds = new int[count];
        for (int index = 0; index < count; index++) {
            returnedTaskIds[index] = taskIds.get(start + index);
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

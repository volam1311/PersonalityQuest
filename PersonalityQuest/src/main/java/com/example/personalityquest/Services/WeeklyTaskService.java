package com.example.personalityquest.Services;

import com.example.personalityquest.DAO.EmailDAO;
import com.example.personalityquest.DAO.WeeklyTaskDAO;
import com.example.personalityquest.Model.Task;
import com.example.personalityquest.Model.WeeklyTask;
import com.example.personalityquest.ApplicationManager;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * This class managers everything to do with WeeklyTasks such as retrieving this current
 * weeks tasks for a user, get their taskId's, generate new tasks for this week or update existing
 * tasks
 */
public class WeeklyTaskService {


    /// RETRIEVING
    /**
     * Gets an Array of this current weeks WeeklyTasks for an email
     * @param email The email of the account of the WeeklyTasks you want to retrieve
     * @return A populated Array of WeeklyTasks from this week OR Null if there are no weekly tasks
     * assigned for this Week
     * @throws IllegalArgumentException If the given email is null OR the account doesn't exist in
     * the database
     * @throws Exception From GetTaskId's SQL Exception to Database Access Failure.
     */
    public static WeeklyTask[] GetTasksForEmailForThisWeek(String email) throws Exception {
        if (IsEmailNull(email) || !EmailDAO.DoesAccountWithEmailExist(email)) {
            throw new IllegalArgumentException("Null Email or this account does not exist");
        }

        // gets the week start
        // to find the tasks that have been assigned this week
        LocalDate localDate = LocalDate.now();
        LocalDate weekStart = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        int[] taskIds = GetTaskIdsAssignedForWeek(email, weekStart);

        // checks to see if tasks are empty e.g. all 0's
        // if so return null (Means we need to generate some)
        for (int i = 0; i < taskIds.length; i++){
            System.out.println(taskIds[i]);
            if (taskIds[i] == 0) {
                System.out.println("Tasks is null");
                return null;}
        }

        WeeklyTask[] tasks = new WeeklyTask[taskIds.length];
        for (int i = 0; i < taskIds.length; i++) {
            tasks[i] = GetWeeklyTask(email, taskIds[i], weekStart);
        }
        return tasks;
    }

    /**
     * Gets a Weekly Task for a given taskId, email and weekStart
     * @param email The email of the acccount you want to get the WeeklyTask from
     * @param taskId The taskId of the WeeklyTask you want
     * @param weekStart The weekStart of when the task occured
     * @return The Weekly Task object with the accountEmail, taskId, status, reflection and weekStart
     * @throws IllegalArgumentException When weekStart, email or taskId is null or Empty
     * @throws SQLException From a Database Access Failure
     * @throws Exception If a "Bad" Weekly Task is made
     */
    public static WeeklyTask GetWeeklyTask(String email, int taskId, LocalDate weekStart) throws Exception {
        if (IsWeekStartNull(weekStart) || IsTaskIdNull(taskId) || IsEmailNull(email)){
            throw new IllegalArgumentException("WeekStart, TaskId or Email is Null");
        }

        return WeeklyTaskDAO.GetWeeklyTaskMatchingId(email, taskId, weekStart);
    }

    /**
     * Gets all the taskId's for the WeeklyTasks assigned this week
     * @param email The email of the account you want to get the taskId's for
     * @param weekStart The weekStart of the tasks you want to get
     * @return A populated Array of the TaskId's for this week
     * @throws IllegalArgumentException If Week start or Email is null or empty
     * @throws SQLException Database Access Failure
     */
    private static int[] GetTaskIdsAssignedForWeek(String email, LocalDate weekStart) throws Exception {
        if (IsWeekStartNull(weekStart) || IsEmailNull(email)){
            throw new IllegalArgumentException("Week start or email is null");
        }

        return WeeklyTaskDAO.GetTaskIdsAssignedForWeek(email, weekStart);
    }

    /// GENERATING
    /**
     * Gernerates an Array of Weekly Tasks, Inserts them into the database and returns it
     * @param email The email for the account you want to generate tasks for
     * @return An Array of WeeklyTasks that have been generated this week
     * @throws IllegalArgumentException If email is null or there is no account for an email
     * @throws Exception For Database Access and Update Failures and for when retrieving tasks with GetTaskIdsAssignedForWeek
     */
    public static WeeklyTask[] GenerateTasksForThisWeek(String email) throws Exception {
        if (IsEmailNull(email) || !EmailDAO.DoesAccountWithEmailExist(email)) {
            throw new IllegalArgumentException("Email is null or this account does not exist");
        }


        // gets the week start
        // to find the tasks that have been assigned this week
        LocalDate localDate = LocalDate.now();
        LocalDate weekStart = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        InsertTasks(email, weekStart);
        int[] taskIds = GetTaskIdsAssignedForWeek(email, weekStart);

        WeeklyTask[] tasks = new WeeklyTask[taskIds.length];
        for (int i = 0; i < taskIds.length; i++) {
            Task task = TaskService.GetTaskForId(taskIds[i]);
            tasks[i] = new WeeklyTask(email, task.getTaskId(), "Not Started", "", String.valueOf(weekStart));

        }

        return tasks;
    }
    /**
     * Inserts new Tasks into the database for the given email and weekStart by randomly choosing
     * from the list of available tasks for your quest
     * @param email The email of the account you want to insert tasks for
     * @param weekStart The weekStart you want to insertTasks for
     * @throws Exception For Database Update Failures and if week start or email is null or empty
     */
    private static void InsertTasks(String email, LocalDate weekStart) throws Exception {
        if (IsWeekStartNull(weekStart) || IsEmailNull(email)){
            throw new IllegalArgumentException("Week start or email is null");
        }


        WeeklyTask[] tasks = GetTasksForEmailForThisWeek(email);

        if (tasks != null){
            throw new Exception("Tried to generate tasks when they already exist");
        }

        WeeklyTaskDAO.InsertTasks(email, weekStart);
    }

    /// UPDATING
    /**
     * Updates the given Weekly Tasks to a draft
     * @param task The task you want to update to draft
     * @param reflection the draft reflection of the WeeklyTask
     * @param email The email of the account matching the WeeklyTask
     * @return The updated WeeklyTask with its status as 'Started'
     * @throws Exception If a Database Access Failure Or if the weekly task , reflection or email is null or empty
     */
    public static WeeklyTask UpdateGivenTaskToDraft(WeeklyTask task, String reflection, String email) throws Exception {
        if (IsWeeklyTaskNull(task) || IsReflectionNull(reflection) || IsEmailNull(email)){
            throw new IllegalArgumentException("Null weekly task, reflection or email");
        }

        // update the task
        WeeklyTaskDAO.UpdateGivenTaskToDraft(task, reflection, email);

        // find it and return it
        return GetWeeklyTask(email, task.getTaskId(), LocalDate.parse(task.getWeekStarted()));
    }
    /**
     * Updates the given Weekly Tasks to be finished
     * @param task The task you want to update to be finished
     * @param reflection the finished reflection of the WeeklyTask
     * @param email The email of the account matching the WeeklyTask
     * @return The updated WeeklyTask with its status as 'Finished'
     * @throws Exception If a Database Access Failure Or if the weekly task , reflection or email is null or empty
     */
    public static WeeklyTask UpdateGivenTaskToBeFinished(WeeklyTask task, String reflection, String email) throws Exception {
        if (IsWeeklyTaskNull(task) || IsReflectionNull(reflection) || IsEmailNull(email)){
            throw new IllegalArgumentException("Null weekly task, reflection or email");
        }

        WeeklyTaskDAO.UpdateGivenTaskToBeFinished(task, reflection, email);

        return GetWeeklyTask(email, task.getTaskId(), LocalDate.parse(task.getWeekStarted()));
    }

    /// NULL CHECKING
    /**
     * Checks if the given email is null
     * @param email The email you want checked
     * @return Whether the email is Empty or Null
     */
    private static boolean IsEmailNull(String email){
        return ApplicationManager.isEmpty(email);
    }
    /**
     * Checks if the given weeklyTask is null
     * @param weeklyTask The weeklyTask you want checked
     * @return Whether the weeklyTask is null
     */
    private static boolean IsWeeklyTaskNull(WeeklyTask weeklyTask){
        return weeklyTask == null;
    }
    /**
     * Checks if the given weekStart is null
     * @param weekStart The weekStart you want checked
     * @return Whether the weekStart is null
     */
    private static boolean IsWeekStartNull(LocalDate weekStart){
        if (weekStart == null) {return false;}
        if (weekStart.isAfter(LocalDate.now())) {return false;}
        return String.valueOf(weekStart) == null;
    }
    /**
     * Checks if the given reflection is null
     * @param reflection The reflection you want checked
     * @return Whether the reflection is null or empty
     */
    private static boolean IsReflectionNull(String reflection){
        return ApplicationManager.isEmpty(reflection);
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

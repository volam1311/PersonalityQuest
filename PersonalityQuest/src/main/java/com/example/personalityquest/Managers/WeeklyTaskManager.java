package com.example.personalityquest.Managers;

import com.example.personalityquest.DataClasses.Task;
import com.example.personalityquest.DataClasses.UserQuest;
import com.example.personalityquest.DataClasses.WeeklyTask;
import com.example.personalityquest.SQLite;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * This class managers everything to do with WeeklyTasks such as retrieving this current
 * weeks tasks for a user, get their taskId's, generate new tasks for this week or update existing
 * tasks
 */
public class WeeklyTaskManager {

    private final static int AMOUNT_OF_TASKS = SystemManager.TaskConfig.getAmountOfTasks();
    private final static String SQL_UPDATE_DRAFT = """
            UPDATE WeeklyTasks
            SET reflection = ?, status = 'Started'
            WHERE accountEmail = ? AND taskId = ?
        """;

    private final static String SQL_FINISH_TASK = """
            UPDATE WeeklyTasks
            SET reflection = ?, status = 'Finished'
            WHERE accountEmail = ? AND taskId = ?
        """;

    private final static String TASK_FOR_EMAIL = """ 
            SELECT * FROM WeeklyTasks
            WHERE accountEmail = ?
            AND weekStart = ?
            """;

    private final static String GET_WEEKLY_TASK = """ 
            SELECT * FROM WeeklyTasks 
            WHERE taskId = ? AND 
            accountEmail = ? AND weekStart = ?
            """;

    private static int defaultTaskSearchNum = 1;

    /**
     * Sets the default task num to insert into the database if no quest is active
     * @param num The number you want to set
     */
    public static void SetDefaultTaskSearchNum(int num){
        defaultTaskSearchNum = num;
    }

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

        if (IsEmailNull(email) || !EmailManager.DoesAccountWithEmailExist(email)) {
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

        // returns tasks for given week
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
        // executes the query to find the given taskId, email and weekStart date
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_WEEKLY_TASK);
        // assign parameters
        statement.setInt(1, taskId);
        statement.setString(2, email);
        statement.setString(3, String.valueOf(weekStart));

        ResultSet rs = statement.executeQuery();

        if (rs.next()){
            return new WeeklyTask(
                    rs.getString("accountEmail"),
                    rs.getInt("taskId"),
                    rs.getString("status"),
                    rs.getString("reflection"),
                    rs.getString("weekStart")
            );
        }

        System.out.println("GetWeeklyTask Returned Null");
        return null;
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
        // executes the query to find all tasks that were assigned this week
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(TASK_FOR_EMAIL);
        // assign parameters
        statement.setString(1, email);
        statement.setString(2, String.valueOf(weekStart));

        // appends all taskId's from the query to an array
        // to be used in next query
        int[] taskIds = new int[AMOUNT_OF_TASKS];
        ResultSet rs = statement.executeQuery();
        int count = 0;
        while (rs.next()) {
            taskIds[count] = rs.getInt("taskId");
            count++;
        }

        // if its counted least one task then the user has a set this week
        // if it hasn't must return null
        if (count < AMOUNT_OF_TASKS && count != 0){
            return Arrays.copyOf(taskIds, count);
        }
        return taskIds;
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
        if (IsEmailNull(email) || !EmailManager.DoesAccountWithEmailExist(email)) {
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
            Task task = TaskManager.GetTaskForId(taskIds[i]);
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

        /// HAVE A WAY TO CHOOSE WHICH TASKS GET ASSIGNED FOR NOW JUST TEST TASK
        String INSERT_NEW_TASKS = " INSERT INTO WeeklyTasks " +
                "(accountEmail, taskId, status, weekStart) VALUES";

        WeeklyTask[] tasks = GetTasksForEmailForThisWeek(email);


        if (tasks != null){
            throw new Exception("Tried to generate tasks when they already exist");
        }
        for (int i = 0; i < AMOUNT_OF_TASKS; i++){

            INSERT_NEW_TASKS += (" (?, ?, ?, ?)");
            if (i + 1 != AMOUNT_OF_TASKS){
                INSERT_NEW_TASKS +=", ";
            }
        }

        try{
            UserQuest currentActiveQuest = UserQuestManager.GetCurrentActiveUserQuestForEmail
                    (SystemManager.CurrentAccount.getCurrentEmail());

            System.out.println("Current Quest labourId is" + currentActiveQuest.getLabourId());

            int[] taskIds = TaskManager.GetRandomAmountOfTaskIdsForLabourId(currentActiveQuest.getLabourId());

            // checks for successful retrieval of all the different tasks and none were null
            for (int i = 0; i < taskIds.length; i++){
                if (taskIds[i] == 0){
                    throw new Exception("Not Full Amount of Tasks where generated instead only "
                            + taskIds.length + " where generated when the expecting was " + AMOUNT_OF_TASKS);
                }
            }

            // appends all task ids to FindTaskInfo Query and then
            // executes it find all task info
            Connection connection = SQLite.getConnection();
            PreparedStatement statement = connection.prepareStatement(INSERT_NEW_TASKS);
            // assign parameters
            for (int i = 0; i < AMOUNT_OF_TASKS; i++){
                int start = i * 4;
                statement.setString(start + 1, email);
                statement.setInt(start + 2, taskIds[i]);
                statement.setString(start + 3, "NotStarted");
                statement.setString(start + 4, String.valueOf(weekStart));
            }
            statement.executeUpdate();
        }
        catch (Exception exception){
            System.out.println(exception.getMessage());

            // appends all task ids to FindTaskInfo Query and then
            // executes it find all task info
            Connection connection = SQLite.getConnection();
            PreparedStatement statement = connection.prepareStatement(INSERT_NEW_TASKS);
            // assign parameters
            for (int i = 0; i < AMOUNT_OF_TASKS; i++){
                int start = i * 4;
                statement.setString(start + 1, email);
                statement.setInt(start + 2, defaultTaskSearchNum);
                statement.setString(start + 3, "NotStarted");
                statement.setString(start + 4, String.valueOf(weekStart));
            }
            statement.executeUpdate();
        }
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
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_DRAFT);
        statement.setString(1, reflection);
        statement.setString(2, email);
        statement.setInt(3, task.getTaskId());

        statement.executeUpdate();

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
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(SQL_FINISH_TASK);
        statement.setString(1, reflection);
        statement.setString(2, email);
        statement.setInt(3, task.getTaskId());

        statement.executeUpdate();

        return GetWeeklyTask(email, task.getTaskId(), LocalDate.parse(task.getWeekStarted()));
    }

    /// NULL CHECKING
    /**
     * Checks if the given email is null
     * @param email The email you want checked
     * @return Whether the email is Empty or Null
     */
    private static boolean IsEmailNull(String email){
        return SystemManager.isEmpty(email);
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
        return SystemManager.isEmpty(reflection);
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

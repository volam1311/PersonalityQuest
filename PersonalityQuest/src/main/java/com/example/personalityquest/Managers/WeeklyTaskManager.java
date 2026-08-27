package com.example.personalityquest.Managers;

import com.example.personalityquest.DataClasses.Task;
import com.example.personalityquest.DataClasses.WeeklyTask;
import com.example.personalityquest.SQLite;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class WeeklyTaskManager {


    private final static int AMOUNTOFTASKS = 1;
    private final static String SQL_UPDATE_DRAFT = "UPDATE WeeklyTasks" +
            " SET reflection = ?, status = " + "'Started'" +
            " WHERE accountEmail = ? AND taskId = ?";

    private final static String SQL_FINISH_TASK = "UPDATE WeeklyTasks" +
            " SET reflection = ?, SET status = " + "Finished" +
            " WHERE accountEmail = ? AND taskId = ?";

    private final static String TASK_FOR_EMAIL = """
            SELECT * FROM WeeklyTasks
            WHERE accountEmail = ?
            AND weekStart = ?
            """;

    private final static String FIND_TASK_INFO = """
            SELECT * FROM Tasks
            WHERE taskId = ?;
            """;

    private final static String GET_WEEKLY_TASK = """ 
            SELECT * FROM WeeklyTasks WHERE taskId = ? AND accountEmail = ? AND weekStart = ?
            """;

    private static int defaultTaskSearchNum = 1;

    public static void SetDefaultTaskSearchNum(int num){
        defaultTaskSearchNum = num;
    }
    public static WeeklyTask[] GetTasksForEmailForThisWeek(String email) throws Exception {
        if (!EmailManager.DoesAccountWithEmailExist(email)) {
            return null;
        }

        // gets the week start
        // to find the tasks that have been assigned this week
        LocalDate localDate = LocalDate.now();
        LocalDate weekStart = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        int[] taskIds = GetTaskIds(email, weekStart);

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
            Task task = GetTaskForId(taskIds[i]);
            tasks[i] = new WeeklyTask(email, task.getTaskId(), "Not Started", "", String.valueOf(weekStart));
        }

        // returns tasks for given week
        return tasks;
    }
    public static WeeklyTask[] GenerateTasksForThisWeek(String email) throws Exception {
        if (!EmailManager.DoesAccountWithEmailExist(email)) {
            return null;
        }

        // gets the week start
        // to find the tasks that have been assigned this week
        LocalDate localDate = LocalDate.now();
        LocalDate weekStart = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        InsertTasks(email, weekStart);
        int[] taskIds = GetTaskIds(email, weekStart);

        WeeklyTask[] tasks = new WeeklyTask[taskIds.length];
        for (int i = 0; i < taskIds.length; i++) {
            Task task = GetTaskForId(taskIds[i]);
            tasks[i] = new WeeklyTask(email, task.getTaskId(), "Not Started", "", String.valueOf(weekStart));

        }

        return tasks;
    }
    public static Task GetTaskForId(int taskId) throws Exception {
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
    public static void UpdateGivenTaskToDraft(WeeklyTask task, String reflection, String email) throws SQLException {
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_DRAFT);
        statement.setString(1, reflection);
        statement.setString(2, email);
        statement.setInt(3, task.getTaskId());

        statement.executeUpdate();
    }
    public static WeeklyTask GetWeeklyTask(String email, int taskId, LocalDate weekStart) throws Exception {
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

    private static void InsertTasks(String email, LocalDate weekStart) throws Exception {

        /// HAVE A WAY TO CHOOSE WHICH TASKS GET ASSIGNED FOR NOW JUST TEST TASK
        String INSERT_3_NEW_TASKS = " INSERT INTO WeeklyTasks (accountEmail, taskId, status, weekStart) VALUES";

        WeeklyTask[] tasks = GetTasksForEmailForThisWeek(email);

        if (tasks != null){
            throw new Exception("Tried to generate tasks when they already exist");
        }
        for (int i = 0; i < AMOUNTOFTASKS; i++){

            INSERT_3_NEW_TASKS += (" (?, ?, ?, ?)");
            if (i + 1 != AMOUNTOFTASKS){
                INSERT_3_NEW_TASKS +=", ";
            }
        }

        // appends all task ids to FindTaskInfo Query and then
        // executes it find all task info
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(INSERT_3_NEW_TASKS);
        // assign parameters
        for (int i = 0; i < AMOUNTOFTASKS; i++){
            int start = i * 4;
            statement.setString(start + 1, email);
            statement.setInt(start + 2, defaultTaskSearchNum);
            statement.setString(start + 3, "NotStarted");
            statement.setString(start + 4, String.valueOf(weekStart));
        }

        statement.executeUpdate();
    }
    private static int[] GetTaskIds(String email, LocalDate weekStart) throws SQLException {
        // executes the query to find all tasks that were assigned this week
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(TASK_FOR_EMAIL);
        // assign parameters
        statement.setString(1, email);
        statement.setString(2, String.valueOf(weekStart));

        // appends all taskId's from the query to an array
        // to be used in next query
        int[] taskIds = new int[AMOUNTOFTASKS];
        ResultSet rs = statement.executeQuery();
        int count = 0;
        while (rs.next()) {
            taskIds[count] = rs.getInt("taskId");
            count++;
        }

        return taskIds;
    }


}

package com.example.personalityquest.Managers;

import com.example.personalityquest.DataClasses.EmailDetails;
import com.example.personalityquest.DataClasses.Task;
import com.example.personalityquest.SQLite;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class WeeklyTaskManager {

    private final static int AMOUNTOFTASKS = 2;
    private final static String SQL_UPDATE_DRAFT = "UPDATE TaskCompletions" +
            " SET reflection = ?, SET status = "+ "Started" +
            " WHERE accountEmail = ? AND taskId = ?";

    private final static String SQL_FINISH_TASK = "UPDATE TaskCompletions" +
            " SET reflection = ?, SET status = " + "Finished" +
            " WHERE accountEmail = ? AND taskId = ?";

    private final static String TASK_FOR_EMAIL = """
            SELECT * FROM TaskCompletions
            WHERE accountEmail = ?
            AND weekStart = ?
            """;

    private final static String FIND_TASK_INFO = """
            SELECT * FROM Tasks
            WHERE taskId IN (?, ?, ?);
            """;

    public static Task[] GetTasksForEmailForThisWeek(String email) throws Exception {
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

        // get the tasks for the above ids
        Task[] tasks = GetTasksForIds(taskIds);

        // returns tasks for given week
        return tasks;
    }
    public static Task[] GenerateTasksForThisWeek(String email) throws Exception {
        if (!EmailManager.DoesAccountWithEmailExist(email)) {
            return null;
        }

        // gets the week start
        // to find the tasks that have been assigned this week
        LocalDate localDate = LocalDate.now();
        LocalDate weekStart = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        InsertTasks(email, weekStart);
        int[] taskIds = GetTaskIds(email, weekStart);
        Task[] tasks = GetTasksForIds(taskIds);

        return tasks;
    }
    public static void UpdateGivenTaskToDraft(int taskId){

    }

    private static void InsertTasks(String email, LocalDate weekStart) throws SQLException {

        /// HAVE A WAY TO CHOOSE WHICH TASKS GET ASSIGNED FOR NOW JUST TEST TASK
        String INSERT_3_NEW_TASKS = " INSERT INTO TaskCompletions (accountEmail, taskId, status, weekStart) VALUES";

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
            statement.setInt(start + 2, 1);
            statement.setString(start + 3, "NotStarted");
            statement.setString(start + 4, String.valueOf(weekStart));
        }

        statement.executeUpdate();
    }
    private static Task[] GetTasksForIds(int[] taskIds) throws Exception {
        // appends all task ids to FindTaskInfo Query and then
        // executes it find all task info
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(FIND_TASK_INFO);
        // assign parameters
        // assigns all task info to an array
        Task[] tasks = new Task[AMOUNTOFTASKS];

        for (int i = 0; i < taskIds.length; i++){
            statement.setInt(i + 1, taskIds[i]);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                tasks[i] = new Task(
                        rs.getInt("taskId"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("labourId")
                );
            }
        }



        for (int i = 0; i < tasks.length; i++){
            if (tasks[i] == null){
                throw new Exception("User does not have 3 separate Tasks. Check Db To See Why.");
            }
        }
        return tasks;
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

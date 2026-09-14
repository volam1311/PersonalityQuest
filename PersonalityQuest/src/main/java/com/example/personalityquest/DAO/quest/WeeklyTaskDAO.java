package com.example.personalityquest.DAO.quest;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.Model.quest.UserQuest;
import com.example.personalityquest.Model.quest.WeeklyTask;
import com.example.personalityquest.SQLite;
import com.example.personalityquest.Services.quest.TaskService;
import com.example.personalityquest.Services.quest.UserQuestService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class WeeklyTaskDAO {
    private final static int AMOUNT_OF_TASKS = ApplicationManager.TaskConfig.getAmountOfTasks();

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
            SELECT taskId FROM WeeklyTasks
            WHERE accountEmail = ?
            AND weekStart = ?
            GROUP BY taskId
            ORDER BY MIN(id)
            """;

    private final static String GET_WEEKLY_TASK = """ 
            SELECT * FROM WeeklyTasks 
            WHERE taskId = ? AND 
            accountEmail = ? AND weekStart = ?
            """;



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
    public static WeeklyTask GetWeeklyTaskMatchingId(String email, int taskId, LocalDate weekStart) throws Exception {
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
    public static int[] GetTaskIdsAssignedForWeek(String email, LocalDate weekStart) throws Exception {
        // executes the query to find all tasks that were assigned this week
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(TASK_FOR_EMAIL);
        // assign parameters
        statement.setString(1, email);
        statement.setString(2, String.valueOf(weekStart));

        List<Integer> taskIds = new ArrayList<>();
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            taskIds.add(rs.getInt("taskId"));
        }

        if (taskIds.isEmpty()) {
            return new int[AMOUNT_OF_TASKS];
        }
        return toIntArray(taskIds);
    }
    /**
     * Inserts this week's weekly practices for the given email.
     * These are chosen from the weekly-task pool, not from the labour's storyline quest tasks.
     * @param email The email of the account you want to insert tasks for
     * @param weekStart The weekStart you want to insertTasks for
     * @throws Exception For Database Update Failures and if week start or email is null or empty
     */
    public static void InsertTasks(String email, LocalDate weekStart) throws Exception {
        try{
            UserQuest currentActiveQuest = UserQuestService.GetCurrentActiveUserQuestForEmail
                    (ApplicationManager.CurrentAccount.getCurrentEmail());

            System.out.println("Current Quest labourId is" + currentActiveQuest.getLabourId());

            int weekNumber = CountAssignedWeeks(email) + 1;
            int[] taskIds = TaskService.GetTaskIdsForLabourWeek(currentActiveQuest.getLabourId(), weekNumber);
            if (taskIds.length == 0) {
                taskIds = TaskService.GetRandomAmountOfTaskIdsForLabourId(currentActiveQuest.getLabourId());
            }

            int[] uniqueTaskIds = uniquePositiveIds(taskIds);
            if (uniqueTaskIds.length == 0) {
                throw new Exception("Not Full Amount of Tasks where generated instead only "
                        + taskIds.length + " where generated when the expecting was " + AMOUNT_OF_TASKS);
            }

            InsertWeeklyTaskRows(email, uniqueTaskIds, weekStart);
        }
        catch (Exception exception){
            System.out.println(exception.getMessage());
            InsertWeeklyTaskRows(email, new int[] { ApplicationManager.TaskConfig.getDefaultSearchNum() }, weekStart);
        }
    }

    private static void InsertWeeklyTaskRows(String email, int[] taskIds, LocalDate weekStart) throws SQLException {
        StringBuilder insertSql = new StringBuilder(
                "INSERT INTO WeeklyTasks (accountEmail, taskId, status, weekStart) VALUES ");
        for (int i = 0; i < taskIds.length; i++) {
            if (i > 0) {
                insertSql.append(", ");
            }
            insertSql.append("(?, ?, ?, ?)");
        }

        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(insertSql.toString());
        for (int i = 0; i < taskIds.length; i++) {
            int start = i * 4;
            statement.setString(start + 1, email);
            statement.setInt(start + 2, taskIds[i]);
            statement.setString(start + 3, "NotStarted");
            statement.setString(start + 4, String.valueOf(weekStart));
        }
        statement.executeUpdate();
    }

    private static int[] uniquePositiveIds(int[] taskIds) {
        Set<Integer> uniqueIds = new LinkedHashSet<>();
        if (taskIds != null) {
            for (int taskId : taskIds) {
                if (taskId != 0) {
                    uniqueIds.add(taskId);
                }
            }
        }
        return toIntArray(uniqueIds);
    }

    private static int[] toIntArray(Iterable<Integer> values) {
        List<Integer> list = new ArrayList<>();
        for (Integer value : values) {
            list.add(value);
        }
        int[] result = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    /**
     * How many distinct weeks this account already has weekly tasks for.
     */
    public static int CountAssignedWeeks(String email) throws SQLException {
        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                """
                    SELECT COUNT(DISTINCT weekStart) FROM WeeklyTasks
                    WHERE accountEmail = ?
                    """)) {
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    /// UPDATING
    /**
     * Updates the given Weekly Tasks to a draft
     * @param task The task you want to update to draft
     * @param reflection the draft reflection of the WeeklyTask
     * @param email The email of the account matching the WeeklyTask
     * @throws Exception If a Database Access Failure Or if the weekly task , reflection or email is null or empty
     */
    public static void UpdateGivenTaskToDraft(WeeklyTask task, String reflection, String email) throws Exception {
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(SQL_UPDATE_DRAFT);
        statement.setString(1, reflection);
        statement.setString(2, email);
        statement.setInt(3, task.getTaskId());

        statement.executeUpdate();
    }
    /**
     * Updates the given Weekly Tasks to be finished
     * @param task The task you want to update to be finished
     * @param reflection the finished reflection of the WeeklyTask
     * @param email The email of the account matching the WeeklyTask
     * @throws Exception If a Database Access Failure Or if the weekly task
     */
    public static void UpdateGivenTaskToBeFinished(WeeklyTask task, String reflection, String email) throws Exception {
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(SQL_FINISH_TASK);
        statement.setString(1, reflection);
        statement.setString(2, email);
        statement.setInt(3, task.getTaskId());

        statement.executeUpdate();
    }
}

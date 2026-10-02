package com.example.personalityquest.DAO.quest;

import com.example.personalityquest.Model.quest.Task;
import com.example.personalityquest.SQLite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Stores and retrieves task catalog records */
public class TaskDAO {
    private static final String FIND_TASK_INFO = """
            SELECT * FROM Tasks
            WHERE taskId = ?;
            """;

    private static final String GET_QUEST_TASKS_FOR_LABOURID = """
            SELECT * FROM Tasks
            WHERE labourId = ?
            AND IFNULL(taskType, 'QUEST') = 'QUEST'
            ORDER BY taskId
            """;

    private static final String GET_WEEKLY_TASK_IDS_FOR_LABOURID = """
            SELECT taskId FROM Tasks
            WHERE labourId = ?
            AND IFNULL(taskType, 'QUEST') = 'WEEKLY'
            ORDER BY taskId
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
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(FIND_TASK_INFO);
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
            throw new Exception("No task matching the givenId");
        }
        return task;
    }

    /**
     * Gets the weekly-practice task ids for a labour. These are not storyline quest tasks.
     * @param labourId The labourId you want to get the weekly taskIds for
     * @return A list of weekly task ids
     * @throws SQLException Database Access Failure
     */
    public static List<Integer> GetWeeklyTaskIdsForLabourId(int labourId) throws SQLException {
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_WEEKLY_TASK_IDS_FOR_LABOURID);
        List<Integer> taskIds = new ArrayList<Integer>();

        statement.setInt(1, labourId);
        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            taskIds.add(rs.getInt("taskId"));
        }

        return taskIds;
    }

    /**
     * Gets every storyline quest task that belongs to a labour.
     * Weekly practices are stored separately and are not included.
     * @param labourId The labourId you want the quest tasks for
     * @return A list of Task objects, empty if none exist
     * @throws Exception If a row cannot be mapped to a Task, or Database Access Failure
     */
    public static List<Task> GetTasksForLabourId(int labourId) throws Exception {
        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(GET_QUEST_TASKS_FOR_LABOURID)) {
            statement.setInt(1, labourId);
            ResultSet rs = statement.executeQuery();
            List<Task> tasks = new ArrayList<>();
            while (rs.next()) {
                tasks.add(new Task(
                        rs.getInt("taskId"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getInt("labourId")
                ));
            }
            return tasks;
        }
    }
}

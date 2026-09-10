package com.example.personalityquest.DAO;

import com.example.personalityquest.Model.Task;
import com.example.personalityquest.SQLite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

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
     * Gets all the TaskId's that match a given labourId
     * @param labourId The labourId you want to get the taskIds for
     * @return A List Interger of the taskId's
     * @throws SQLException Database Access Failure
     */
    public static List<Integer> GetTaskIdsForLabourID(int labourId) throws SQLException {
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

        return  taskIds;
    }

    /**
     * Gets every Task that belongs to a labour.
     * @param labourId The labourId you want the tasks for
     * @return A list of Task objects, empty if none exist
     * @throws Exception If a row cannot be mapped to a Task, or Database Access Failure
     */
    public static List<Task> GetTasksForLabourId(int labourId) throws Exception {
        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(GET_TASKS_FOR_LABOURID)) {
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

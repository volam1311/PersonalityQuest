package com.example.personalityquest.DAO.quest;

import com.example.personalityquest.DAO.ParentDAO;
import com.example.personalityquest.Model.quest.Task;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Stores and retrieves task catalog records */
public class TaskDAO extends ParentDAO {
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

    private static final String GET_CHALLENGE_FOR_LABOUR_AND_REACTION = """
            SELECT * FROM Tasks
            WHERE labourId = ?
            AND IFNULL(taskType, 'QUEST') = 'QUEST'
            AND reactionType = ?
            ORDER BY taskId
            """;

    public TaskDAO(){
        super();
    }

    public TaskDAO(Connection connection) {
        super(connection);
    }

    /**
     * Gets a Task from the database that matches the given id
     * @param taskId The taskId you of the Task you want to retrieve
     * @return A Task objecting containing the given taskId's information of the
     * taskId, name, description and labourId(QuestId)
     * @throws IllegalArgumentException If the given taskId is equal to 0
     * @throws Exception If there is a Database Access Failure OR user doesn't have separate tasks
     */
    public Task GetTaskForId(int taskId) throws Exception {
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
                    rs.getString("overview"),
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
    public List<Integer> GetWeeklyTaskIdsForLabourId(int labourId) throws SQLException {
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
    public List<Task> GetTasksForLabourId(int labourId) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(GET_QUEST_TASKS_FOR_LABOURID)) {
            statement.setInt(1, labourId);
            ResultSet rs = statement.executeQuery();
            List<Task> tasks = new ArrayList<>();
            while (rs.next()) {
                tasks.add(new Task(
                        rs.getInt("taskId"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("overview"),
                        rs.getInt("labourId")
                ));
            }
            return tasks;
        }
    }

    public void EnsureTables() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("ALTER TABLE Tasks ADD Column reactionType TEXT");
        } catch (SQLException alreadyExists) {
            // Column already exists, ignore
        }
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("ALTER TABLE Tasks ADD COLUMN overview TEXT NOT NULL DEFAULT ''");
        } catch (SQLException alreadyExists) {
            // Column already exists, ignore
        }
    }

    public List<Task> GetTasksForLabourIdAndReactionType(int labourId, String reactionType) throws Exception{
        try (PreparedStatement statement = connection.prepareStatement(GET_CHALLENGE_FOR_LABOUR_AND_REACTION)) {
            statement.setInt(1, labourId);
            statement.setString(2, reactionType);
            ResultSet rs = statement.executeQuery();
            List<Task> tasks = new ArrayList<>();
            while (rs.next()) {
                tasks.add(new Task(
                        rs.getInt("taskId"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("overview"),
                        rs.getInt("labourId")
                ));
            }
            return tasks;
        }
    }

    public void SetChallenge(int taskId, int labourId, String name, String description, String overview, String reactionType) throws SQLException{
        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE Tasks SET name = ?, description = ?, overview = ?, labourId = ?, reactionType = ? WHERE taskId = ?")){
            statement.setString(1, name);
            statement.setString(2, description);
            statement.setString(3, overview);
            statement.setInt(4, labourId);
            statement.setString(5, reactionType);
            statement.setInt(6, taskId);
            statement.executeUpdate();
        }
    }

    public void DeleteTask(int taskId) throws SQLException{
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM Tasks WHERE taskId = ?")){
            statement.setInt(1, taskId);
            statement.executeUpdate();
        }
    }

    public boolean IsLabourAlreadyMigrated(int labourId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(*) FROM Tasks WHERE labourId = ? AND reactionType IS NOT NULL")){
            statement.setInt(1, labourId);
            ResultSet rs = statement.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public List<Integer> GetTaskIdsForLabourId(int labourId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT taskId FROM Tasks WHERE labourId = ? AND IFNULL(taskType, 'QUEST') = 'QUEST' ORDER BY taskId")) {
            statement.setInt(1, labourId);
            ResultSet rs = statement.executeQuery();
            List<Integer> taskIds = new ArrayList<>();
            while (rs.next()) {
                taskIds.add(rs.getInt("taskId"));
            }
            return taskIds;
        }
    }

    public void MigrateLabourChallenges(int labourId,
                                               String excessName, String excessDescription, String excessOverview,
                                               String deficitName, String deficitDescription, String deficitOverview) throws SQLException {
        List<Integer> taskIds = GetTaskIdsForLabourId(labourId);
        if (taskIds.size() < 2) {
            return;
        }
        SetChallenge(taskIds.get(0), labourId, excessName, excessDescription, excessOverview, "EXCESS");
        SetChallenge(taskIds.get(1), labourId, deficitName, deficitDescription, deficitOverview, "DEFICIT");
        for (int index = 2; index < taskIds.size(); index++) {
            DeleteTask(taskIds.get(index));
        }
    }

    public void PopulateChallenges() throws SQLException {
        MigrateLabourChallenges(2,
                "Stand Level",
                "This week, in a group where you'd normally expect to lead or be listened to first, deliberately take the equal seat instead — ask for someone else's account before you give yours, and let the outcome rest on the shared facts rather than your standing.",
                "Record in your Journal the moment you took the equal seat instead of leading.",
                "Hold Your Ground",
                "This week, find one moment where you'd normally go quiet to keep the peace — a group leaning the wrong way, a decision you privately disagree with — and say your piece anyway, plainly and without apology.",
                "Record in your Journal the moment you spoke up instead of staying quiet.");

        MigrateLabourChallenges(3,
                "Pick your ground",
                "This week, take on a challenge you'd normally rush straight into, but pause first and choose how you'll face it — the timing, the ground, the approach that gives you the best chance. Act with the same resolve, just aimed with judgment rather than thrown blindly.",
                "Record in your Journal the challenge you will pause and face.",
                "Close the distance",
                "This week, find one thing you've been facing from a safe distance — a conversation you're circling, a task you keep setting up for but not starting — and actually close with it. Take the direct step you've been waiting to feel ready for.",
                "Record in your Journal the step you took to close the distance.");

        MigrateLabourChallenges(1,
                "Temper your faith",
                "Before you commit to a favour, plan or a promise, verbalise to yourself what could go wrong. Make the commitment only after you're ready and with open eyes. Do this before one major decision that may have consequences if done so blindly.",
                "Record in your Journal the commitment you made with open eyes.",
                "Lower the armour",
                "This week, find one situation where you'd normally assume the worst of someone — a message that seems harsh, a person you expect to be difficult, a request you think will be refused — and approach it openly anyway. Have the conversation or make the ask, without the armour up, and see what happens.",
                "Record in your Journal the moment you lowered the armour and approached it openly.");
    }

}

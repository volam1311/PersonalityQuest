package com.example.personalityquest.DAO.quest;

import com.example.personalityquest.Model.quest.Quest;
import com.example.personalityquest.Model.quest.UserQuest;
import com.example.personalityquest.SQLite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserQuestDAO {

    private static final String CREATE_USER_QUESTS = """
            CREATE TABLE IF NOT EXISTS UserQuests (
            accountEmail TEXT NOT NULL,
            labourId INTEGER NOT NULL,
            percentageComplete REAL NOT NULL,
            status TEXT NOT NULL,
            reflection TEXT NOT NULL DEFAULT '',
            reflectionStatus TEXT NOT NULL DEFAULT 'Not Started',
            PRIMARY KEY (accountEmail, labourId)
            )
            """;

    public static void EnsureTables() throws SQLException {
        Connection connection = SQLite.getConnection();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_USER_QUESTS);
        }
        AddColumnIfMissing("reflection", "TEXT NOT NULL DEFAULT ''");
        AddColumnIfMissing("reflectionStatus", "TEXT NOT NULL DEFAULT 'Not started'");
    }

    private static void AddColumnIfMissing(String columnName, String definition) {
        Connection connection = SQLite.getConnection();
        try (Statement statement = connection.createStatement()){
            statement.executeUpdate("ALTER TABLE UserQuests ADD COLUMN " + columnName + " " + definition);
        } catch (SQLException alreadyExists){
            // Column already exists so ignore
        }
    }

    public static void ClearAll()  throws SQLException {
        Connection connection = SQLite.getConnection();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DELETE FROM UserQuests");
        }
    }

    /**
     * Retrives the current active quest for the user matching the account
     * @param email The email of the account you want to check
     * @return The currently active UserQuest in the database or null if no active quest is set for the email
     * @throws IllegalArgumentException If the given email is empty
     * @throws SQLException Database Access Failure
     */
    public static UserQuest GetCurrentActiveUserQuestForEmail(String email) throws IllegalArgumentException, SQLException {
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                """
                    SELECT * FROM UserQuests
                    WHERE accountEmail = ? AND status = 'Active'
                    """);
        statement.setString(1, email);

        ResultSet rs = statement.executeQuery();
        UserQuest quest = null;
        if (rs.next()){
            quest = new UserQuest(
                    rs.getInt("labourId"),
                    rs.getString("accountEmail"),
                    rs.getFloat("percentageComplete"),
                    rs.getString("status"),
                    rs.getString("reflection"),
                    rs.getString("reflectionStatus")
            );
        }

        return quest;
    }

    /**
     * Gets every UserQuest assigned to the given account.
     * Active quests are returned first, then not started, then complete.
     * @param email The email of the account to load quests for
     * @return A list of UserQuests, empty if the account has none
     * @throws SQLException Database Access Failure
     */
    public static List<UserQuest> GetUserQuestsForEmail(String email) throws SQLException {
        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                """
                    SELECT * FROM UserQuests
                    WHERE accountEmail = ?
                    ORDER BY CASE status
                        WHEN 'Active' THEN 0
                        WHEN 'Not Started' THEN 1
                        ELSE 2
                    END, labourId
                    """)) {
            statement.setString(1, email);

            ResultSet rs = statement.executeQuery();
            List<UserQuest> quests = new ArrayList<>();
            while (rs.next()) {
                quests.add(new UserQuest(
                        rs.getInt("labourId"),
                        rs.getString("accountEmail"),
                        rs.getFloat("percentageComplete"),
                        rs.getString("status"),
                        rs.getString("reflection"),
                        rs.getString("reflectionStatus")
                ));
            }
            return quests;
        }
    }

    /**
     * Sets the given UserQuest to active in the database
     * @param userQuest The userQuest you want to set as active
     * @param email The account you want to set the quest active for
     * @throws IllegalArgumentException If the quest or email is null Or if the given email doesn't
     * have an active quest
     * @throws SQLException Database Update Failure
     */
    public static void SetUserQuesStatusAsActive(UserQuest userQuest, String email) throws IllegalArgumentException, SQLException {
        Connection connection = SQLite.getConnection();

        PreparedStatement statement = connection.prepareStatement(
                """
                    UPDATE UserQuests 
                    SET status = 'Active'
                    WHERE accountEmail = ? AND labourId = ?
                    """);
        statement.setString(1, email);
        statement.setInt(2, userQuest.getLabourId());
        statement.executeUpdate();

    }

    /**
     * Inserts a new User Quest into the database then returns it
     * @param quest The quest you want to turn into a userQuest
     * @param email The account you want to insert a new quest for
     * @throws SQLException If A constraint on foreign keys fails or Database Update Failure
     */
    public static void InsertNewQuestForEmail(Quest quest, String email) throws SQLException {
        Connection connection = SQLite.getConnection();
        try{
            PreparedStatement statement = connection.prepareStatement(
                    """
                        INSERT INTO UserQuests
                            (accountEmail, labourId, percentageComplete, status)
                        VALUES (?, ?, 0.0, 'Not Started')
                        """);

            statement.setString(1, email);
            statement.setInt(2, quest.getLabourId());
            statement.execute();

        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }

    /**
     * Sets the given UserQuest to status "Complete" in the database
     * @param quest The UserQuest you want to set as "Complete"
     * @param email The account you want to set the quest as complete for
     * @throws IllegalArgumentException If quest is null or email is empty
     * @throws SQLException Database Access and Update Failure
     */
    public static void SetUserQuestStatusAsComplete(UserQuest quest, String email) throws SQLException {
        Connection connection = SQLite.getConnection();

        PreparedStatement statement = connection.prepareStatement(
                """
                    UPDATE UserQuests 
                    SET status = 'Complete'
                    WHERE accountEmail = ? AND labourId = ?
                    """);
        statement.setString(1, email);
        statement.setInt(2, quest.getLabourId());
        statement.executeUpdate();
    }

    /**
     * Sets the given User quest to a percentage complete from 0 and 1 for the given email
     * @param quest The user quest you want to update
     * @param email The account email you want to update the user quest for
     * @param percentage The percentage between 0 and 1 you want the quest to be at
     * @throws IllegalArgumentException If the quest or email is null Or if the percentageComplete is not between
     * 0 and 1
     * @throws SQLException Database Access or Update Failure
     */
    public static void SetUserQuestToPercentageComplete(UserQuest quest, String email, float percentage) throws SQLException {

        Connection connection = SQLite.getConnection();

        PreparedStatement statement = connection.prepareStatement(
                """
                    UPDATE UserQuests 
                    SET percentageComplete = ?
                    WHERE accountEmail = ? AND labourId = ? AND status = 'Active'
                    """);
        statement.setFloat(1, percentage);
        statement.setString(2, email);
        statement.setInt(3, quest.getLabourId());
        statement.executeUpdate();

    }

    /**
     * Checks to see if a given account email has a currently active quest
     * @param email The email of the account you want to check
     * @return Whether the given email has an account with an active quest
     * @throws IllegalArgumentException If the user has more than 1 active quest
     * @throws SQLException Database Access Failure
     */
    public static boolean DoesUserHaveActiveQuest(String email) throws IllegalArgumentException, SQLException {
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                """
                    SELECT COUNT(*) FROM UserQuests
                    WHERE accountEmail = ? AND status = 'Active'
                    """);
        statement.setString(1, email);
        ResultSet rs = statement.executeQuery();

        int count = 0;
        if (rs.next()){
            count = rs.getInt(1);
        }

        if (count > 1){
            throw new IllegalArgumentException("User has more then 1 active quest");
        }

        return count == 1;
    }

    /**
     * Gets an accounts specific UserQuest for the labourId
     * @param email The account email you want to retrieve a UserQuest for
     * @param labourId The labourId that matches a UserQuest in the database for email
     * @return The UserQuest matching the email and labourId given OR null if none could be retrived
     * @throws SQLException Database Access Failure
     */
    public static UserQuest GetUserQuestForEmailAndLabourId(String email, int labourId) throws SQLException {
        Connection connection = SQLite.getConnection();

        PreparedStatement statement = connection.prepareStatement(
                """
                    SELECT * FROM UserQuests
                    WHERE accountEmail = ? AND labourId = ?
                    """);


        statement.setString(1, email);
        statement.setInt(2, labourId);

        ResultSet rs = statement.executeQuery();
        UserQuest updatedQuest = null;

        if (rs.next()){
            updatedQuest = new UserQuest(
                    rs.getInt("labourId"),
                    rs.getString("accountEmail"),
                    rs.getFloat("percentageComplete"),
                    rs.getString("status")
            );
        }

        if (updatedQuest == null){
            System.out.println("updated quest is null");
        }
        return updatedQuest;

    }

    private static final String SQL_UPDATE_REFLECTION_DRAFT = """
            UPDATE UserQuests
            SET reflection = ?, reflectionStatus = 'Started'
            WHERE accountEmail = ? AND labourId = ?
            """;

    private static final String SQL_FINISH_REFLECTION = """
            UPDATE UserQuests
            SET reflection = ?,reflectionStatus = 'Finished'
            WHERE accountEmail = ? AND labourId = ?
            """;

    public static void UpdateGivenQuestReflectionToDraft(UserQuest quest, String reflection, String email) throws SQLException {
        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement =  connection.prepareStatement(SQL_UPDATE_REFLECTION_DRAFT)){
            statement.setString(1, reflection);
            statement.setString(2, email);
            statement.setInt(3, quest.getLabourId());
            statement.executeUpdate();
        }
    }

    public static void UpdateGivenQuestReflectionToBeFinished(UserQuest quest, String reflection, String email) throws SQLException {
        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement =  connection.prepareStatement(SQL_FINISH_REFLECTION)){
            statement.setString(1, reflection);
            statement.setString(2, email);
            statement.setInt(3, quest.getLabourId());
            statement.executeUpdate();
        }
    }
}

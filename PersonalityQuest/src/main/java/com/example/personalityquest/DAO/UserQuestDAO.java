package com.example.personalityquest.DAO;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.Model.Quest;
import com.example.personalityquest.Model.UserQuest;
import com.example.personalityquest.SQLite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserQuestDAO {
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
                    rs.getString("status")
            );
        }

        return quest;
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
     * @return The new UserQuest that was made
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
     * @return The updated UserQuest with a status complete
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
     * @return The updated UserQuest with the new percentageComplete
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
}

package com.example.personalityquest.Managers;

import com.example.personalityquest.DataClasses.Quest;
import com.example.personalityquest.DataClasses.UserQuest;
import com.example.personalityquest.SQLite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserQuestManager {
    public static UserQuest GetCurrentActiveUserQuestForEmail(String email) throws SQLException {
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

    public static UserQuest SetUserQuesStatusAsActive(UserQuest quest, String email) throws IllegalArgumentException, SQLException {
        if (quest == null || SystemManager.isEmpty(email)){
            return null;
        }
        if (DoesUserHaveActiveQuest(email)){
            throw new IllegalArgumentException("User already has an active quest with labourId "
                    + GetCurrentActiveUserQuestForEmail(email).getLabourId());
        }

        Connection connection = SQLite.getConnection();

        PreparedStatement statement = connection.prepareStatement(
                """
                    UPDATE UserQuests 
                    SET status = 'Active'
                    WHERE accountEmail = ? AND labourId = ?
                    """);
        statement.setString(1, email);
        statement.setInt(2, quest.getLabourId());
        statement.executeUpdate();

        return GetCurrentActiveUserQuestForEmail(email);
    }
    public static UserQuest InsertNewQuestForEmail(Quest quest, String email) throws SQLException {
        if (quest == null || SystemManager.isEmpty(email)){
            throw new IllegalArgumentException("Quest you wanted to set or email is null");
        }

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


            return GetUserQuestForEmailAndLabourId(email, quest.getLabourId());
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }

    public static UserQuest SetUserQuestStatusAsComplete(UserQuest quest, String email) throws SQLException {
        if (quest == null || SystemManager.isEmpty(email)){
            throw new IllegalArgumentException("Quest you wanted to set or email is null");
        }

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


        return GetUserQuestForEmailAndLabourId(email, quest.getLabourId());
    }
    public static UserQuest SetUserQuestToPercentageComplete(UserQuest quest, String email, float percentage) throws SQLException {
        if (quest == null || SystemManager.isEmpty(email)){
            throw new IllegalArgumentException("Quest you wanted to set or email is null");
        }

        if (IsPercentageOutOfRange(percentage)){
            throw new IllegalArgumentException("Percentage is out of the 0-1 range");
        }

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

        return GetUserQuestForEmailAndLabourId(email, quest.getLabourId());
    }

    private static boolean DoesUserHaveActiveQuest(String email) throws IllegalArgumentException, SQLException {
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
    private static boolean IsPercentageOutOfRange(float percentage){
        if (percentage < 0 || percentage > 1){
            return true;
        }

        return false;
    }

    private static UserQuest GetUserQuestForEmailAndLabourId(String email, int labourId) throws SQLException {
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

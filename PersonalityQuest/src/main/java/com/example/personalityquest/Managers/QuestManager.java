package com.example.personalityquest.Managers;

import com.example.personalityquest.DataClasses.Quest;
import com.example.personalityquest.SQLite;
import javafx.collections.ObservableArray;
import jdk.jshell.spi.ExecutionControl;

import java.sql.*;
import java.text.ParseException;
import java.util.Random;
import java.util.jar.JarEntry;

/**
 * This class managers everything to do with quests and has utility functions to retrieve
 * quests matching archetypeId's or a labourId or get the count of how many quests
 * match an archetypeId
 */
public class QuestManager {
    /**
     * Gets an Array of Quests for the given archetypeId
     * @param archetypeId The archetypeId you want to get quests for
     * @return Array of quests matching archetypeId
     * @throws SQLException "Database Access Failure"
     */
    public static Quest[] GetQuestsForArchetypeId(int archetypeId) throws SQLException {
        if (archetypeId == 0){
            throw new IllegalArgumentException("Bad archetypeId of 0");
        }
        int count = GetCountOfQuestsForArchetypeId(archetypeId);

        // no quests exist for this given ID
        if (count == 0){
            return null;
        }

        Connection connection = SQLite.getConnection();
        PreparedStatement getQuestsForArchetypeId = connection.prepareStatement(
                """
                    SELECT * FROM Quests
                    WHERE archetypeId = ?
                    """);
        getQuestsForArchetypeId.setInt(1, archetypeId);

        ResultSet rs = getQuestsForArchetypeId.executeQuery();
        Quest[] quests = new Quest[count];

        int index = 0;
        while (rs.next()){
            if (index == count) {
                System.out.println("Early break for quests as their where more quests then the count that was gotten");
                break;
            }

            quests[index] = new Quest(
                    rs.getInt("labourId"),
                    rs.getInt("archetypeId"),
                    rs.getString("name")
            );

            index++;
        }


        return quests;
    }

    /**
     * Retrives Quest Details for the given questId
     * @param labourId the labourId for the quest you want
     * @return The Quest is successful or null if labourId doesn't match a quest in the database
     * @throws SQLException Database Access Failure
     */
    public static Quest GetQuestForLabourId(int labourId) throws SQLException {
        if (labourId == 0){
            throw new IllegalArgumentException("Bad labourId of 0");
        }
        Connection connection = SQLite.getConnection();
        PreparedStatement getQuestForLabourID = connection.prepareStatement(
                """
                    SELECT * FROM Quests
                    WHERE labourId = ?
                    """);
        getQuestForLabourID.setInt(1, labourId);

        ResultSet rs = getQuestForLabourID.executeQuery();

        Quest quest = null;
        if (rs.next()){
            quest = new Quest(
                    rs.getInt("labourId"),
                    rs.getInt("archetypeId"),
                    rs.getString("name")
            );
        }

        return quest;
    }

    /**
     * Retrives a Random Quest from the database that matches the archetypeId
     * @param archetypeId The archetypeId of the quest you want to retrieve
     * @return The Random Quests information
     * @throws SQLException "Database Access Failure"
     */
    public static Quest GetRandomQuestForArchetypeId(int archetypeId) throws SQLException {
        Quest[] quests = GetQuestsForArchetypeId(archetypeId);

        if (quests == null){
            return null;
        }
        Random random = new Random();

        int randomInt = random.nextInt(0, quests.length);

        return quests[randomInt];
    }

    /**
     * The amount of quests that match the archetypeId
     * @param archetypeId The archetypeId that matches quests you want to retrieve the counts for
     * @return the amount of quests in the database for the archetypeId
     * @throws SQLException Database Access Failure
     */
    private static int GetCountOfQuestsForArchetypeId(int archetypeId) throws SQLException {
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                """
                    SELECT COUNT(*) FROM Quests
                    WHERE archetypeId = ?
                    """);
        statement.setInt(1, archetypeId);
        ResultSet rs = statement.executeQuery();

        int count = 0;
        if (rs.next()){
            count = rs.getInt(1);
        }

        return count;
    }
}

package com.example.personalityquest.Managers;

import com.example.personalityquest.DataClasses.Quest;
import com.example.personalityquest.SQLite;
import javafx.collections.ObservableArray;
import jdk.jshell.spi.ExecutionControl;

import java.sql.*;
import java.text.ParseException;
import java.util.Random;
import java.util.jar.JarEntry;

public class QuestManager {
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

    public static Quest GetRandomQuestForArchetypeId(int archetypeId) throws SQLException {
        Quest[] quests = GetQuestsForArchetypeId(archetypeId);

        if (quests == null){
            return null;
        }
        Random random = new Random();

        int randomInt = random.nextInt(0, quests.length);

        return quests[randomInt];
    }

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

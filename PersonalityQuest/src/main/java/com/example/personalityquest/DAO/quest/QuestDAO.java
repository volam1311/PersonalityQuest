package com.example.personalityquest.DAO.quest;

import com.example.personalityquest.Model.quest.Quest;
import com.example.personalityquest.Model.quiz.Archetype;
import com.example.personalityquest.SQLite;
//import com.example.personalityquest.Model.quest.quiz.Archetype

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

public class QuestDAO {
    /**
     * Gets an Array of Quests for the given archetypeId
     * @param archetypeId The archetypeId you want to get quests for
     * @return Array of quests matching archetypeId
     * @throws SQLException "Database Access Failure"
     */
    public static Quest[] GetQuestsForArchetypeId(int archetypeId) throws SQLException {

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
     * Looks up the display name of an archetype.
     * @param archetypeId The archetype to look up
     * @return The archetype name, or null if no row matches
     * @throws SQLException Database Access Failure
     */
    public static String GetArchetypeName(int archetypeId) throws SQLException {
        for (Archetype archetype : Archetype.values()) {
            if (archetype.getArchetypeId() == archetypeId){
                return archetype.getName();
            }
        }
        return null;
//        Connection connection = SQLite.getConnection();
//        try (PreparedStatement statement = connection.prepareStatement(
//                """
//                    SELECT name FROM Archetype
//                    WHERE archetypeId = ?
//                    """)) {
//            statement.setInt(1, archetypeId);
//            ResultSet rs = statement.executeQuery();
//            if (rs.next()) {
//                return rs.getString("name");
//            }
//            return null;
//        }
    }

    /**
     * Looks up an archetype id from a display name such as "Explorer" or "The Innocent".
     * @param name The archetype name to match
     * @return The archetypeId, or null if none matches
     * @throws SQLException Database Access Failure
     */
    public static Integer GetArchetypeIdForName(String name) throws SQLException {
        //Connection connection = SQLite.getConnection();
        String needle = name.trim().toLowerCase(Locale.ROOT);
        if (needle.startsWith("the ")) {
            needle = needle.substring(4).trim();
        }

        for (Archetype archetype : Archetype.values()) {
            String storedName = archetype.getName().trim().toLowerCase(Locale.ROOT);
            if (storedName.equals(needle)) {
                return archetype.getArchetypeId();
            }
        }
        return null;

//        try (PreparedStatement statement = connection.prepareStatement(
//                """
//                    SELECT archetypeId, name FROM Archetype
//                    """)) {
//            ResultSet rs = statement.executeQuery();
//            Integer fallback = null;
//            while (rs.next()) {
//                String stored = rs.getString("name");
//                if (stored == null || stored.isBlank()) {
//                    continue;
//                }
//
//                String storedName = stored.trim().toLowerCase(Locale.ROOT);
//                if (storedName.startsWith("the ")) {
//                    storedName = storedName.substring(4).trim();
//                }
//
//                if (storedName.equals(needle)) {
//                    return rs.getInt("archetypeId");
//                }
//                if (fallback == null && (storedName.contains(needle) || needle.contains(storedName))) {
//                    fallback = rs.getInt("archetypeId");
//                }
//            }
//            return fallback;
//        }
    }


    /**
     * Looks up the stored description of an archetype.
     * @param archetypeId The archetype to look up
     * @return The description, or null if no row matches
     * @throws SQLException Database Access Failure
     */
    public static String GetArchetypeDescription(int archetypeId) throws SQLException {
        for (Archetype archetype : Archetype.values()) {
            if (archetype.getArchetypeId() == archetypeId){
                return archetype.getSmallDescription();
            }
        }
        return null;
//        Connection connection = SQLite.getConnection();
//        try (PreparedStatement statement = connection.prepareStatement(
//                """
//                    SELECT smallDescription FROM Archetype
//                    WHERE archetypeId = ?
//                    """)) {
//            statement.setInt(1, archetypeId);
//            ResultSet rs = statement.executeQuery();
//            if (rs.next()) {
//                return rs.getString("smallDescription");
//            }
//            return null;
//        }
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

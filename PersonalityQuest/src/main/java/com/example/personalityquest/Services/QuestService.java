package com.example.personalityquest.Services;

import com.example.personalityquest.DAO.QuestDAO;
import com.example.personalityquest.Model.Quest;

import java.sql.SQLException;
import java.util.Random;

/**
 * This class managers everything to do with quests and has utility functions to retrieve
 * quests matching archetypeId's or a labourId or get the count of how many quests
 * match an archetypeId
 */
public class QuestService {
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

        return QuestDAO.GetQuestsForArchetypeId(archetypeId);
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

        return QuestDAO.GetQuestForLabourId(labourId);
    }

    /**
     * Looks up the display name of an archetype.
     * @param archetypeId The archetype to look up
     * @return The archetype name, or "Unknown archetype" if none matches
     * @throws SQLException Database Access Failure
     */
    public static String GetArchetypeName(int archetypeId) throws SQLException {
        if (archetypeId == 0) {
            throw new IllegalArgumentException("Bad archetypeId of 0");
        }

        String name = QuestDAO.GetArchetypeName(archetypeId);
        if (name == null || name.isBlank()) {
            return "Unknown archetype";
        }
        return name;
    }

    /**
     * Looks up the stored description of an archetype.
     * @param archetypeId The archetype to look up
     * @return The description, or an empty string if none is stored
     * @throws SQLException Database Access Failure
     */
    public static String GetArchetypeDescription(int archetypeId) throws SQLException {
        if (archetypeId == 0) {
            throw new IllegalArgumentException("Bad archetypeId of 0");
        }

        String description = QuestDAO.GetArchetypeDescription(archetypeId);
        if (description == null || description.isBlank()) {
            return "";
        }
        return description;
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

}

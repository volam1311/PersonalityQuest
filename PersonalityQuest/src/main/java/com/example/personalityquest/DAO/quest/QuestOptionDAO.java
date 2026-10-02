package com.example.personalityquest.DAO.quest;

import com.example.personalityquest.Model.quest.QuestOption;
import com.example.personalityquest.SQLite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestOptionDAO {
    private static final String CREATE_QUEST_OPTIONS = """
            CREATE TABLE IF NOT EXISTS QuestOptions (
            questOptionId INTEGER PRIMARY KEY,
            labourId INTEGER NOT NULL,
            archetypeId INTEGER NOT NULL,
            reactionType TEXT NOT NULL,
            text TEXT NOT NULL
            )
            """;

    private QuestOptionDAO() {}

    public static void EnsureTables() throws SQLException {
        Connection connection = SQLite.getConnection();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_QUEST_OPTIONS);
        }
    }

    public static boolean HasCatalog() throws SQLException {
        EnsureTables();
        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM QuestOptions")){
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        }
    }

    public static void InsertQuestOption(int questOptionId, int labourId, int archetypeId,
                                         QuestOption.ReactionType reactionType, String text) throws SQLException {
        EnsureTables();
        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT OR IGNORE INTO QuestOptions (questOptionId, labourId, archetypeId, reactionType, text)
                    VALUES (?, ?, ?, ?, ?)
                    """)) {
            statement.setInt(1, questOptionId);
            statement.setInt(2, labourId);
            statement.setInt(3, archetypeId);
            statement.setString(4, reactionType.name());
            statement.setString(5, text);
            statement.executeUpdate();
        }
    }

    public static List<QuestOption> GetQuestOptionsForLabourId(int labourId) throws SQLException {
        EnsureTables();
        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                    """
                        SELECT * FROM QuestOptions
                        WHERE labourId = ?
                        ORDER BY questOptionId
                        """)){
            statement.setInt(1, labourId);
            ResultSet resultSet = statement.executeQuery();
            List<QuestOption> options = new ArrayList<>();
            while (resultSet.next()) {
                options.add(new QuestOption(
                        resultSet.getInt("questOptionId"),
                        resultSet.getInt("labourId"),
                        resultSet.getInt("archetypeId"),
                        QuestOption.ReactionType.valueOf(resultSet.getString("reactionType")),
                        resultSet.getString("text")
                ));
            }
            return options;
        }
    }

    public static void SeedCatalog() throws SQLException {

        // Hero Questions
        InsertQuestOption(1, 3, 3, QuestOption.ReactionType.DEFICIT,
                " Option 1 Deficit");
        InsertQuestOption(2, 3, 3, QuestOption.ReactionType.EXCESS,
                " Option 2 Excess");
        InsertQuestOption(3, 3, 3, QuestOption.ReactionType.DEFICIT,
                " Option 1 Deficit");
        InsertQuestOption(4, 3, 3, QuestOption.ReactionType.EXCESS,
                " Option 2 Excess");


        // Outlaw questions
        InsertQuestOption(45, 12, 12, QuestOption.ReactionType.DEFICIT,
                " Option 1 Deficit");
        InsertQuestOption(46, 12, 12, QuestOption.ReactionType.EXCESS,
                " Option 2 Excess");
        InsertQuestOption(47, 12, 12, QuestOption.ReactionType.DEFICIT,
                " Option 1 Deficit");
        InsertQuestOption(48, 12, 12, QuestOption.ReactionType.EXCESS,
                " Option 2 Excess");
    }

    public static void ResetAndSeedCatalog() throws SQLException {
        Connection connection = SQLite.getConnection();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE IF EXISTS QuestOptions");
        }
        EnsureTables();
        SeedCatalog();
    }
}

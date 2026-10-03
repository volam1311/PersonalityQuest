package com.example.personalityquest.DAO.quest;

import com.example.personalityquest.Model.quest.ReflectionPrompt;
import com.example.personalityquest.SQLite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReflectionPromptDAO {
    private static final String CREATE_REFLECTION_PROMPTS = """
            CREATE TABLE IF NOT EXISTS ReflectionPrompts (
            reflectionId INTEGER PRIMARY KEY,
            labourId INTEGER NOT NULL,
            reactionType TEXT,
            prompt TEXT NOT NULL
            )
            """;

    private ReflectionPromptDAO() {    }

    public static void EnsureTables() throws SQLException {
        Connection connection = SQLite.getConnection();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_REFLECTION_PROMPTS);
        }
    }

    public static boolean HasCatalog() throws SQLException {
        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM ReflectionPrompts")){
            ResultSet rs = statement.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public static void InsertReflectionPrompt(int reflectionId, int labourId, String reactionType,
                                              String prompt) throws SQLException {
        EnsureTables();
        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT OR IGNORE INTO ReflectionPrompts(reflectionId, labourId, reactionType, prompt)
                    VALUES (? , ? , ? , ?)
                    """)){
            statement.setInt(1, reflectionId);
            statement.setInt(2, labourId);
            statement.setString(3, reactionType);
            statement.setString(4, prompt);
            statement.executeUpdate();
        }
    }

    /**
     * @param labourId The labour to fetch reflection prompts for
     * @return The labour's reflection prompts, empty if none are seeded
     * @throws SQLException Database Access Failure
     */
    public static List<ReflectionPrompt> GetReflectionPromptsForLabourId(int labourId) throws SQLException {
        EnsureTables();
        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                """
                    SELECT * FROM ReflectionPrompts 
                    WHERE labourId = ?
                    ORDER BY reflectionId 
                    """)) {
            statement.setInt(1, labourId);
            ResultSet rs = statement.executeQuery();
            List<ReflectionPrompt> reflectionPrompts = new ArrayList<>();
            while (rs.next()) {
                reflectionPrompts.add(new ReflectionPrompt(
                        rs.getInt("reflectionId"),
                        rs.getInt("labourId"),
                        rs.getString("reactionType"),
                        rs.getString("prompt")
                ));
            }
            return reflectionPrompts;
        }
    }

    public static void SeedCatalog() throws SQLException {

        InsertReflectionPrompt(23, 12, "DEFICIT",
                "Deficit Reflection Prompt");

        InsertReflectionPrompt(24, 12, "EXCESS",
                "Excess Reflection Prompt");
    }

    public static void ResetAndSeedCatalog() throws SQLException {
        Connection connection = SQLite.getConnection();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE IF EXISTS ReflectionPrompts");
        }
        EnsureTables();
        SeedCatalog();
    }
}

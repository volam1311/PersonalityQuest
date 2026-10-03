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


        // Hero prompts
        InsertReflectionPrompt(25, 3, "EXCESS",
                "Hercules felt the fear and acted anyway, but he didn't charge blindly — he changed the ground to one where he could win (Options 1 & 2). Think to a time you met something head-on without stopping to choose your approach, and it cost you more than it needed to. Looking back, what would picking your ground first have changed?\n\nGoing forward, where is one challenge you can meet with the same drive, but aimed with better judgment?");

        InsertReflectionPrompt(26, 3, "DEFICIT",
                "Hercules' weapons failed him, and the safe choice was to hold back and wait — but the lion would never have tired, and waiting would have cost him the fight (Option 3). Think to a time you held at a safe distance from something you knew you had to face — what were you waiting to feel before you'd commit?\n\nGoing forward, when is holding back genuine good judgment, and when is it just fear choosing for you?");

        // Everyman prompts
        InsertReflectionPrompt(21, 2, "DEFICIT",
                "Hercules had done the work and earned the reward, yet the easy path was to let a king's word and a hometown crowd settle it without a fight (Option 3). Think to a time you let the group decide for you because standing out felt too costly — what were you afraid would happen if you spoke up?\n\nGoing forward, when does keeping the peace genuinely serve everyone, and when is it just your own voice going missing?");

        InsertReflectionPrompt(22, 2, "EXCESS",
                "Hercules did the labour fairly and in the open, with a witness beside him — his strength was never meant to stand above the bond of a fair deal (Options 1 & 2). Think to a time you leaned on your own standing to win something, when the fairer path was to let the truth or another voice carry it. Looking back, what did relying on your own weight cost the people around you?\n\nGoing forward, where is one place you can win with others on level footing rather than above them?");

        // Innocent prompts
        InsertReflectionPrompt(27, 1, "EXCESS",
                "You already have a massive twelve labours to complete, and there is no telling how much extra work these gods will make you do (Option 1). But in the same light, Apollo and Artemis are too the children of Zeus, and there is no telling how they will react to this slight (Option 2). Think to a time you hastily chose or committed to a path that felt \"simple\" yet had unforeseen consequences — how did you hope this would turn out, and what made you skip past the warning signs there was more involved?\n\nGoing forward, what is one specific thing you can do to keep your hopefulness grounded — to hold onto faith that things can work out, while still seeing clearly what you're doing?");

        InsertReflectionPrompt(28, 1, "DEFICIT",
                "Think to the challenge, where you chose to brace for conflict instead of trusting it might go well — like squaring up to Artemis to win the hind. What past experience taught you to expect the worst here, and what were you protecting yourself from?\n\nGoing forward, when is that caution genuinely serving you, and when is it costing you good outcomes you never gave a chance?");


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

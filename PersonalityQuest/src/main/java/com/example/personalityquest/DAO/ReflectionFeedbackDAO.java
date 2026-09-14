package com.example.personalityquest.DAO;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.SQLite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Stores AI coaching replies against a weekly task reflection.
 */
public final class ReflectionFeedbackDAO {
    private static final String CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS ReflectionFeedback (
                accountEmail TEXT NOT NULL,
                taskId INTEGER NOT NULL,
                weekStart TEXT NOT NULL,
                feedback TEXT NOT NULL,
                PRIMARY KEY (accountEmail, taskId, weekStart)
            )
            """;

    private static final String UPSERT = """
            INSERT INTO ReflectionFeedback (accountEmail, taskId, weekStart, feedback)
            VALUES (?, ?, ?, ?)
            ON CONFLICT(accountEmail, taskId, weekStart)
            DO UPDATE SET feedback = excluded.feedback
            """;

    private static final String FIND = """
            SELECT feedback FROM ReflectionFeedback
            WHERE accountEmail = ? AND taskId = ? AND weekStart = ?
            """;

    private ReflectionFeedbackDAO() {
    }

    /**
     * Creates the feedback table when it does not already exist.
     */
    public static void EnsureTables() throws SQLException {
        Connection connection = SQLite.getConnection();
        try (Statement statement = connection.createStatement()) {
            statement.execute(CREATE_TABLE);
        }
    }

    /**
     * Saves or replaces AI feedback for a weekly task.
     */
    public static void Save(String email, int taskId, String weekStart, String feedback) throws SQLException {
        EnsureTables();
        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(UPSERT)) {
            statement.setString(1, email);
            statement.setInt(2, taskId);
            statement.setString(3, weekStart);
            statement.setString(4, feedback);
            statement.executeUpdate();
        }
    }

    /**
     * Loads previously stored AI feedback, or an empty string when none exists.
     */
    public static String Find(String email, int taskId, String weekStart) throws SQLException {
        EnsureTables();
        if (ApplicationManager.isEmpty(email) || ApplicationManager.isEmpty(weekStart) || taskId == 0) {
            return "";
        }

        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(FIND)) {
            statement.setString(1, email);
            statement.setInt(2, taskId);
            statement.setString(3, weekStart);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String feedback = resultSet.getString("feedback");
                return feedback == null ? "" : feedback;
            }
        }
        return "";
    }
}

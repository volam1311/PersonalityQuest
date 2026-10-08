package com.example.personalityquest.DAO.chat;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.DAO.ParentDAO;
import com.example.personalityquest.SQLite;
import com.example.personalityquest.Services.chat.ReflectionFeedbackService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Stores AI coaching replies against a weekly task reflection.
 */
public final class ReflectionFeedbackDAO extends ParentDAO {
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


    public ReflectionFeedbackDAO() {
        super();
    }

    public ReflectionFeedbackDAO(Connection connection){
        super(connection);
    }

    /**
     * Creates the feedback table when it does not already exist.
     */
    public void EnsureTables() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(CREATE_TABLE);
        }
    }

    /**
     * Saves or replaces AI feedback for a weekly task.
     */
    public void Save(String email, int taskId, String weekStart, String feedback) throws SQLException {
        EnsureTables();
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
    public String Find(String email, int taskId, String weekStart) throws SQLException {
        EnsureTables();
        if (ApplicationManager.isEmpty(email) || ApplicationManager.isEmpty(weekStart) || taskId == 0) {
            return "";
        }

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

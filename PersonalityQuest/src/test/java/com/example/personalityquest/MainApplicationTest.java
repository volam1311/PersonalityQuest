package com.example.personalityquest;

import com.example.personalityquest.DAO.quest.UserQuestDAO;
import com.example.personalityquest.SQLite;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class MainApplicationTest {
    private Connection connection;

    @AfterEach
    void tearDown() throws SQLException {
        SQLite.setConnection(null);
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    void startupPreservesExistingUserQuestProgress() throws Exception {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        SQLite.setConnection(connection);
        UserQuestDAO UserQuestDAO = new UserQuestDAO(connection);

        UserQuestDAO.EnsureTables();

        try (PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO UserQuests
                    (accountEmail, labourId, percentageComplete, status)
                    VALUES (?, ?, ?, ?)
                    """)) {
            statement.setString(1, "test@example.com");
            statement.setInt(2, 20);
            statement.setDouble(3, 0.25);
            statement.setString(4, "Active");
            statement.executeUpdate();
        }

        MainApplication.initialiseUserQuestData();

        try (Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM UserQuests")) {
            result.next();
            assertEquals(1, result.getInt(1));
        }
    }
}

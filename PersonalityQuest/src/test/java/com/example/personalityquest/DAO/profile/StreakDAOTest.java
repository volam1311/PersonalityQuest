package com.example.personalityquest.DAO.profile;

import com.example.personalityquest.SQLite;
import com.example.personalityquest.DAO.profile.StreakDAO;
import com.example.personalityquest.Model.profile.StreakProgress;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class StreakDAOTest {
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE Accounts (
                        email TEXT NOT NULL,
                        userName TEXT NOT NULL,
                        firstName TEXT NOT NULL,
                        lastName TEXT NOT NULL,
                        password TEXT NOT NULL,
                        PRIMARY KEY(email)
                    )
                    """);
            statement.execute("""
                    CREATE TABLE UserProgress (
                        accountEmail TEXT PRIMARY KEY,
                        currentStreak INTEGER NOT NULL DEFAULT 0,
                        bestStreak INTEGER NOT NULL DEFAULT 0,
                        lastCompletionDate TEXT,
                        totalQuestsCompleted INTEGER NOT NULL DEFAULT 0,
                        FOREIGN KEY (accountEmail) REFERENCES Accounts(email)
                    )
                    """);
            statement.execute("""
                    INSERT INTO Accounts
                        (email, userName, firstName, lastName, password)
                    VALUES
                        ('test', 'test', 'Test', 'User', 'password')
                    """);
        }

        SQLite.setConnection(connection);

    }

    @AfterEach
    void tearDown() throws SQLException {
        if (!connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void getProgressCreatesDefaultProgressRow() throws SQLException {
        StreakProgress progress = StreakDAO.GetProgress(
                "test"
        );

        assertNotNull(progress);
        assertEquals(0, progress.GetCurrentStreak());
        assertEquals(0, progress.GetBestStreak());
        assertNull(progress.GetLastCompletionDate());
    }

    @Test
    void saveProgressPersists() throws SQLException {
         StreakDAO.SaveProgress(
                "test",
                1,
                1,
                LocalDate.of(2000, 1, 1)
        );

        StreakProgress progress = StreakDAO.GetProgress("test");

        assertNotNull(progress);
        assertEquals(1, progress.GetCurrentStreak());
        assertEquals(1, progress.GetBestStreak());
        assertEquals(
                LocalDate.of(2000, 1, 1),
                progress.GetLastCompletionDate()
        );
    }

    @Test
    public void saveProgressCanUpdateExistingValues()
            throws SQLException {

        StreakDAO.SaveProgress(
                "test",
                1,
                1,
                LocalDate.of(2000, 1, 1)
        );

        StreakDAO.SaveProgress(
                "test",
                2,
                2,
                LocalDate.of(2000, 1, 2)
        );

        StreakProgress progress = StreakDAO.GetProgress("test");

        assertNotNull(progress);
        assertEquals(2, progress.GetCurrentStreak());
        assertEquals(2, progress.GetBestStreak());
        assertEquals(
                LocalDate.of(2000, 1, 2),
                progress.GetLastCompletionDate()
        );
    }

    @Test
    void returnNullForInvalidEmail() throws SQLException {
        StreakProgress progress = StreakDAO.GetProgress(null);
        assertNull(progress);
    }

    @Test
    void returnNullForUnknownEmail() throws SQLException {
        StreakProgress progress = StreakDAO.GetProgress("unknown_email");
        assertNull(progress);
    }

    @Test
    void saveProgressDoesNothingForUnknownEmail() throws SQLException {
        StreakDAO.SaveProgress(
                "unknown_email",
                1,
                1,
                LocalDate.of(2000, 1, 1)
        );

        assertNull(StreakDAO.GetProgress("unknown_email"));
    }

    @Test
    void saveProgressDoesNothingForInvalidEmail() throws SQLException {

        assertDoesNotThrow(() ->
                StreakDAO.SaveProgress(
                        null,
                        1,
                        1,
                        LocalDate.of(2000, 1, 1)
                )
        );
    }

    @Test
    void GetTotalQuestsCompleted() throws SQLException {
        StreakDAO.SaveProgress(
                "test",
                1,
                1,
                LocalDate.of(2000, 1, 1)
        );
        int amount = StreakDAO.GetTotalQuestsCompleted("test");
        assertEquals(0, amount);
    }

    @Test
    void IncreaseTotalQuestsCompleted() throws SQLException {
        StreakDAO.SaveProgress(
                "test",
                1,
                1,
                LocalDate.of(2000, 1, 1)
        );
        StreakDAO.SetTotalQuestsCompleted("test", 2);
        assertEquals(2, StreakDAO.GetTotalQuestsCompleted("test"));
    }
}


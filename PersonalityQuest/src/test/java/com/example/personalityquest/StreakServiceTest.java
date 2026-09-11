package com.example.personalityquest;

import com.example.personalityquest.Services.StreakService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StreakServiceTest {
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
    void firstCompletionStartsStreakAtOne() throws SQLException {
        int streak = StreakService.RecordCompletion(
                "test",
                LocalDate.of(2000,1,1)
        );

        assertEquals(1, streak);
    }

    @Test
    void completionOnNextDayIncreasesStreak() throws SQLException {
        StreakService.RecordCompletion(
                "test",
                LocalDate.of(2000,1,1)
        );

        int streak = StreakService.RecordCompletion(
                "test",
                LocalDate.of(2000,1,2)
        );

        assertEquals(2, streak);
    }

    @Test
    void missingDayResetsStreak() throws SQLException {
        StreakService.RecordCompletion(
                "test",
                LocalDate.of(2000,1,1)
        );

        StreakService.RecordCompletion(
                "test",
                LocalDate.of(2000,1,2)
        );

        int streak = StreakService.RecordCompletion(
                "test",
                LocalDate.of(2000,1,4)
        );

        assertEquals(1, streak);
    }

    @Test
    void bestStreakKeptAfterCurrentStreakResets() throws SQLException {
        StreakService.RecordCompletion(
                "test",
                LocalDate.of(2000,1,1)
        );

        StreakService.RecordCompletion(
                "test",
                LocalDate.of(2000,1,2)
        );

        int streak = StreakService.RecordCompletion(
                "test",
                LocalDate.of(2000,1,4)
        );

        int bestStreak = StreakService.GetBestStreak(
                "test"
        );

        assertEquals(2, bestStreak);
    }

    @Test
    void currentStreakBecomesZeroAfterMissedDay() throws SQLException {
        StreakService.RecordCompletion(
                "test",
                LocalDate.of(2000, 1, 1)
        );

        int streak = StreakService.GetCurrentStreak(
                "test",
                LocalDate.of(2000, 1, 3)
        );

        assertEquals(0, streak);
    }

    @Test
    void noEmailReturnsZero() throws SQLException {
        int streak = StreakService.RecordCompletion(
                null,
                LocalDate.of(2000,1,1)
        );

        assertEquals(0, streak);
    }

    @Test
    void invalidEmailReturnsZero() throws SQLException {
        int streak = StreakService.RecordCompletion(
                "wrong_email",
                LocalDate.of(2000,1,1)
        );

        assertEquals(0, streak);
    }

    @Test
    void nullDateReturnsZero() throws SQLException {
        int streak = StreakService.RecordCompletion(
                "test",
                null
        );

        assertEquals(0, streak);
    }
}


package com.example.personalityquest.Services.profile;

import com.example.personalityquest.DAO.profile.StreakDAO;
import com.example.personalityquest.SQLite;
import com.example.personalityquest.Services.profile.StreakService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

public class StreakServiceTest {
    private Connection connection;

    private StreakService StreakService;
    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        StreakDAO streakDAO = new StreakDAO(connection);
        StreakService = new StreakService(streakDAO);

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
    void firstCompletionStartsStreakAtOne() throws SQLException {
        int streak = StreakService.RecordCompletion(
                "test",
                LocalDate.of(2000,1,1)
        );

        assertEquals(1, streak);
    }

    @Test
    void completionInNextWeekIncreasesStreak() throws SQLException {
        StreakService.RecordCompletion(
                "test",
                LocalDate.of(2000, 1, 3)
        );

        int streak = StreakService.RecordCompletion(
                "test",
                LocalDate.of(2000, 1, 10)
        );

        assertEquals(2, streak);
    }

    @Test
    void completingTwiceInTheSameWeekDoesNotIncreaseStreak()
            throws SQLException {
        StreakService.RecordCompletion(
                "test",
                LocalDate.of(2000, 1, 3)
        );

        int streak = StreakService.RecordCompletion(
                "test",
                LocalDate.of(2000, 1, 9)
        );

        assertEquals(1, streak);
    }

    @Test
    void missingWeekResetsStreak() throws SQLException {
        StreakService.RecordCompletion(
                "test",
                LocalDate.of(2000, 1, 3)
        );

        StreakService.RecordCompletion(
                "test",
                LocalDate.of(2000, 1, 10)
        );

        int streak = StreakService.RecordCompletion(
                "test",
                LocalDate.of(2000, 1, 24)
        );

        assertEquals(1, streak);
    }

    @Test
    void bestStreakKeptAfterCurrentStreakResets() throws SQLException {
        StreakService.RecordCompletion(
                "test",
                LocalDate.of(2000, 1, 3)
        );

        StreakService.RecordCompletion(
                "test",
                LocalDate.of(2000, 1, 10)
        );

        int streak = StreakService.RecordCompletion(
                "test",
                LocalDate.of(2000, 1, 24)
        );

        int bestStreak = StreakService.GetBestStreak(
                "test"
        );

        assertEquals(2, bestStreak);
    }

    @Test
    void currentStreakBecomesZeroAfterMissedWeek() throws SQLException {
        StreakService.RecordCompletion(
                "test",
                LocalDate.of(2000, 1, 3)
        );

        int streak = StreakService.GetCurrentStreak(
                "test",
                LocalDate.of(2000, 1, 24)
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

    @Test
    void GetTotalQuestsCompletedWithNullEmail() throws SQLException {
        assertThrowsExactly(IllegalArgumentException.class, () -> StreakService.GetTotalQuestsCompleted(null));
    }

    @Test
    void GetTotalQuestsCompletedWithBadEmail() throws SQLException {
        StreakService.RecordCompletion(
                "test",
                LocalDate.of(2000, 1, 1)
        );

        int amount = StreakService.GetTotalQuestsCompleted("bad");
        assertEquals(0, amount);
    }

    @Test
    void IncreaseTotalQuestsCompleted() throws SQLException {
        StreakService.RecordCompletion(
                "test",
                LocalDate.of(2000, 1, 1)
        );


        int currentTotal = StreakService.GetTotalQuestsCompleted("test");
        int totalQuests = StreakService.IncreaseTotalQuestsCompleted("test");

        assertEquals(currentTotal + 1, totalQuests);
    }

    @Test
    void IncreaseTotalQuestsCompletedWithBadEmail() throws SQLException {
        StreakService.RecordCompletion(
                "test",
                LocalDate.of(2000, 1, 1)
        );

        int totalQuests = StreakService.IncreaseTotalQuestsCompleted("bad");

        assertEquals(0, totalQuests);
    }

    @Test
    void IncreaseTotalQuestsCompletedWithNullEmail() throws SQLException {
        assertThrowsExactly(IllegalArgumentException.class, () -> StreakService.GetTotalQuestsCompleted(null));
    }
}


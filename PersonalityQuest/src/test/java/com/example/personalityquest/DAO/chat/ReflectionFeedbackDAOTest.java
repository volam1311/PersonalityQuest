package com.example.personalityquest.DAO.chat;

import com.example.personalityquest.SQLite;
import com.example.personalityquest.DAO.chat.ReflectionFeedbackDAO;
import com.example.personalityquest.Model.quest.WeeklyTask;
import com.example.personalityquest.Services.chat.ReflectionFeedbackService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Ref;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReflectionFeedbackDAOTest {
    private Connection connection;

    private ReflectionFeedbackDAO ReflectionFeedbackDAO;
    private ReflectionFeedbackService ReflectionFeedbackService;
    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        ReflectionFeedbackDAO = new ReflectionFeedbackDAO(connection);
        ReflectionFeedbackService = new ReflectionFeedbackService(ReflectionFeedbackDAO);
        SQLite.setConnection(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    void SaveAndFindRoundTrip() throws Exception {
        ReflectionFeedbackDAO.Save("test@example.com", 12, "2026-09-07", "Keep going.");
        assertEquals("Keep going.", ReflectionFeedbackDAO.Find("test@example.com", 12, "2026-09-07"));
    }

    @Test
    void SaveReplacesExistingFeedback() throws Exception {
        ReflectionFeedbackDAO.Save("test@example.com", 12, "2026-09-07", "First");
        ReflectionFeedbackDAO.Save("test@example.com", 12, "2026-09-07", "Second");
        assertEquals("Second", ReflectionFeedbackDAO.Find("test@example.com", 12, "2026-09-07"));
    }

    @Test
    void FindReturnsEmptyWhenMissing() throws SQLException {
        assertEquals("", ReflectionFeedbackDAO.Find("missing@example.com", 1, "2026-09-07"));
        assertEquals("", ReflectionFeedbackDAO.Find("", 1, "2026-09-07"));
    }

    @Test
    void ServiceSaveAndFindUseTheWeeklyTaskKey() throws Exception {
        WeeklyTask weeklyTask = new WeeklyTask("test@example.com", 12, "Finished", "I reflected.", "2026-09-07");
        ReflectionFeedbackService.Save(weeklyTask, "  Strong effort.  ");
        assertEquals("Strong effort.", ReflectionFeedbackService.Find(weeklyTask));
        assertEquals("", ReflectionFeedbackService.Find(null));
    }
}

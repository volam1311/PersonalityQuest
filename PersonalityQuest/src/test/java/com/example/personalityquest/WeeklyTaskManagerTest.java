package com.example.personalityquest;

import com.example.personalityquest.DataClasses.Task;
import com.example.personalityquest.DataClasses.WeeklyTask;
import com.example.personalityquest.Managers.HashingManager;
import com.example.personalityquest.Managers.WeeklyTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class WeeklyTaskManagerTest {
    private Connection connection;

    private Task[] dummyTasks;



    @BeforeEach
    public void setUp() throws SQLException {
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
                    CREATE TABLE Tasks (
                        taskID INT PRIMARY KEY,
                        name TEXT NOT NULL,
                        description TEXT NOT NULL,
                        labourID INT NOT NULL
                    )
                    """);

            statement.execute("""
                    CREATE TABLE WeeklyTasks (
                        id INT PRIMARY KEY,
                        accountEmail TEXT NOT NULL,
                        taskID INT NOT NULL,
                        status TEXT NOT NULL,
                        reflection TEXT,
                        weekStart TEXT NOT NULL
                    )
                    """);

        }
        try (PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO Accounts
                        (email, userName, firstName, lastName, password)
                    VALUES ("test", "test", "test", "test", ?)
                    """))
        {
            String hashedPassword = HashingManager.Hash("test");
            statement.setString(1, hashedPassword);

            statement.executeUpdate();
        }

        PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO Tasks
                        (taskId, name, description, labourID)
                    VALUES (9999, "TestTask", "This is a Test Task", 1)
                    """);
        statement.executeUpdate();
        SQLite.setConnection(connection);
    }

    @Test
    public void RetriveTasks() throws Exception {
        dummyTasks = new Task[] {
                new Task(9999, "TestTask", "This is a Test Task", 1)
        };

        LocalDate localDate = LocalDate.now();
        LocalDate weekStart = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO WeeklyTasks
                        (accountEmail, taskId, status, weekStart)
                    VALUES ("test", 9999, "NotStarted", ?);
                    """);
        statement.setString(1, String.valueOf(weekStart));
        statement.executeUpdate();

        WeeklyTask[] tasks = WeeklyTaskManager.GetTasksForEmailForThisWeek("test");

        assertTrue(Objects.equals(dummyTasks[0].getTaskId(), tasks[0].getTaskId()));
    }

    @Test
    public void NoTasksExist() throws Exception {
        dummyTasks = new Task[] {
                new Task(9999, "TestTask", "This is a Test Task", 1)
        };

        WeeklyTask[] tasks = WeeklyTaskManager.GetTasksForEmailForThisWeek("test");
        assertNull(tasks);
    }

    @Test
    public void GenerateTasksIfNull() throws Exception {
        dummyTasks = new Task[] {
                new Task(9999, "TestTask", "This is a Test Task", 1)
        };

        WeeklyTaskManager.SetDefaultTaskSearchNum(9999);
        WeeklyTask[] tasks = WeeklyTaskManager.GetTasksForEmailForThisWeek("test");

        if (Objects.equals(tasks, null)){
            System.out.println("Generating New Tasks");
            tasks = WeeklyTaskManager.GenerateTasksForThisWeek("test");
        }
        assertTrue(Objects.equals(dummyTasks[0].getTaskId(), tasks[0].getTaskId()));
    }

    @Test
    public void UpdateTaskToDraft() throws Exception {
        dummyTasks = new Task[] {
                new Task(9999, "TestTask", "This is a Test Task", 1)
        };

        LocalDate localDate = LocalDate.now();
        LocalDate weekStart = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO WeeklyTasks
                        (accountEmail, taskId, status, weekStart)
                    VALUES ("test", 9999, "NotStarted", ?);
                    """);
        statement.setString(1, String.valueOf(weekStart));
        statement.executeUpdate();

        WeeklyTask task = WeeklyTaskManager.GetWeeklyTask("test", 9999, weekStart);

        WeeklyTaskManager.UpdateGivenTaskToDraft(task, "New Reflection", "test");

        task = WeeklyTaskManager.GetWeeklyTask("test", 9999, weekStart);

        assertEquals("New Reflection", task.getReflection());
    }
}

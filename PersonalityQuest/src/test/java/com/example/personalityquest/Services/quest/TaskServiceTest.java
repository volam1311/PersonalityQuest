package com.example.personalityquest.Services.quest;

import com.example.personalityquest.SQLite;
import com.example.personalityquest.Model.quest.Task;
import com.example.personalityquest.Model.quest.WeeklyTask;
import com.example.personalityquest.Services.auth.HashingService;
import com.example.personalityquest.Services.quest.TaskService;
import com.example.personalityquest.Services.quest.WeeklyTaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class TaskServiceTest {
    private Connection connection;

    private Task[] dummyTasks = new Task[3];



    @BeforeEach
    public void setUp() throws Exception {
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
                        labourID INT NOT NULL,
                        taskType TEXT NOT NULL DEFAULT 'QUEST'
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
            String hashedPassword = HashingService.Hash("test");
            statement.setString(1, hashedPassword);

            statement.executeUpdate();
        }

        PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO Tasks
                        (taskId, name, description, labourID)
                    VALUES (7777, "TestTask", "This is a Test Task", 1)
                    """);
        statement.executeUpdate();

        statement = connection.prepareStatement(
                """
                    INSERT INTO Tasks
                        (taskId, name, description, labourID)
                    VALUES (8888, "TestTask2", "This is a 2nd Test Task", 2)
                    """);
        statement.executeUpdate();

        statement = connection.prepareStatement(
                """
                    INSERT INTO Tasks
                        (taskId, name, description, labourID)
                    VALUES (9999, "TestTask3", "This is a 3rd Test Task", 2)
                    """);
        statement.executeUpdate();

        dummyTasks[0] = new Task(7777, "TestTask", "This is a Test Task", 1);
        dummyTasks[1] = new Task(8888, "TestTask2", "This is a 2nd Test Task", 2);
        dummyTasks[2] = new Task(9999, "TestTask3", "This is a 3rd Test Task", 2);
        SQLite.setConnection(connection);
    }


    @Test
    public void GetTaskForId() throws Exception {
        Task task = TaskService.GetTaskForId(7777);

        assertEquals(dummyTasks[0].getTaskId(), task.getTaskId());
    }

    @Test
    public void GetTaskForIdThatDoesntExist() {

        assertThrowsExactly(Exception.class, () -> TaskService.GetTaskForId(1000));
    }

    @Test
    public void GetTaskForNullTaskId() {

        assertThrowsExactly(IllegalArgumentException.class, () -> TaskService.GetTaskForId(0));
    }

    @Test
    public void GetTasksForLabourId() throws Exception {
        List<Task> tasks = TaskService.GetTasksForLabourId(1);

        assertEquals(dummyTasks[0].getTaskId(), tasks.get(0).getTaskId());
        assertEquals(dummyTasks[0].getName(), tasks.get(0).getName());
        assertEquals(dummyTasks[0].getDescription(), tasks.get(0).getDescription());
        assertEquals(dummyTasks[0].getLabourId(), tasks.get(0).getLabourId());
    }

    @Test
    public void GetTasksForLabourIdWithMultipleResults() throws Exception {
        List<Task> tasks = TaskService.GetTasksForLabourId(2);


        assertEquals(dummyTasks[1].getTaskId(), tasks.get(0).getTaskId());
        assertEquals(dummyTasks[1].getName(), tasks.get(0).getName());
        assertEquals(dummyTasks[1].getDescription(), tasks.get(0).getDescription());
        assertEquals(dummyTasks[1].getLabourId(), tasks.get(0).getLabourId());


        assertEquals(dummyTasks[2].getTaskId(), tasks.get(1).getTaskId());
        assertEquals(dummyTasks[2].getName(), tasks.get(1).getName());
        assertEquals(dummyTasks[2].getDescription(), tasks.get(1).getDescription());
        assertEquals(dummyTasks[2].getLabourId(), tasks.get(1).getLabourId());
    }

    @Test
    public void GetTasksForLabourIdThatHasNoTasks() throws Exception {
        List<Task> tasks = TaskService.GetTasksForLabourId(75);

        assertEquals(0, tasks.size());
    }

    @Test
    public void GetTasksForNullLabourId()  {

        assertThrowsExactly(IllegalArgumentException.class, () -> TaskService.GetTasksForLabourId(0));
    }
    @Test
    public void GetRandomTaskIdsForNullLabourId() {
        assertThrowsExactly(IllegalArgumentException.class, () -> TaskService.GetRandomAmountOfTaskIdsForLabourId(0));

    }

    @Test
    public void GetRandomTasksForLabourId() throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO Tasks
                        (taskId, name, description, labourID, taskType)
                    VALUES (?, ?, ?, 2, 'WEEKLY')
                    """)) {
            int[] ids = { 8001, 8002 };
            for (int id : ids) {
                statement.setInt(1, id);
                statement.setString(2, "Weekly " + id);
                statement.setString(3, "Weekly description " + id);
                statement.executeUpdate();
            }
        }

        int[] tasks = TaskService.GetRandomAmountOfTaskIdsForLabourId(2);

        for(int task: tasks){
            if (task != 8001 && task != 8002){
                throw new Exception("Didnt have correct Ids");
            }
        }

    }

    @Test
    public void GetRandomTasksForLabourIdThatHasNoTasks() throws Exception {
        int[] tasks = TaskService.GetRandomAmountOfTaskIdsForLabourId(75);

        assertEquals(0, tasks.length);

    }

    @Test
    public void GetTaskIdsForLabourWeekWrapsWhenTheLastWeekIsShort() throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO Tasks
                        (taskId, name, description, labourID)
                    VALUES (?, ?, ?, 20)
                    """)) {
            int[] ids = { 101, 102, 103, 104 };
            for (int id : ids) {
                statement.setInt(1, id);
                statement.setString(2, "Task " + id);
                statement.setString(3, "Description " + id);
                statement.executeUpdate();
            }
        }

        try (PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO Tasks
                        (taskId, name, description, labourID, taskType)
                    VALUES (?, ?, ?, 20, 'WEEKLY')
                    """)) {
            int[] ids = { 201, 202, 203, 204 };
            for (int id : ids) {
                statement.setInt(1, id);
                statement.setString(2, "Weekly " + id);
                statement.setString(3, "Weekly description " + id);
                statement.executeUpdate();
            }
        }

        assertArrayEquals(new int[] { 201, 202, 203 }, TaskService.GetTaskIdsForLabourWeek(20, 1));
        assertArrayEquals(new int[] { 204, 201, 202 }, TaskService.GetTaskIdsForLabourWeek(20, 2));
        assertArrayEquals(new int[] { 201, 202, 203 }, TaskService.GetTaskIdsForLabourWeek(20, 3));
    }

    @Test
    public void GetTasksForLabourIdIgnoresWeeklyPractices() throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO Tasks
                        (taskId, name, description, labourID, taskType)
                    VALUES (5001, "Weekly Practice", "Do this week", 1, 'WEEKLY')
                    """)) {
            statement.executeUpdate();
        }

        List<Task> questTasks = TaskService.GetTasksForLabourId(1);

        assertEquals(1, questTasks.size());
        assertEquals(dummyTasks[0].getTaskId(), questTasks.get(0).getTaskId());
    }
}

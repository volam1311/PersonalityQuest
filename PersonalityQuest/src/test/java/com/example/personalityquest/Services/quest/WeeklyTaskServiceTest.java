package com.example.personalityquest.Services.quest;

import com.example.personalityquest.SQLite;
import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.DAO.quest.WeeklyTaskDAO;
import com.example.personalityquest.Model.quest.Task;
import com.example.personalityquest.Model.quest.WeeklyTask;
import com.example.personalityquest.Services.auth.HashingService;
import com.example.personalityquest.Services.quest.WeeklyTaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class WeeklyTaskServiceTest {
    private Connection connection;

    private Task[] dummyTasks;

    private WeeklyTaskService WeeklyTaskService;


    @BeforeEach
    public void setUp() throws SQLException {
        WeeklyTaskDAO dao = new WeeklyTaskDAO(DriverManager.getConnection("jdbc:sqlite::memory:"));
        WeeklyTaskService = new WeeklyTaskService(dao);
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
                        overview TEXT NOT NULL DEFAULT '',
                        labourID INT NOT NULL,
                        taskType TEXT NOT NULL DEFAULT 'QUEST'
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
            String hashedPassword = HashingService.Hash("test");
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

    /// RETRIEVING TASKS
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

        WeeklyTask[] tasks = WeeklyTaskService.GetTasksForEmailForThisWeek("test");

        assertTrue(Objects.equals(dummyTasks[0].getTaskId(), tasks[0].getTaskId()));
    }
    @Test
    public void NoTasksExist() throws Exception {
        dummyTasks = new Task[] {
                new Task(9999, "TestTask", "This is a Test Task", 1)
        };

        WeeklyTask[] tasks = WeeklyTaskService.GetTasksForEmailForThisWeek("test");
        assertNull(tasks);
    }
    @Test
    public void GettingTasksForThisWeekWithNullEmail() throws Exception {
        assertThrowsExactly(IllegalArgumentException.class, () -> WeeklyTaskService.GetTasksForEmailForThisWeek(null));
    }

    /// GENERATING
    @Test
    public void GeneratingWeeklyTasksIfNull() throws Exception {
        dummyTasks = new Task[] {
                new Task(9999, "TestTask", "This is a Test Task", 1)
        };

        ApplicationManager.TaskConfig.setDefaultSearchNum(9999);
        WeeklyTask[] tasks = WeeklyTaskService.GetTasksForEmailForThisWeek("test");

        if (Objects.equals(tasks, null)){
            System.out.println("Generating New Tasks");
            tasks = WeeklyTaskService.GenerateTasksForThisWeek("test");
        }
        assertTrue(Objects.equals(dummyTasks[0].getTaskId(), tasks[0].getTaskId()));
    }
    @Test
    public void GeneratingWeeklyTasksWithEmailThatDoesntExist() throws Exception {
        dummyTasks = new Task[] {
                new Task(9999, "TestTask", "This is a Test Task", 1)
        };

        ApplicationManager.TaskConfig.setDefaultSearchNum(9999);
        WeeklyTask[] tasks = WeeklyTaskService.GetTasksForEmailForThisWeek("test");

        if (Objects.equals(tasks, null)){
            System.out.println("Generating New Tasks");
            assertThrowsExactly(IllegalArgumentException.class, () -> WeeklyTaskService.GenerateTasksForThisWeek("doesnt exisgjbfjsdahjsd"));
        }
    }
    @Test
    public void GeneratingWeeklyTasksWithTaskIdThatDoesntExist() throws Exception {
        dummyTasks = new Task[] {
                new Task(9999, "TestTask", "This is a Test Task", 1)
        };

        ApplicationManager.TaskConfig.setDefaultSearchNum(17982331);
        WeeklyTask[] tasks = WeeklyTaskService.GetTasksForEmailForThisWeek("test");

        if (Objects.equals(tasks, null)){
            System.out.println("Generating New Tasks");
            assertThrowsExactly(IllegalArgumentException.class, () -> WeeklyTaskService.GenerateTasksForThisWeek("doesnt exisgjbfjsdahjsd"));
        }
        assertNull(tasks);
    }
    @Test
    public void GeneratingWeeklyTasksWithWeeklyStartInTheFuture() throws Exception {
        assertNull(WeeklyTaskService.GetWeeklyTask("test", 9999, LocalDate.MAX));
    }

    /// RETRIEVING WEEKLY TASKS
    @Test
    public void GetWeeklyTask() throws Exception {
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

        WeeklyTask task = WeeklyTaskService.GetWeeklyTask("test", 9999, weekStart);

        assertEquals(task.getEmail(), "test");


    }
    @Test
    public void GetWeeklyTaskThatDoesntExistShouldBeNull() throws Exception {
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

        WeeklyTask task = WeeklyTaskService.GetWeeklyTask("wrong email", 9999, weekStart);

        assertNull(task);


    }
    @Test
    public void GettingWeeklyTaskWithNullEmail() {
        assertThrowsExactly(IllegalArgumentException.class, () -> WeeklyTaskService.GetWeeklyTask(null, 1, LocalDate.now()));
    }
    @Test
    public void GettingWeeklyTaskWithNullWeekStart() throws Exception {
        assertNull(WeeklyTaskService.GetWeeklyTask("test", 9999, null));
    }
    @Test
    public void GettingWeeklyTaskWithTaskIdThatDoesntExist() throws Exception {
        assertNull(WeeklyTaskService.GetWeeklyTask("test", -217371237, LocalDate.now()));
    }
    @Test
    public void GettingWeeklyTaskWithEmailThatDoesntExist() throws Exception {
        assertNull(WeeklyTaskService.GetWeeklyTask("random email ashdahsdhas", 9999, null));
    }

    @Test
    public void GettingWeeklyTaskWithWeeklyStartInTheFuture() throws Exception {
        assertNull(WeeklyTaskService.GetWeeklyTask("test", 9999, LocalDate.MAX));
    }


    /// UPDATING WEEKLY TASKS
    @Test
    public void UpdateWeeklyTaskToDraft() throws Exception {
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

        WeeklyTask task = WeeklyTaskService.GetWeeklyTask("test", 9999, weekStart);

        WeeklyTaskService.UpdateGivenTaskToDraft(task, "New Reflection", "test");

        task = WeeklyTaskService.GetWeeklyTask("test", 9999, weekStart);

        assertEquals("New Reflection", task.getReflection());
    }
    @Test
    public void UpdateWeeklyTaskToDraftThatIsNull() throws Exception {
        assertThrowsExactly(IllegalArgumentException.class, () -> WeeklyTaskService.UpdateGivenTaskToDraft(null, "New Reflection", "test"));
    }
    @Test
    public void UpdatingWeeklyTaskToDraftWithNullEmail() throws Exception {
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

        WeeklyTask task = WeeklyTaskService.GetWeeklyTask("test", 9999, weekStart);

        assertThrowsExactly(IllegalArgumentException.class, () -> WeeklyTaskService.UpdateGivenTaskToDraft(task, "New Reflection", null));
    }
    @Test
    public void UpdatingWeeklyTaskToDraftWithNullReflection() throws Exception {
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

        WeeklyTask task = WeeklyTaskService.GetWeeklyTask("test", 9999, weekStart);

        assertThrowsExactly(IllegalArgumentException.class, () -> WeeklyTaskService.UpdateGivenTaskToDraft(task, null, "test"));
    }

    /// FINISHING WEEKLY TASKS
    @Test
    public void UpdateWeeklyTaskToBeFinished() throws Exception {
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

        WeeklyTask task = WeeklyTaskService.GetWeeklyTask("test", 9999, weekStart);

        WeeklyTaskService.UpdateGivenTaskToBeFinished(task, "New Reflection", "test");

        task = WeeklyTaskService.GetWeeklyTask("test", 9999, weekStart);

        assertEquals("New Reflection", task.getReflection());
    }
    @Test
    public void UpdateWeeklyTaskToBeFinishedThatIsNull() throws Exception {
        assertThrowsExactly(IllegalArgumentException.class, () -> WeeklyTaskService.UpdateGivenTaskToBeFinished(null, "New Reflection", "test"));
    }
    @Test
    public void UpdatingWeeklyTaskToBeFinishedWithNullEmail() throws Exception {
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

        WeeklyTask task = WeeklyTaskService.GetWeeklyTask("test", 9999, weekStart);

        assertThrowsExactly(IllegalArgumentException.class, () -> WeeklyTaskService.UpdateGivenTaskToBeFinished(task, "New Reflection", null));
    }
    @Test
    public void UpdatingWeeklyTaskToBeFinishedWithEmailThatDoesntExist() throws Exception {
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

        WeeklyTask task = WeeklyTaskService.GetWeeklyTask("test", 9999, weekStart);

        assertNull( WeeklyTaskService.UpdateGivenTaskToBeFinished(task, "New Reflection", "asdhjgfbhasjdahjsd"));
    }
    @Test
    public void UpdatingWeeklyTaskToBeFinishedWithNullReflection() throws Exception {
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

        WeeklyTask task = WeeklyTaskService.GetWeeklyTask("test", 9999, weekStart);

        assertThrowsExactly(IllegalArgumentException.class, () -> WeeklyTaskService.UpdateGivenTaskToBeFinished(task, null, "test"));
    }

    @Test
    public void SavingDraftOnlyUpdatesSelectedWeek() throws Exception {
        LocalDate thisWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate previousWeek = thisWeek.minusWeeks(1);

        try (PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO WeeklyTasks
                        (accountEmail, taskId, status, reflection, weekStart)
                    VALUES
                        ('test', 9999, 'Finished', 'Older reflection', ?),
                        ('test', 9999, 'NotStarted', '', ?)
                    """)) {
            statement.setString(1, previousWeek.toString());
            statement.setString(2, thisWeek.toString());
            statement.executeUpdate();
        }

        WeeklyTask currentTask = WeeklyTaskService.GetWeeklyTask("test", 9999, thisWeek);
        WeeklyTaskService.UpdateGivenTaskToDraft(currentTask, "New reflection", "test");
        WeeklyTask olderTask = WeeklyTaskService.GetWeeklyTask("test", 9999, previousWeek);
        WeeklyTask updatedTask = WeeklyTaskService.GetWeeklyTask("test", 9999, thisWeek);

        assertEquals("Older reflection", olderTask.getReflection());
        assertEquals("Finished", olderTask.getStatus());
        assertEquals("New reflection", updatedTask.getReflection());
        assertEquals("Started", updatedTask.getStatus());
    }


}

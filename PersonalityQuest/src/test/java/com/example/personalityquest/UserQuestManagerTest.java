package com.example.personalityquest;

import com.example.personalityquest.Managers.HashingManager;
import com.example.personalityquest.Managers.UserQuestManager;
import jdk.jshell.spi.ExecutionControl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;

public class UserQuestManagerTest {

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
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE Arechetype (
                        archetypeId INT PRIMARY KEY,
                        name TEXT NOT NULL,
                        smallDescription TEXT NOT NULL
                    )
                    """);
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE Quests (
                        labourId INT PRIMARY KEY,
                        archetypeId INT NOT NULL,
                        name TEXT NOT NULL,
                        FOREIGN KEY(archetypeId) REFERENCES Arechtype(archetypeId)
                    )
                    """);
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE UserQuests (
                        id INT PRIMARY KEY,
                        accountEmail TEXT NOT NULL,
                        labourId INT NOT NULL,
                        percentageComplete REAL NOT NULL,
                        status TEXT NOT NULL,
                        FOREIGN KEY(labourId) REFERENCES Quests(labourId)
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

        try (PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO Arechetype
                        (archetypeId, name, smallDescription)
                    VALUES (99, "testArechtypeName", "testArechtypeDescription")
                    """))
        {

            statement.executeUpdate();
        }

        try (PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO Quests
                        (labourId, archetypeId, name)
                    VALUES (20, 99, "testQuestName")
                    """))
        {

            statement.executeUpdate();
        }


    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    public void GetUserQuestForEmail() throws SQLException, ExecutionControl.NotImplementedException {
        PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO UserQuests
                        (accountEmail, labourId, percentageComplete, status)
                    VALUES ("test", 20, 0.0, 'Active')
                    """);

        statement.executeUpdate();

        UserQuestManager.GetCurrentUserQuestForEmail("test");
    }

    @Test
    public void GetUserQuestForEmailIfNoActiveQuestExists() throws SQLException, ExecutionControl.NotImplementedException {
        PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO UserQuests
                        (accountEmail, labourId, percentageComplete, status)
                    VALUES ("test", 20, 0.0, 'Not Started')
                    """);

        statement.executeUpdate();

        UserQuestManager.GetCurrentUserQuestForEmail("test");
    }

    @Test
    public void GetUserQuestForEmailThatDoesntExist() throws SQLException, ExecutionControl.NotImplementedException {
        PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO UserQuests
                        (accountEmail, labourId, percentageComplete, status)
                    VALUES ("test", 20, 0.0, 'Not Started')
                    """);

        statement.executeUpdate();

        UserQuestManager.GetCurrentUserQuestForEmail("realEmail");
    }
    
}

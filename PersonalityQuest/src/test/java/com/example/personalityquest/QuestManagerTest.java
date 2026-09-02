package com.example.personalityquest;

import com.example.personalityquest.Managers.HashingManager;
import com.example.personalityquest.Managers.QuestManager;
import jdk.jshell.spi.ExecutionControl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;

public class QuestManagerTest {

    private Connection connection;

    private int labourId;
    private int archetypeId;

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

        labourId = 20;
        archetypeId = 99;
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
    @Test
    public void GetQuestForLabourId() throws ExecutionControl.NotImplementedException {
        QuestManager.GetQuestForLabourId(labourId);
    }
    @Test
    public void GetQuestForNonExistentLabourId() throws ExecutionControl.NotImplementedException {
        QuestManager.GetQuestForLabourId(999999);
    }
    @Test
    public void GetQuestsForArchetypeId() throws ExecutionControl.NotImplementedException {
        QuestManager.GetQuestsForArchetypeId(99);
    }
    @Test
    public void GetQuestsForNonExistentArchetypeId() throws ExecutionControl.NotImplementedException {
        QuestManager.GetQuestsForArchetypeId(999999);
    }
    @Test
    public void GetRandomAmountOfQuestsForArchetypeId() throws ExecutionControl.NotImplementedException {
        QuestManager.GetAmountOfRandomQuestsForArchetypeId(1, archetypeId);
    }
    @Test
    public void GetRandomAmountOfQuestsForArchetypeIdWithMoreThenExist() throws ExecutionControl.NotImplementedException {
        QuestManager.GetAmountOfRandomQuestsForArchetypeId(3, archetypeId);
    }
    @Test
    public void GetRandomAmountOfQuestsForArchetypeIdWithNegativeNumber() throws ExecutionControl.NotImplementedException {
        QuestManager.GetAmountOfRandomQuestsForArchetypeId(-5, archetypeId);
    }
    @Test
    public void GetRandomAmountOfQuestsForNonExistentArchetypeId() throws ExecutionControl.NotImplementedException {
        QuestManager.GetAmountOfRandomQuestsForArchetypeId(1, 999999);
    }
}

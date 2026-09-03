package com.example.personalityquest;

import com.example.personalityquest.DataClasses.Quest;
import com.example.personalityquest.Managers.HashingManager;
import com.example.personalityquest.Managers.PasswordManager;
import com.example.personalityquest.Managers.QuestManager;
import jdk.jshell.spi.ExecutionControl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

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

        try (PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO Quests
                        (labourId, archetypeId, name)
                    VALUES (21, 99, "testQuestName2")
                    """))
        {

            statement.executeUpdate();
        }

        labourId = 20;
        archetypeId = 99;

        SQLite.setConnection(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    public void GetQuestForLabourId() throws SQLException {
        Quest quest = QuestManager.GetQuestForLabourId(labourId);

        // 1st quest
        assertEquals(labourId, quest.getLabourId());
        assertEquals(archetypeId, quest.getArchetypeId());
        assertEquals("testQuestName", quest.getName());
    }
    @Test
    public void GetQuestForNonExistentLabourId() throws SQLException {
        Quest quest = QuestManager.GetQuestForLabourId(999999);

        assertNull(quest);
    }
    @Test
    public void GetQuestsForArchetypeId() throws SQLException  {
        Quest[] quests = QuestManager.GetQuestsForArchetypeId(99);

        // 1st quest
        assertEquals(labourId, quests[0].getLabourId());
        assertEquals(archetypeId, quests[0].getArchetypeId());
        assertEquals("testQuestName", quests[0].getName());

        // 2nd quest
        assertEquals(21, quests[1].getLabourId());
        assertEquals(archetypeId, quests[1].getArchetypeId());
        assertEquals("testQuestName2", quests[1].getName());

    }
    @Test
    public void GetQuestsForNonExistentArchetypeId() throws SQLException  {
        Quest[] quests = QuestManager.GetQuestsForArchetypeId(999999);

        assertNull(quests);
    }
    @Test
    public void GetRandomQuestForArchetypeId() throws SQLException {
        Quest quest = QuestManager.GetRandomQuestForArchetypeId(archetypeId);

        // the random nature requires this
        if (quest.getLabourId() == 20){
            assertEquals(labourId, quest.getLabourId());
            assertEquals(archetypeId, quest.getArchetypeId());
            assertEquals("testQuestName", quest.getName());
        }
        else{
            assertEquals(21, quest.getLabourId());
            assertEquals(archetypeId, quest.getArchetypeId());
            assertEquals("testQuestName2", quest.getName());
        }
    }
    @Test
    public void GetRandomQuestForNonExistentArchetypeId() throws SQLException {
        Quest quest = QuestManager.GetRandomQuestForArchetypeId(999999);

        assertNull(quest);
    }
}

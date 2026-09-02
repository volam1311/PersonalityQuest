package com.example.personalityquest;

import com.example.personalityquest.DataClasses.Quest;
import com.example.personalityquest.DataClasses.UserQuest;
import com.example.personalityquest.Managers.HashingManager;
import com.example.personalityquest.Managers.UserQuestManager;
import jdk.jshell.spi.ExecutionControl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;

public class UserQuestManagerTest {

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

        labourId = 20;
        archetypeId = 99;
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    /// GET USER QUEST FOR EMAIL
    @Test
    public void GetUserQuestForEmail() throws SQLException, ExecutionControl.NotImplementedException {
        PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO UserQuests
                        (accountEmail, labourId, percentageComplete, status)
                    VALUES ("test", ?, 0.0, 'Active')
                    """);

        statement.setInt(1, labourId);
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

        statement.execute();
        UserQuestManager.GetCurrentUserQuestForEmail("realEmail");
    }

    /// SET QUEST AS ACTIVE
    @Test
    public void SetQuestToActive() throws SQLException, ExecutionControl.NotImplementedException {
        PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO UserQuests
                        (accountEmail, labourId, percentageComplete, status)
                    VALUES ("test", 20, 0.0, 'Not Started')
                    """);

        statement.execute();
        statement = connection.prepareStatement(
                """
                    SELECT * FROM UserQuests
                    WHERE accountEmail = ? AND labourId = ?
                    """);
        statement.setString(1, "test");
        statement.setInt(2, labourId);
        ResultSet rs = statement.executeQuery();
        UserQuest quest = null;
        if (rs.next()){
            quest = new UserQuest(
                    rs.getInt("labourId"),
                    rs.getString(("accountEmail")),
                    rs.getFloat("percentageComplete"),
                    rs.getString("status")
            );
        }

        System.out.println(quest.getAccountEmail());

        UserQuestManager.SetUserQuesStatusAsActive(quest, "realEmail");
    }
    @Test
    public void SetNullQuestToActive() throws ExecutionControl.NotImplementedException {
        UserQuest quest = null;

        UserQuestManager.SetUserQuesStatusAsActive(quest, "realEmail");
    }

    /// SET QUEST TO PERCENTAGE COMPLETE
    @Test
    public void SetUserQuestToPercentageCompleteOf0_5() throws ExecutionControl.NotImplementedException, SQLException {
        PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO UserQuests
                        (accountEmail, labourId, percentageComplete, status)
                    VALUES ("test", 20, 0.0, 'Not Started')
                    """);

        statement.execute();
        statement = connection.prepareStatement(
                """
                    SELECT * FROM UserQuests
                    WHERE accountEmail = ? AND labourId = ?
                    """);
        statement.setString(1, "test");
        statement.setInt(2, labourId);
        ResultSet rs = statement.executeQuery();
        UserQuest quest = null;
        if (rs.next()){
            quest = new UserQuest(
                    rs.getInt("labourId"),
                    rs.getString(("accountEmail")),
                    rs.getFloat("percentageComplete"),
                    rs.getString("status")
            );
        }

        UserQuestManager.SetUserQuestToPercentageComplete(quest, "realEmail", 0.5f);
    }
    @Test
    public void SetUserQuestToPercentageCompleteAbove1() throws ExecutionControl.NotImplementedException, SQLException {
        PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO UserQuests
                        (accountEmail, labourId, percentageComplete, status)
                    VALUES ("test", 20, 0.0, 'Not Started')
                    """);

        statement.execute();
        statement = connection.prepareStatement(
                """
                    SELECT * FROM UserQuests
                    WHERE accountEmail = ? AND labourId = ?
                    """);
        statement.setString(1, "test");
        statement.setInt(2, labourId);
        ResultSet rs = statement.executeQuery();
        UserQuest quest = null;
        if (rs.next()){
            quest = new UserQuest(
                    rs.getInt("labourId"),
                    rs.getString(("accountEmail")),
                    rs.getFloat("percentageComplete"),
                    rs.getString("status")
            );
        }

        UserQuestManager.SetUserQuestToPercentageComplete(quest, "realEmail", 1.2f);
    }
    @Test
    public void SetUserQuestToPercentageCompleteBelow0() throws ExecutionControl.NotImplementedException, SQLException {
        PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO UserQuests
                        (accountEmail, labourId, percentageComplete, status)
                    VALUES ("test", 20, 0.0, 'Not Started')
                    """);

        statement.execute();
        statement = connection.prepareStatement(
                """
                    SELECT * FROM UserQuests
                    WHERE accountEmail = ? AND labourId = ?
                    """);
        statement.setString(1, "test");
        statement.setInt(2, labourId);
        ResultSet rs = statement.executeQuery();
        UserQuest quest = null;
        if (rs.next()){
            quest = new UserQuest(
                    rs.getInt("labourId"),
                    rs.getString(("accountEmail")),
                    rs.getFloat("percentageComplete"),
                    rs.getString("status")
            );
        }

        UserQuestManager.SetUserQuestToPercentageComplete(quest, "realEmail", -0.5f);
    }

    /// SET QUEST TO STATUS COMPLETE
    @Test
    public void SetQuestToStatusComplete() throws SQLException, ExecutionControl.NotImplementedException {
        PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO UserQuests
                        (accountEmail, labourId, percentageComplete, status)
                    VALUES ("test", 20, 0.0, 'Not Started')
                    """);

        statement.execute();
        statement = connection.prepareStatement(
                """
                    SELECT * FROM UserQuests
                    WHERE accountEmail = ? AND labourId = ?
                    """);
        statement.setString(1, "test");
        statement.setInt(2, labourId);
        ResultSet rs = statement.executeQuery();
        UserQuest quest = null;
        if (rs.next()){
            quest = new UserQuest(
                    rs.getInt("labourId"),
                    rs.getString(("accountEmail")),
                    rs.getFloat("percentageComplete"),
                    rs.getString("status")
            );
        }


        UserQuestManager.SetUserQuestStatusAsComplete(quest, "realEmail");
    }
    @Test
    public void SetNullQuestToStatusComplete() throws ExecutionControl.NotImplementedException {
        UserQuest quest = null;

        UserQuestManager.SetUserQuestStatusAsComplete(quest, "realEmail");
    }

    /// INSERT QUEST FOR EMAIL
    @Test
    public void InsertNewQuestForEmail() throws  SQLException, ExecutionControl.NotImplementedException {
        PreparedStatement statement = connection.prepareStatement(
                """
                    SELECT * FROM Quests
                    WHERE labourId = ?
                    """);

        statement.setInt(1, labourId);
        ResultSet rs = statement.executeQuery();
        Quest quest = null;
        if (rs.next()){
            quest = new Quest(
                    rs.getInt("labourId"),
                    rs.getInt(("archetypeId")),
                    rs.getString("name")
            );
        }

        System.out.println(quest.getName());
        UserQuestManager.InsertNewQuestForEmail(quest, "realEmail");
    }
    @Test
    public void InsertNewNullQuestForEmail() throws ExecutionControl.NotImplementedException {
        Quest quest = null;

        UserQuestManager.InsertNewQuestForEmail(quest, "realEmail");
    }
}

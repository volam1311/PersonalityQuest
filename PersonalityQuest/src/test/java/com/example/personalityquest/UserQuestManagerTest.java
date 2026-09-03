package com.example.personalityquest;

import com.example.personalityquest.DataClasses.Quest;
import com.example.personalityquest.DataClasses.UserQuest;
import com.example.personalityquest.Managers.HashingManager;
import com.example.personalityquest.Managers.UserQuestManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserQuestManagerTest {

    private Connection connection;

    private int labourId;

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
                        accountEmail TEXT NOT NULL,
                        labourId INT NOT NULL,
                        percentageComplete FLOAT NOT NULL,
                        status TEXT NOT NULL,
                        PRIMARY KEY(accountEmail, labourId)
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

        SQLite.setConnection(connection);
        labourId = 20;
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    /// GET USER QUEST FOR EMAIL
    @Test
    public void GetUserQuestForEmail() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO UserQuests
                        (accountEmail, labourId, percentageComplete, status)
                    VALUES ("test", ?, 0.0, 'Active')
                    """);

        statement.setInt(1, labourId);
        statement.execute();
        UserQuest quest = UserQuestManager.GetCurrentActiveUserQuestForEmail("test");

        assertEquals(labourId, quest.getLabourId());
    }
    @Test
    public void GetUserQuestForEmailIfNoActiveQuestExists() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO UserQuests
                        (accountEmail, labourId, percentageComplete, status)
                    VALUES ("test", 20, 0.0, 'Not Started')
                    """);

        statement.executeUpdate();
        UserQuest quest = UserQuestManager.GetCurrentActiveUserQuestForEmail("test");

        assertNull(quest);
    }
    @Test
    public void GetUserQuestForEmailThatDoesntExist() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO UserQuests
                        (accountEmail, labourId, percentageComplete, status)
                    VALUES ("test", 20, 0.0, 'Not Started')
                    """);

        statement.execute();
        UserQuest quest = UserQuestManager.GetCurrentActiveUserQuestForEmail("realEmail");

        assertNull(quest);
    }


    /// SET QUEST AS ACTIVE
    @Test
    public void SetQuestToActive() throws Exception {
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

        UserQuest userQuest = UserQuestManager.SetUserQuesStatusAsActive(quest, "test");

        assertEquals(quest.getLabourId(), userQuest.getLabourId());
        assertEquals(quest.getAccountEmail(), userQuest.getAccountEmail());
        assertEquals("Active", userQuest.getStatus());
        assertEquals(quest.getPercentageComplete(), userQuest.getPercentageComplete());
    }
    @Test
    public void SetQuestToActiveIfEmailAlreadyHasAnActiveQuest() throws Exception {
        PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO UserQuests
                        (accountEmail, labourId, percentageComplete, status)
                    VALUES ("test", 20, 0.0, 'Not Started')
                    """);

        statement.execute();

        statement = connection.prepareStatement(
                """
                    INSERT INTO UserQuests
                        (accountEmail, labourId, percentageComplete, status)
                    VALUES ("test", 21, 0.0, 'Active')
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
         UserQuest quest;

        if (rs.next()){
            quest = new UserQuest(
                    rs.getInt("labourId"),
                    rs.getString(("accountEmail")),
                    rs.getFloat("percentageComplete"),
                    rs.getString("status")
            );
        } else {
            quest = null;
        }

        assertThrowsExactly(IllegalArgumentException.class, () -> UserQuestManager.SetUserQuesStatusAsActive(quest, "test"));
    }
    @Test
    public void SetNullQuestToActive() throws Exception {
        UserQuest quest = null;

        UserQuest userQuest = UserQuestManager.SetUserQuesStatusAsActive(quest, "realEmail");
        assertNull(userQuest);
    }

    /// SET QUEST TO PERCENTAGE COMPLETE
    @Test
    public void SetUserQuestToPercentageCompleteOf0_5() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO UserQuests
                        (accountEmail, labourId, percentageComplete, status)
                    VALUES ("test", 20, 0.0, 'Active')
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

        UserQuest updatedQuest = UserQuestManager.SetUserQuestToPercentageComplete(quest, "test", 0.5f);

        assertEquals(quest.getLabourId(), updatedQuest.getLabourId());
        assertEquals(quest.getAccountEmail(), updatedQuest.getAccountEmail());
        assertEquals("Active", updatedQuest.getStatus());
        assertEquals(0.5f, updatedQuest.getPercentageComplete());
    }
    @Test
    public void SetUserQuestToPercentageCompleteAbove1() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO UserQuests
                        (accountEmail, labourId, percentageComplete, status)
                    VALUES ("test", 20, 0.0, 'Active')
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
        UserQuest quest;
        if (rs.next()){
            quest = new UserQuest(
                    rs.getInt("labourId"),
                    rs.getString(("accountEmail")),
                    rs.getFloat("percentageComplete"),
                    rs.getString("status")
            );
        } else {
            quest = null;
        }

        assertThrowsExactly(IllegalArgumentException.class, () -> UserQuestManager.SetUserQuestToPercentageComplete(quest, "test", 1.2f));
    }
    @Test
    public void SetUserQuestToPercentageCompleteBelow0() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO UserQuests
                        (accountEmail, labourId, percentageComplete, status)
                    VALUES ("test", 20, 0.0, 'Active')
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
        UserQuest quest;
        if (rs.next()){
            quest = new UserQuest(
                    rs.getInt("labourId"),
                    rs.getString(("accountEmail")),
                    rs.getFloat("percentageComplete"),
                    rs.getString("status")
            );
        } else {
            quest = null;
        }

        assertThrowsExactly(IllegalArgumentException.class, () -> UserQuestManager.SetUserQuestToPercentageComplete(quest, "test", -0.5f));
    }

    /// SET QUEST TO STATUS COMPLETE
    @Test
    public void SetQuestToStatusComplete() throws SQLException {
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

        UserQuest userQuest = UserQuestManager.SetUserQuestStatusAsComplete(quest, "test");

        assertEquals(quest.getLabourId(), userQuest.getLabourId());
        assertEquals(quest.getAccountEmail(), userQuest.getAccountEmail());
        assertEquals("Complete", userQuest.getStatus());
        assertEquals(quest.getPercentageComplete(), userQuest.getPercentageComplete());
    }
    @Test
    public void SetNullQuestToStatusComplete() throws SQLException {
        UserQuest quest = null;

        assertThrowsExactly(IllegalArgumentException.class, () -> UserQuestManager.SetUserQuestStatusAsComplete(quest, "realEmail"));
    }

    /// INSERT QUEST FOR EMAIL
    @Test
    public void InsertNewQuestForEmail() throws  SQLException {
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
        UserQuest userQuest = UserQuestManager.InsertNewQuestForEmail(quest, "test");

        assertEquals(quest.getLabourId(), userQuest.getLabourId());
        assertEquals("test", userQuest.getAccountEmail());
        assertEquals("Not Started", userQuest.getStatus());
        assertEquals(0.0, userQuest.getPercentageComplete());
    }
    @Test
    public void InsertNewNullQuestForEmail() throws SQLException {
        Quest quest = null;

        assertThrowsExactly(IllegalArgumentException.class, () -> UserQuestManager.InsertNewQuestForEmail(quest, "test"));
    }
}

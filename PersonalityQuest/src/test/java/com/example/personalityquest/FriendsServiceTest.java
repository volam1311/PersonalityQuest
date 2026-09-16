package com.example.personalityquest;

import com.example.personalityquest.Model.auth.EmailDetails;
import com.example.personalityquest.Services.auth.HashingService;
import com.example.personalityquest.Services.profile.FriendsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FriendsServiceTest {
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
        try (Statement statement = connection.createStatement()){
            statement.execute("""
            CREATE TABLE Friends (
                accountEmail TEXT NOT NULL,
                friendsEmail TEXT NOT NULL,
                PRIMARY KEY (accountEmail, friendsEmail),
                FOREIGN KEY (accountEmail) REFERENCES Accounts(email),
                FOREIGN KEY (friendsEmail) REFERENCES Accounts(email)      
            )
            """);
        }

        try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO Accounts
                        (email, userName, firstName, lastName, password)
                    VALUES
                        ('test', 'test', 'test', 'test', ?)
                    """)
        ){

            statement.setString(1, HashingService.Hash("password"));
            statement.executeUpdate();
        }

        try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO Accounts
                        (email, userName, firstName, lastName, password)
                    VALUES
                        ('test2', 'test2', 'test2', 'test2', ?)
                    """)
        ){

            statement.setString(1, HashingService.Hash("password2"));
            statement.executeUpdate();
        }


        try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO FRIENDS
                        (accountEmail, friendsEmail)
                    VALUES
                        ('test', 'test2')
                    """)
        ){
            statement.executeUpdate();
        }

        try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO FRIENDS
                        (accountEmail, friendsEmail)
                    VALUES
                        ('test2', 'test')
                    """)
        ){
            statement.executeUpdate();
        }
        SQLite.setConnection(connection);
    }

    @Test
    public void GetFriendsForAccount() throws Exception {
        List<EmailDetails> detailsForFriends = FriendsService.GetFriendsForAccount("test");
        assertEquals("test2", detailsForFriends.getFirst().getEmail());
        assertEquals("test2", detailsForFriends.getFirst().getFirstName());
        assertEquals("test2", detailsForFriends.getFirst().getLastName());
        assertEquals("test2", detailsForFriends.getFirst().getUserName());
    }

    @Test
    public void GetFriendsForAccountThatHasNoFriends() throws Exception {
        try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO Accounts
                        (email, userName, firstName, lastName, password)
                    VALUES
                        ('test3', 'test3', 'test3', 'test3', ?)
                    """)
        ){

            statement.setString(1, HashingService.Hash("password3"));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        List<EmailDetails> detailsForFriends = FriendsService.GetFriendsForAccount("test3");
        assertEquals(0, detailsForFriends.size());
    }

    @Test
    public void AddFriend() throws Exception {
        try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO Accounts
                        (email, userName, firstName, lastName, password)
                    VALUES
                        ('test3', 'test3', 'test3', 'test3', ?)
                    """)
        ){

            statement.setString(1, HashingService.Hash("password3"));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        FriendsService.AddFriendForAccount("test3", "test2");

        List<EmailDetails> detailsForFriends = FriendsService.GetFriendsForAccount("test3");
        assertEquals("test2", detailsForFriends.getFirst().getEmail());
        assertEquals("test2", detailsForFriends.getFirst().getFirstName());
        assertEquals("test2", detailsForFriends.getFirst().getLastName());
        assertEquals("test2", detailsForFriends.getFirst().getUserName());
    }
    @Test
    public void AddFriendForFriendAccountThatDoesntExist(){
        try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO Accounts
                        (email, userName, firstName, lastName, password)
                    VALUES
                        ('test3', 'test3', 'test3', 'test3', ?)
                    """)
        ){

            statement.setString(1, HashingService.Hash("password3"));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        assertThrowsExactly(IllegalArgumentException.class, () -> FriendsService.AddFriendForAccount("test3", "doesntExist"));
    }

    @Test
    public void RemoveFriend() throws Exception {
        FriendsService.RemoveFriend("test", "test2");

        List<EmailDetails> detailsForFriends = FriendsService.GetFriendsForAccount("test");
        assertEquals(0, detailsForFriends.size());
    }

    @Test
    public void RemoveFriendForFriendAccountThatDoesntExist(){
        try (PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO Accounts
                        (email, userName, firstName, lastName, password)
                    VALUES
                        ('test3', 'test3', 'test3', 'test3', ?)
                    """)
        ){

            statement.setString(1, HashingService.Hash("password3"));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        assertThrowsExactly(IllegalArgumentException.class, () -> FriendsService.RemoveFriend("test3", "doesntExist"));
    }
}



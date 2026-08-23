package com.example.personalityquest;

import com.example.personalityquest.Managers.EmailManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class EmailManagerTest {
    private Connection connection;

    private EmailDetails fakeEmailDetails = new EmailDetails(
            "test",
            "test",
            "test",
            "test"
    );
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
        }
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("""
                    INSERT INTO Accounts 
                        (email, userName, firstName, lastName, password) 
                    VALUES ("test", "test", "test", "test", "test")
                    """);
        }

        SQLite.setConnection(connection);
    }

    @Test
    public void EmailDoesntExist() throws Exception {
        assertFalse(EmailManager.DoesAccountWithEmailExist("This email doesnt exist"));
    }

    @Test
    public void EmailDoesExist() throws Exception {
        assertTrue(EmailManager.DoesAccountWithEmailExist("test"));
    }

    @Test
    public void NullEmailForDoesExist() throws Exception {
        assertFalse(EmailManager.DoesAccountWithEmailExist(null));
    }

    @Test
    public void GetDetailsForAnEmailThatDoesntExist() throws Exception {
        assertNull(EmailManager.GetDetailsForEmail("This email doesnt exist"));
    }

    @Test
    public void GetDetailsForAnEmailThatNull() throws Exception {
        assertNull(EmailManager.GetDetailsForEmail(null));
    }

    @Test
    public void GetEmailViaDetails() throws Exception {
        assertEquals(fakeEmailDetails.getFirstName(), EmailManager.GetDetailsForEmail("test").getEmail());
    }

    @Test
    public void GetUserNameViaDetails() throws Exception {
        assertEquals(fakeEmailDetails.getFirstName(), EmailManager.GetDetailsForEmail("test").getUserName());
    }

    @Test
    public void GetFirstNameViaDetails() throws Exception {
        assertEquals(fakeEmailDetails.getFirstName(), EmailManager.GetDetailsForEmail("test").getFirstName());
    }

    @Test
    public void GetLastNameViaDetails() throws Exception {
        assertEquals(fakeEmailDetails.getFirstName(), EmailManager.GetDetailsForEmail("test").getLastName());
    }
}

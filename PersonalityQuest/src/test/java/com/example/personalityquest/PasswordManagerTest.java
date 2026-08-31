package com.example.personalityquest;

import com.example.personalityquest.DataClasses.EmailDetails;
import com.example.personalityquest.Managers.HashingManager;
import com.example.personalityquest.Managers.PasswordManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordManagerTest {
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
        try (PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO Accounts
                        (email, userName, firstName, lastName, password)\s
                    VALUES ("test", "test", "test", "test", ?)
                    """))
        {
            String hashedPassword = HashingManager.Hash("test");
            statement.setString(1, hashedPassword);

            statement.executeUpdate();
        }

        SQLite.setConnection(connection);
    }

    @Test
    public void PasswordDoesntMatch() throws Exception {
        assertFalse(PasswordManager.isPasswordForEmail("test", "not-test"));
    }

    @Test
    public void PasswordDoesMatch() throws Exception {
        assertTrue(PasswordManager.isPasswordForEmail("test", "test"));
    }

    @Test
    public void NewPasswordIsInDB() throws Exception {
        PasswordManager.UpdatePasswordForEmail("test", "newPassword");

        assertTrue(PasswordManager.isPasswordForEmail("test", "newPassword"));
    }
    @Test
    public void OldPasswordIsNotInDb() throws Exception {
        PasswordManager.UpdatePasswordForEmail("test", "newPassword");

        assertFalse(PasswordManager.isPasswordForEmail("test", "test"));
    }

    @Test
    public void PasswordCheckWithNullEmail() throws Exception {
        assertFalse(PasswordManager.isPasswordForEmail(null, "test"));
    }

    @Test
    public void PasswordCheckWithNullPassword() throws Exception {
        assertFalse(PasswordManager.isPasswordForEmail("test", null));
    }
    @Test
    public void PasswordCheckWithBothNull() throws Exception {
        assertFalse(PasswordManager.isPasswordForEmail(null, null));
    }

    @Test
    public void UpdatePasswordWithNullEmail() throws Exception {
        assertFalse(PasswordManager.UpdatePasswordForEmail(null, "test"));
    }

    @Test
    public void UpdatePasswordWithNullPassword() throws Exception {
        assertFalse(PasswordManager.UpdatePasswordForEmail("test", null));
    }
    @Test
    public void UpdatePasswordWithBothNull() throws Exception {
        assertFalse(PasswordManager.UpdatePasswordForEmail(null, null));
    }
}

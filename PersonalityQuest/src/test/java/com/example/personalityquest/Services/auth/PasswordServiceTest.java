package com.example.personalityquest.Services.auth;

import com.example.personalityquest.SQLite;
import com.example.personalityquest.Model.auth.EmailDetails;
import com.example.personalityquest.Services.auth.HashingService;
import com.example.personalityquest.Services.auth.PasswordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordServiceTest {
    private Connection connection;

    private EmailDetails fakeEmailDetails = new EmailDetails(
            "test",
            "test",
            "test",
            "test"
    );

    private String currentPassword;

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
            String hashedPassword = HashingService.Hash("test");
            statement.setString(1, hashedPassword);

            statement.executeUpdate();

            currentPassword = "test";
        }

        SQLite.setConnection(connection);
    }

    @Test
    public void PasswordDoesntMatch() throws Exception {
        assertFalse(PasswordService.isPasswordForEmail("test", "not-test"));
    }

    @Test
    public void PasswordDoesMatch() throws Exception {
        assertTrue(PasswordService.isPasswordForEmail("test", "test"));
    }

    @Test
    public void NewPasswordIsInDB() throws Exception {
        PasswordService.UpdatePasswordForEmail("test", currentPassword, "newPassword");

        assertTrue(PasswordService.isPasswordForEmail("test", "newPassword"));
    }
    @Test
    public void OldPasswordIsNotInDb() throws Exception {
        PasswordService.UpdatePasswordForEmail("test", currentPassword, "newPassword");

        assertFalse(PasswordService.isPasswordForEmail("test", "test"));
    }

    @Test
    public void PasswordCheckWithNullEmail() throws Exception {
        assertFalse(PasswordService.isPasswordForEmail(null, "test"));
    }

    @Test
    public void PasswordCheckWithNullPassword() throws Exception {
        assertFalse(PasswordService.isPasswordForEmail("test", null));
    }
    @Test
    public void PasswordCheckWithBothNull() throws Exception {
        assertFalse(PasswordService.isPasswordForEmail(null, null));
    }

    @Test
    public void UpdatePasswordWithNullEmail() throws Exception {
        assertFalse(PasswordService.UpdatePasswordForEmail(null, currentPassword,"test"));
    }

    @Test
    public void UpdatePasswordWithNullPassword() throws Exception {
        assertFalse(PasswordService.UpdatePasswordForEmail("test", currentPassword,null));
    }
    @Test
    public void UpdatePasswordWithBothNull() throws Exception {
        assertFalse(PasswordService.UpdatePasswordForEmail(null, currentPassword, null));
    }
}

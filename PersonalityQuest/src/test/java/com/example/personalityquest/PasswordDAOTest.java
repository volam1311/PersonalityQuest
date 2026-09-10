package com.example.personalityquest;

import com.example.personalityquest.Model.EmailDetails;
import com.example.personalityquest.Services.HashingService;
import com.example.personalityquest.DAO.PasswordDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordDAOTest {
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
            String hashedPassword = HashingService.Hash("test");
            statement.setString(1, hashedPassword);

            statement.executeUpdate();
        }

        SQLite.setConnection(connection);
    }

    @Test
    public void PasswordDoesntMatch() throws Exception {
        assertFalse(PasswordDAO.isPasswordForEmail("test", "not-test"));
    }

    @Test
    public void PasswordDoesMatch() throws Exception {
        assertTrue(PasswordDAO.isPasswordForEmail("test", "test"));
    }

    @Test
    public void NewPasswordIsInDB() throws Exception {
        PasswordDAO.UpdatePasswordForEmail("test", "newPassword");

        assertTrue(PasswordDAO.isPasswordForEmail("test", "newPassword"));
    }
    @Test
    public void OldPasswordIsNotInDb() throws Exception {
        PasswordDAO.UpdatePasswordForEmail("test", "newPassword");

        assertFalse(PasswordDAO.isPasswordForEmail("test", "test"));
    }

    @Test
    public void PasswordCheckWithNullEmail() throws Exception {
        assertFalse(PasswordDAO.isPasswordForEmail(null, "test"));
    }

    @Test
    public void PasswordCheckWithNullPassword() throws Exception {
        assertFalse(PasswordDAO.isPasswordForEmail("test", null));
    }
    @Test
    public void PasswordCheckWithBothNull() throws Exception {
        assertFalse(PasswordDAO.isPasswordForEmail(null, null));
    }

    @Test
    public void UpdatePasswordWithNullEmail() throws Exception {
        assertFalse(PasswordDAO.UpdatePasswordForEmail(null, "test"));
    }

    @Test
    public void UpdatePasswordWithNullPassword() throws Exception {
        assertFalse(PasswordDAO.UpdatePasswordForEmail("test", null));
    }
    @Test
    public void UpdatePasswordWithBothNull() throws Exception {
        assertFalse(PasswordDAO.UpdatePasswordForEmail(null, null));
    }
}

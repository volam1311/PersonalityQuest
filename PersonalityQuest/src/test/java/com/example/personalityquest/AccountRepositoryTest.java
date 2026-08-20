package com.example.personalityquest;

import org.junit.jupiter.api.*;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class AccountRepositoryTest {

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
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    void createAccountPersistsUserDetails() throws SQLException {
        AccountRepository.createAccount(
                connection, "alex@example.com", "alex", "Alex", "Smith", "password123");

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT email, userName, firstName, lastName FROM Accounts WHERE email = ?")) {
            statement.setString(1, "alex@example.com");
            try (ResultSet resultSet = statement.executeQuery()) {
                assertTrue(resultSet.next());
                assertEquals("alex@example.com", resultSet.getString("email"));
                assertEquals("alex", resultSet.getString("userName"));
                assertEquals("Alex", resultSet.getString("firstName"));
                assertEquals("Smith", resultSet.getString("lastName"));
                assertFalse(resultSet.next());
            }
        }
    }

    @Test
    void createAccountStoresHashedPasswordNotPlainText() throws SQLException {
        String password = "password123";
        AccountRepository.createAccount(
                connection, "alex@example.com", "alex", "Alex", "Smith", password);

        String storedPassword = storedPasswordFor("alex@example.com");
        assertNotEquals(password, storedPassword);
        assertTrue(storedPassword.startsWith("$2a$"));
        assertTrue(Hashing.VerifyHash(storedPassword, password));
        assertFalse(Hashing.VerifyHash(storedPassword, "wrong-password"));
    }

    @Test
    void duplicateEmailIsRejected() throws SQLException {
        AccountRepository.createAccount(
                connection, "alex@example.com", "alex", "Alex", "Smith", "password123");

        SQLException exception = assertThrows(SQLException.class, () ->
                AccountRepository.createAccount(
                        connection, "alex@example.com", "alex2", "Alex", "Jones", "otherpassword"));

        assertTrue(exception.getMessage().toLowerCase().contains("unique")
                || exception.getMessage().toLowerCase().contains("constraint"));
    }

    private String storedPasswordFor(String email) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT password FROM Accounts WHERE email = ?")) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                assertTrue(resultSet.next());
                return resultSet.getString("password");
            }
        }
    }
}

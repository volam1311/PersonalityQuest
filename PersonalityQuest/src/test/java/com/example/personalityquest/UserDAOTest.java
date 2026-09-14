package com.example.personalityquest;

import com.example.personalityquest.DAO.UserDAO;
import com.example.personalityquest.Model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {

    private Connection connection;
    private UserDAO userDAO;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        userDAO = new UserDAO(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    void createUserPersistsDetailsAndAssignsId() throws SQLException {
        User user = new User("alex", "alex@example.com", "Alex", "Smith", "hashed-password");

        userDAO.createUser(user);

        assertTrue(user.getUserId() > 0);
        assertEquals("alex@example.com", storedValue(user.getUserId(), "email"));
        assertEquals("alex", storedValue(user.getUserId(), "userName"));
        assertEquals("Alex", storedValue(user.getUserId(), "firstName"));
        assertEquals("Smith", storedValue(user.getUserId(), "lastName"));
        assertEquals("hashed-password", storedValue(user.getUserId(), "password"));
    }

    @Test
    void getUserReturnsPersistedUser() {
        User created = new User("alex", "alex@example.com", "Alex", "Smith", "hashed-password");
        userDAO.createUser(created);

        User loaded = userDAO.getUser(created.getUserId());

        assertNotNull(loaded);
        assertEquals(created.getUserId(), loaded.getUserId());
        assertEquals("alex", loaded.getUsername());
        assertEquals("alex@example.com", loaded.getEmail());
        assertEquals("Alex", loaded.getFirstName());
        assertEquals("Smith", loaded.getLastName());
        assertEquals("hashed-password", loaded.getPassword());
    }

    @Test
    void getUserReturnsNullWhenMissing() {
        assertNull(userDAO.getUser(999));
    }

    @Test
    void updateUserChangesStoredDetails() {
        User user = new User("alex", "alex@example.com", "Alex", "Smith", "hashed-password");
        userDAO.createUser(user);

        user.setUsername("alexander");
        user.setEmail("alexander@example.com");
        user.setFirstName("Alexander");
        user.setLastName("Jones");
        user.setPassword("new-hash");
        userDAO.updateUser(user);

        User loaded = userDAO.getUser(user.getUserId());
        assertEquals("alexander", loaded.getUsername());
        assertEquals("alexander@example.com", loaded.getEmail());
        assertEquals("Alexander", loaded.getFirstName());
        assertEquals("Jones", loaded.getLastName());
        assertEquals("new-hash", loaded.getPassword());
    }

    @Test
    void deleteUserRemovesTheRow() {
        User user = new User("alex", "alex@example.com", "Alex", "Smith", "hashed-password");
        userDAO.createUser(user);
        int id = user.getUserId();

        userDAO.deleteUser(user);

        assertNull(userDAO.getUser(id));
    }

    @Test
    void duplicateEmailIsRejected() {
        userDAO.createUser(new User("alex", "alex@example.com", "Alex", "Smith", "hashed-password"));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userDAO.createUser(new User("alex2", "alex@example.com", "Alex", "Jones", "other-hash")));

        assertNotNull(exception.getCause());
        String message = exception.getCause().getMessage().toLowerCase();
        assertTrue(message.contains("unique") || message.contains("constraint"));
    }

    private String storedValue(int id, String column) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT " + column + " FROM Accounts WHERE rowid = ?")) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                assertTrue(resultSet.next());
                return resultSet.getString(column);
            }
        }
    }
}

package com.example.personalityquest.DAO.auth;

import com.example.personalityquest.DAO.ParentDAO;
import com.example.personalityquest.DAO.SQLiteConnection;
import com.example.personalityquest.Model.auth.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * SQLite persistence for {@link User} against the existing Accounts table.
 */
public class UserDAO extends ParentDAO implements IUserDAO {
    private static final String CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS Accounts (
                email TEXT NOT NULL,
                userName TEXT NOT NULL,
                firstName TEXT NOT NULL,
                lastName TEXT NOT NULL,
                password TEXT NOT NULL,
                PRIMARY KEY(email)
            )
            """;

    private static final String INSERT_USER =
            "INSERT INTO Accounts (email, userName, firstName, lastName, password) VALUES (?, ?, ?, ?, ?)";

    private static final String UPDATE_USER =
            "UPDATE Accounts SET email = ?, userName = ?, firstName = ?, lastName = ?, password = ? WHERE rowid = ?";

    private static final String DELETE_USER =
            "DELETE FROM Accounts WHERE rowid = ?";

    private static final String SELECT_USER =
            "SELECT rowid AS id, email, userName, firstName, lastName, password FROM Accounts WHERE rowid = ?";


    /**
     * Creates a DAO that uses the shared application database connection.
     */
    public UserDAO() {
        super();
    }

    /**
     * Creates a DAO that uses the given connection. Used by tests with an in-memory database.
     * @param connection The JDBC connection to use
     */
    public UserDAO(Connection connection) {
        super(connection);
    }

    private void createTable() {
        try (Statement statement = connection.createStatement()) {
            statement.execute(CREATE_TABLE);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create Accounts table", e);
        }
    }

    @Override
    /** {@inheritDoc} */
    public void createUser(User user) {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getUsername());
            statement.setString(3, nullToEmpty(user.getFirstName()));
            statement.setString(4, nullToEmpty(user.getLastName()));
            statement.setString(5, user.getPassword());
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setUserId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create user", e);
        }
    }

    @Override
    /** {@inheritDoc} */
    public void updateUser(User user) {
        try (PreparedStatement statement = connection.prepareStatement(UPDATE_USER)) {
            statement.setString(1, user.getEmail());
            statement.setString(2, user.getUsername());
            statement.setString(3, nullToEmpty(user.getFirstName()));
            statement.setString(4, nullToEmpty(user.getLastName()));
            statement.setString(5, user.getPassword());
            statement.setInt(6, user.getUserId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update user", e);
        }
    }

    @Override
    /** {@inheritDoc} */
    public void deleteUser(User user) {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_USER)) {
            statement.setInt(1, user.getUserId());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete user", e);
        }
    }

    @Override
    /** {@inheritDoc} */
    public User getUser(int id) {
        try (PreparedStatement statement = connection.prepareStatement(SELECT_USER)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapUser(resultSet);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get user", e);
        }
        return null;
    }

    private static User mapUser(ResultSet resultSet) throws SQLException {
        User user = new User(
                resultSet.getString("userName"),
                resultSet.getString("email"),
                resultSet.getString("firstName"),
                resultSet.getString("lastName"),
                resultSet.getString("password")
        );
        user.setUserId(resultSet.getInt("id"));
        return user;
    }

    private static String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}

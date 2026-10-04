package com.example.personalityquest;

import com.example.personalityquest.Services.auth.HashingService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/** Stores account records in the database */
public final class AccountRepository {
    private static final String SQL_SIGN_UP =
            "INSERT INTO Accounts (email, userName, firstName, lastName, password) VALUES(?, ?, ?, ?, ?)";

    private AccountRepository() {
    }

    /**
     * Creates an account record and stores hashed password
     * @param connection database connection to store account
     * @param email email associated with the account
     * @param userName the username associated with the account
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param password plain text password to hash
     * @throws SQLException if account cannot be stored
     */
    public static void createAccount(Connection connection, String email, String userName,
                                     String firstName, String lastName, String password) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SQL_SIGN_UP)) {
            statement.setString(1, email);
            statement.setString(2, userName);
            statement.setString(3, firstName);
            statement.setString(4, lastName);
            statement.setString(5, HashingService.Hash(password));
            statement.executeUpdate();
        }
    }
}

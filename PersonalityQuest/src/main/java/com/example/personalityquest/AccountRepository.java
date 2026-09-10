package com.example.personalityquest;

import com.example.personalityquest.Services.HashingService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class AccountRepository {
    private static final String SQL_SIGN_UP =
            "INSERT INTO Accounts (email, userName, firstName, lastName, password) VALUES(?, ?, ?, ?, ?)";

    private AccountRepository() {
    }

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

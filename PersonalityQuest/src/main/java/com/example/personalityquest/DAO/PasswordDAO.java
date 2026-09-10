package com.example.personalityquest.DAO;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.SQLite;
import com.example.personalityquest.Services.HashingService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PasswordDAO {
    protected final static String accountExists =
            "SELECT * FROM Accounts WHERE email = ?";

    protected final static String updatePassword =
            "UPDATE Accounts SET password = ? WHERE email = ?";

    public static String GetPasswordHash(String email) throws SQLException {
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(accountExists);
        // assign parameters
        statement.setString(1, email);

        ResultSet rs = statement.executeQuery();

        return rs.getString("password");
    }

    /**
     * Updates the account with the given email to have a new hashed password
     * @param email "The accounts email you want to update the password for"
     * @param newPassword "The new password in plain text"
     * @return "Whether the update to the database was successful"
     * @throws SQLException "Database Access Failure"
     */
    public static boolean UpdatePasswordForEmail(String email, String newPassword) throws SQLException {

        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(updatePassword);
        // assign parameters
        statement.setString(1, HashingService.Hash(newPassword));
        statement.setString(2, email);

        statement.executeUpdate();
        return true;
    }
}

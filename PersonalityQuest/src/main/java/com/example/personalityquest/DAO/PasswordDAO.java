package com.example.personalityquest.DAO;

import com.example.personalityquest.Services.HashingService;
import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.SQLite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class managers everything to do with password and has utility functions to check
 * weather a password matches an email in the database or to update the password in the database
 */
public class PasswordDAO {
    protected final static String accountExists =
            "SELECT * FROM Accounts WHERE email = ?";

    protected final static String updatePassword =
            "UPDATE Accounts SET password = ? WHERE email = ?";

    /**
     * Checks to see if the given password is for the given email's account
     * @param email "The email of the account that is for the password"
     * @param password "The password that wants to be checked"
     * @return "Whether password matches the given account email"
     * @throws SQLException "Database Access Failure"
     */
    public static boolean isPasswordForEmail(String email, String password) throws SQLException {
        // check nulls
        if (ApplicationManager.isEmpty(email)) {
            System.out.println("Email is null and therefore the password wont match");
            return false;
        }
        if (ApplicationManager.isEmpty(password)) {
            System.out.println("Can not check a null password");
            return false;
        }

        // email doesn't exist so no the password doesn't match
        if (!EmailDAO.DoesAccountWithEmailExist(email)){
            return false;
        }

        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(accountExists);
        // assign parameters
        statement.setString(1, email);

        ResultSet rs = statement.executeQuery();

        return HashingService.VerifyHash(rs.getString("password"), password);
    }

    /**
     * Updates the account with the given email to have a new hashed password
     * @param email "The accounts email you want to update the password for"
     * @param newPassword "The new password in plain text"
     * @return "Whether the update to the database was successful"
     * @throws SQLException "Database Access Failure"
     */
    public static boolean UpdatePasswordForEmail(String email, String newPassword) throws SQLException {
        // check nulls
        if (ApplicationManager.isEmpty(email)) {
            System.out.println("Email is null and therefore password can not be updated for account");
            return false;
        }
        if (ApplicationManager.isEmpty(newPassword)) {
            System.out.println("Can not have a null password for new account");
            return false;
        }

        // email doesn't exist so no the password doesn't match
        if (!EmailDAO.DoesAccountWithEmailExist(email)){
            return false;
        }

        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(updatePassword);
        // assign parameters
        statement.setString(1, HashingService.Hash(newPassword));
        statement.setString(2, email);

        statement.executeUpdate();
        return true;
    }

}

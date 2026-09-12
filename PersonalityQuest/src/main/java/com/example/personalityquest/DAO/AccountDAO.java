package com.example.personalityquest.DAO;

import com.example.personalityquest.Model.EmailDetails;
import com.example.personalityquest.SQLite;
import com.example.personalityquest.Services.EmailService;
import com.example.personalityquest.Services.HashingService;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * This DAO is used to retrieve or update data pertaining to the Accounts table
 *
 */
public class AccountDAO {
    protected final static String accountExists =
            "SELECT * FROM Accounts WHERE email = ?";

    protected final static String updatePassword =
            "UPDATE Accounts SET password = ? WHERE email = ?";

    // EMAIL
    /**
     * Checks to see whether an account exists in the database with the given email
     * @param email "The email for the presumed account you want to check"
     * @return "Whether the account exists in the database"
     * @throws SQLException "Database Access"
     */
    public static boolean DoesAccountWithEmailExist(String email) throws SQLException {
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(accountExists);
        // assign parameters
        statement.setString(1, email);

        int count = 0;

        ResultSet rs = statement.executeQuery();
        while (rs.next()){
            count++;
        }

        if (count == 0){
            System.out.println("Email doesnt exist");
        }

        return count == 1;
    }

    /**
     * Gets information from the database for the given email and returns it
     * @param email "The given email you want to retrieve details for"
     * @return "Email, UserName FirstName, LastName"
     * @throws Exception "Database Access Failure"
     */
    public static EmailDetails GetDetailsForEmail(String email) throws Exception {

        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(accountExists);
        // assign parameters
        statement.setString(1, email);


        ResultSet rs = statement.executeQuery();
        EmailDetails emailDetails = null;

        while (rs.next()){
            emailDetails = new EmailDetails(
                    rs.getString("email"),
                    rs.getString("userName"),
                    rs.getString("firstName"),
                    rs.getString("lastName")
            );
        }

        if (emailDetails == null){
            System.out.println("Email Details is null");
        }

        return emailDetails;
    }

    // Password
    /**
     * Gets the hashed password that matches a given email
     * @param email The accounts email you want the password for
     * @return The hashed string of the password
     * @throws SQLException Database Access Failure
     */
    public static String GetHashedPasswordForEmail(String email) throws SQLException {
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

    public static void UpdateAccountDetails(EmailDetails emailDetails, String currentEmail) throws SQLException {

    }
}

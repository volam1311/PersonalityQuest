package com.example.personalityquest.Managers;

import com.example.personalityquest.SQLite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PasswordManager {
    protected final static String accountExists =
            "SELECT * FROM Accounts WHERE email = ?";

    protected final static String updatePassword =
            "UPDATE Accounts SET password = ? WHERE email = ?";
    /*
    * Does the given email have the given password for its account
    *
    * Returns: Whether the password matches the one for the given email
    * */
    public static boolean isPasswordForEmail(String email, String password) throws SQLException {
        // check nulls
        if (SystemManager.isEmpty(email)) {
            System.out.println("Email is null and therefore the password wont match");
            return false;
        }
        if (SystemManager.isEmpty(password)) {
            System.out.println("Can not check a null password");
            return false;
        }

        // email doesn't exist so no the password doesn't match
        if (!EmailManager.DoesAccountWithEmailExist(email)){
            return false;
        }

        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(accountExists);
        // assign parameters
        statement.setString(1, email);

        ResultSet rs = statement.executeQuery();

        return HashingManager.VerifyHash(rs.getString("password"), password);
    }

    /*
    * Updates the account to have the newPassword
    *
    * Returns: Whether account update was successful
    * */
    public static boolean UpdatePasswordForEmail(String email, String newPassword) throws SQLException {
        // check nulls
        if (SystemManager.isEmpty(email)) {
            System.out.println("Email is null and therefore password can not be updated for account");
            return false;
        }
        if (SystemManager.isEmpty(newPassword)) {
            System.out.println("Can not have a null password for new account");
            return false;
        }

        // email doesn't exist so no the password doesn't match
        if (!EmailManager.DoesAccountWithEmailExist(email)){
            return false;
        }

        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(updatePassword);
        // assign parameters
        statement.setString(1, HashingManager.Hash(newPassword));
        statement.setString(2, email);

        statement.executeUpdate();
        return true;
    }

    private void NullCheck(){

    }
}

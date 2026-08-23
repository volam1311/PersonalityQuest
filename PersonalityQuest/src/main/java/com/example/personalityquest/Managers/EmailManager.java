package com.example.personalityquest.Managers;

import com.example.personalityquest.EmailDetails;
import com.example.personalityquest.SQLite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmailManager {

    protected final static String accountExists =
            "SELECT * FROM Accounts WHERE email = ?";

    /*
    * Checks whether the given string exists as an account in the database
    * */
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

        return count == 1;
    }

    /*
     * Gets Information from the database for the given email
     *
     * Returns: Email details or null if account doesn't exist
     * */
    public static EmailDetails GetDetailsForEmail(String email) throws Exception {
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(accountExists);
        // assign parameters
        statement.setString(1, email);

        int count = 0;

        ResultSet rs = statement.executeQuery();
        EmailDetails emailDetails = null;

        while (rs.next()){
            count++;
            emailDetails = new EmailDetails(
                rs.getString("email"),
                rs.getString("userName"),
                rs.getString("firstName"),
                rs.getString("lastName")
            );
        }

        return emailDetails;
    }

}

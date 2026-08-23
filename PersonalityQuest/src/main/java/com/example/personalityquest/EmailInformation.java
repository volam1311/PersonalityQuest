package com.example.personalityquest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmailInformation {

    protected final static String accountExists =
            "SELECT * FROM Accounts WHERE email = ?";

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

        if (count == 0)
            throw new Exception("Account does not exist");

        return emailDetails;
    }

}

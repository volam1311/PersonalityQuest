package com.example.personalityquest.Managers;

import com.example.personalityquest.DataClasses.EmailDetails;
import com.example.personalityquest.SQLite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmailManager {


    protected final static String accountExists =
            "SELECT * FROM Accounts WHERE email = ?";


    /**
     * Checks to see whether an account exists in the database with the given email
     * @param email "The email for the presumed account you want to check"
     * @return "Whether the account exists in the database"
     * @throws SQLException "Database Access"
     */
    public static boolean DoesAccountWithEmailExist(String email) throws SQLException {
        // check nulls
        if (SystemManager.isEmpty(email)) {
            System.out.println("Email is null therefor it does not exist");
            return false;}

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
        // check nulls
        if (SystemManager.isEmpty(email)) {
            System.out.println("Email is null therefor details can not be gotten");
            return null;}

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

}

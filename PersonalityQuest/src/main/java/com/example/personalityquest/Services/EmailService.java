package com.example.personalityquest.Services;

import com.example.personalityquest.DAO.AccountDAO;
import com.example.personalityquest.Model.EmailDetails;
import com.example.personalityquest.ApplicationManager;

import java.sql.SQLException;

/**
 * This class managers everything to do with emails and has utility functions to check
 * if account exist for them or retrieve the EmailDetails matching the email
 */
public class EmailService {
    /**
     * Checks to see whether an account exists in the database with the given email
     * @param email "The email for the presumed account you want to check"
     * @return "Whether the account exists in the database"
     * @throws SQLException "Database Access"
     */
    public static boolean DoesAccountWithEmailExist(String email) throws SQLException {
        // check nulls
        if (ApplicationManager.isEmpty(email)) {
            System.out.println("Email is null therefor it does not exist");
            return false;}

        return AccountDAO.DoesAccountWithEmailExist(email);
    }

    /**
     * Gets information from the database for the given email and returns it
     * @param email "The given email you want to retrieve details for"
     * @return "Email, UserName FirstName, LastName"
     * @throws Exception "Database Access Failure"
     */
    public static EmailDetails GetDetailsForEmail(String email) throws Exception {
        // check nulls
        if (ApplicationManager.isEmpty(email)) {
            System.out.println("Email is null therefore details can not be gotten");
            return null;}

        return AccountDAO.GetDetailsForEmail(email);
    }

}

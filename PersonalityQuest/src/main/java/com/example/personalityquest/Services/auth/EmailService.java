package com.example.personalityquest.Services.auth;

import com.example.personalityquest.DAO.auth.AccountDAO;
import com.example.personalityquest.Model.auth.EmailDetails;
import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.Services.ParentService;

import java.sql.SQLException;

/**
 * This class managers everything to do with emails and has utility functions to check
 * if account exist for them or retrieve the EmailDetails matching the email
 */
public class EmailService extends ParentService {

    private AccountDAO AccountDAO;
    public EmailService(){
        super();
        this.AccountDAO = new AccountDAO();
    }

    public EmailService(AccountDAO accountDAO){
        super();
        this.AccountDAO = accountDAO;
    }
    /**
     * Checks to see whether an account exists in the database with the given email
     * @param email "The email for the presumed account you want to check"
     * @return "Whether the account exists in the database"
     * @throws SQLException "Database Access"
     */
    public boolean DoesAccountWithEmailExist(String email) throws SQLException {
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
    public EmailDetails GetDetailsForEmail(String email) throws Exception {
        // check nulls
        if (ApplicationManager.isEmpty(email)) {
            System.out.println("Email is null therefore details can not be gotten");
            return null;}

        return AccountDAO.GetDetailsForEmail(email);
    }

}

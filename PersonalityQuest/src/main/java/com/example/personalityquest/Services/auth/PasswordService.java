package com.example.personalityquest.Services.auth;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.DAO.auth.AccountDAO;

import java.sql.SQLException;
import java.util.Objects;

/**
 * This class managers everything to do with password and has utility functions to check
 * weather a password matches an email in the database or to update the password in the database
 */
public class PasswordService {

    private AccountDAO AccountDAO;

    private EmailService EmailService;
    public PasswordService(){
        this.AccountDAO = new AccountDAO();
        EmailService = new EmailService();
    }

    public PasswordService(AccountDAO accountDAO){
        this.AccountDAO = accountDAO;

        EmailService = new EmailService(accountDAO);
    }
    /**
     * Checks to see if the given password is for the given email's account
     * @param email "The email of the account that is for the password"
     * @param password "The plaintext password that wants to be checked"
     * @return "Whether password matches the given account email"
     * @throws SQLException "Database Access Failure"
     */
    public boolean isPasswordForEmail(String email, String password) throws SQLException {
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
        if (!EmailService.DoesAccountWithEmailExist(email)){
            return false;
        }

        String hashedPassword = AccountDAO.GetHashedPasswordForEmail(email);

        return HashingService.VerifyHash(hashedPassword, password);
    }

    /**
     * Updates the account with the given email to have a new hashed password
     * @param email "The accounts email you want to update the password for"
     * @param newPassword "The new password in plain text"
     * @return "Whether the update to the database was successful"
     * @throws SQLException "Database Access Failure"
     */
    public boolean UpdatePasswordForEmail(String email, String currentPassword, String newPassword) throws SQLException {
        // check nulls
        if (ApplicationManager.isEmpty(email)) {
            System.out.println("Email is null and therefore password can not be updated for account");
            return false;
        }
        if (ApplicationManager.isEmpty(newPassword)) {
            System.out.println("Can not have a null password for new account");
            return false;
        }

        // current password entered does not match account
        if (!isPasswordForEmail(email, currentPassword)){
            System.out.println("Current Password is incorrect");
            return false;
        }
        // email doesn't exist so no the password doesn't match
        if (!EmailService.DoesAccountWithEmailExist(email)){
            return false;
        }

        try{
            AccountDAO.UpdatePasswordForEmail(email,  newPassword);
            return true;
        }
        catch (SQLException e){
            System.out.println(e.getStackTrace());
            return false;
        }
    }

}

package com.example.personalityquest.Services.auth;

import com.example.personalityquest.DAO.auth.AccountDAO;
import com.example.personalityquest.Model.auth.EmailDetails;

import java.sql.SQLException;
import java.util.Objects;

public class AccountService {

    public static void UpdateEmailDetailsForAccount(EmailDetails emailDetails, String currentEmail) throws SQLException {
        /*
         * Does the new email already exist and is the email being updated
         * */
        if (EmailService.DoesAccountWithEmailExist(emailDetails.getEmail()) && !Objects.equals(currentEmail,emailDetails.getEmail())){
            throw new IllegalArgumentException("Email of " + emailDetails.getEmail() + " Already exists");
        }

        AccountDAO.UpdateAccountDetails(emailDetails, currentEmail);
    }
}

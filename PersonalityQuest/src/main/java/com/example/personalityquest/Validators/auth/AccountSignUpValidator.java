package com.example.personalityquest.Validators.auth;

import java.util.Objects;

/** Validates values supplied during account sign-up */
public final class AccountSignUpValidator {
    private AccountSignUpValidator() {
    }

    /**
     * Checks whether all supplied sign-up values pass validation
     * @param email the email to validate
     * @param userName the username to validate
     * @param firstName the first name to validate
     * @param lastName the last name to validate
     * @param password the password to validate
     * @param reEnterPassword the confirmation password to compare
     * @return true if values are valid, otherwise returns false
     */
    public static boolean isValid(String email, String userName, String firstName,
                                  String lastName, String password, String reEnterPassword) {
        return validationError(email, userName, firstName, lastName, password, reEnterPassword) == null;
    }

    /**
     * Returns the first validation error found
     * @param email the email to validate
     * @param userName the username to validate
     * @param firstName the first name to validate
     * @param lastName the last name to validate
     * @param password the password to validate
     * @param reEnterPassword the confirmation password to compare
     * @return an error message or null if details are valid
     */
    public static String validationError(String email, String userName, String firstName,
                                         String lastName, String password, String reEnterPassword) {
        if (!Objects.equals(password, reEnterPassword)) {
            return "Passwords do not match";
        }
        if (isEmpty(email.trim())) {
            return "Email is empty";
        }
        if (isEmpty(userName.trim())) {
            return "UserName is empty";
        }
        if (isEmpty(firstName.trim())) {
            return "FirstName is empty";
        }
        if (isEmpty(lastName.trim())) {
            return "LastName is empty";
        }
        if (isEmpty(password.trim())) {
            return "Password is empty";
        }
        if (password.length() < 8){
            return "Password length should be at least 8 characters";
        }
        return null;
    }

    private static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }
}

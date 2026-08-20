package com.example.personalityquest;

import java.util.Objects;

public final class AccountSignUpValidator {
    private AccountSignUpValidator() {
    }

    public static boolean isValid(String email, String userName, String firstName,
                                  String lastName, String password, String reEnterPassword) {
        return validationError(email, userName, firstName, lastName, password, reEnterPassword) == null;
    }

    public static String validationError(String email, String userName, String firstName,
                                         String lastName, String password, String reEnterPassword) {
        if (!Objects.equals(password, reEnterPassword)) {
            return "Passwords do not match";
        }
        if (isEmpty(email)) {
            return "Email is empty";
        }
        if (isEmpty(userName)) {
            return "UserName is empty";
        }
        if (isEmpty(firstName)) {
            return "FirstName is empty";
        }
        if (isEmpty(lastName)) {
            return "LastName is empty";
        }
        if (isEmpty(password)) {
            return "Password is empty";
        }
        return null;
    }

    private static boolean isEmpty(String value) {
        return value == null || value.isEmpty();
    }
}

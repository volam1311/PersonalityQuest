package com.example.personalityquest;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class AccountSignUpValidatorTest {

    @Test
    void validDetails() {
        assertTrue(AccountSignUpValidator.isValid(
                "alex@example.com", "alex", "Alex", "Smith", "password123", "password123"));
        assertNull(AccountSignUpValidator.validationError(
                "alex@example.com", "alex", "Alex", "Smith", "password123", "password123"));
    }

    @Test
    void mismatchedPasswordsAreRejected() {
        assertFalse(AccountSignUpValidator.isValid(
                "alex@example.com", "alex", "Alex", "Smith", "password123", "different"));
        assertEquals("Passwords do not match", AccountSignUpValidator.validationError(
                "alex@example.com", "alex", "Alex", "Smith", "password123", "different"));
    }

    @Test
    void emptyEmailIsRejected() {
        assertEquals("Email is empty", AccountSignUpValidator.validationError(
                "", "alex", "Alex", "Smith", "password123", "password123"));
    }

    @Test
    void emptyUserNameIsRejected() {
        assertEquals("UserName is empty", AccountSignUpValidator.validationError(
                "alex@example.com", "", "Alex", "Smith", "password123", "password123"));
    }

    @Test
    void emptyFirstNameIsRejected() {
        assertEquals("FirstName is empty", AccountSignUpValidator.validationError(
                "alex@example.com", "alex", "", "Smith", "password123", "password123"));
    }

    @Test
    void emptyLastNameIsRejected() {
        assertEquals("LastName is empty", AccountSignUpValidator.validationError(
                "alex@example.com", "alex", "Alex", "", "password123", "password123"));
    }

    @Test
    void emptyPasswordIsRejected() {
        assertEquals("Password is empty", AccountSignUpValidator.validationError(
                "alex@example.com", "alex", "Alex", "Smith", "", ""));
    }

    @Test
    void nullFieldsAreTreatedAsEmpty() {
        assertEquals("Email is empty", AccountSignUpValidator.validationError(
                null, "alex", "Alex", "Smith", "password123", "password123"));
        assertEquals("UserName is empty", AccountSignUpValidator.validationError(
                "alex@example.com", null, "Alex", "Smith", "password123", "password123"));
        assertEquals("FirstName is empty", AccountSignUpValidator.validationError(
                "alex@example.com", "alex", null, "Smith", "password123", "password123"));
        assertEquals("LastName is empty", AccountSignUpValidator.validationError(
                "alex@example.com", "alex", "Alex", null, "password123", "password123"));
        assertEquals("Password is empty", AccountSignUpValidator.validationError(
                "alex@example.com", "alex", "Alex", "Smith", null, null));
    }

    @Test
    void passwordMismatchIsCheckedBeforeEmptyFields() {
        assertEquals("Passwords do not match", AccountSignUpValidator.validationError(
                "", "", "", "", "password123", ""));
    }
}

package com.example.personalityquest;

import com.example.personalityquest.Validators.AccountSignUpValidator;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class AccountSignUpValidatorTest {

    @Test
    void validDetails() {
        assertTrue(AccountSignUpValidator.isValid(
                "alex@example.com", "alex", "Alex", "Smith", "password123", "password123"));
    }

    @Test
    void mismatchedPasswords() {
        assertFalse(AccountSignUpValidator.isValid(
                "alex@example.com", "alex", "Alex", "Smith", "password123", "different"));
        assertEquals("Passwords do not match", AccountSignUpValidator.validationError(
                "alex@example.com", "alex", "Alex", "Smith", "password123", "different"));
    }

    @Test
    void emptyEmail() {
        assertEquals("Email is empty", AccountSignUpValidator.validationError(
                "", "alex", "Alex", "Smith", "password123", "password123"));
    }

    @Test
    void emptyUserName() {
        assertEquals("UserName is empty", AccountSignUpValidator.validationError(
                "alex@example.com", "", "Alex", "Smith", "password123", "password123"));
    }

    @Test
    void emptyFirstName() {
        assertEquals("FirstName is empty", AccountSignUpValidator.validationError(
                "alex@example.com", "alex", "", "Smith", "password123", "password123"));
    }

    @Test
    void emptyLastName() {
        assertEquals("LastName is empty", AccountSignUpValidator.validationError(
                "alex@example.com", "alex", "Alex", "", "password123", "password123"));
    }

    @Test
    void emptyPassword() {
        assertEquals("Password is empty", AccountSignUpValidator.validationError(
                "alex@example.com", "alex", "Alex", "Smith", "", ""));
    }

    @Test
    void passwordMismatch() {
        assertEquals("Passwords do not match", AccountSignUpValidator.validationError(
                "", "", "", "", "password123", ""));
    }

    @Test
    void passwordPolicyError(){
        assertEquals("Password length should be at least 8 characters",AccountSignUpValidator.validationError(
                "alex@example.com", "alex", "Alex", "Smith", "123456", "123456"));
    }
}

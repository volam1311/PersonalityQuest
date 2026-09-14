package com.example.personalityquest.Model.auth;

import com.example.personalityquest.Model.auth.EmailDetails;
import org.junit.jupiter.api.Test;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class EmailDetailsTest {
    private Connection connection;

    private EmailDetails fakeEmailDetails = new EmailDetails(
            "test",
            "test",
            "test",
            "test"
    );

    @Test
    public void NullEmail(){
        EmailDetails emailDetails = new EmailDetails(null, "test", "test", "test");

        assertEquals("", emailDetails.getEmail());
    }

    @Test
    public void NullUserName(){
        EmailDetails emailDetails = new EmailDetails("test", null, "test", "test");

        assertEquals("", emailDetails.getUserName());
    }

    @Test
    public void NullFirstName(){
        EmailDetails emailDetails = new EmailDetails("test", "test", null, "test");

        assertEquals("", emailDetails.getFirstName());
    }

    @Test
    public void NullLastName(){
        EmailDetails emailDetails = new EmailDetails("test", "test", "test", null);

        assertEquals("", emailDetails.getLastName());
    }


}

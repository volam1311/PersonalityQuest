package com.example.personalityquest.Model.auth;

import java.time.LocalDate;

/** Remembers an account email and the date it last signed in. */
public class LoginCache {

    private final String email;
    private final LocalDate lastLoginDate;
    public LoginCache(String email, LocalDate lastLoginDate){
        this.email = email;
        this.lastLoginDate = lastLoginDate;
    }

    public String GetEmail(){
        return email;
    }

    public LocalDate GetLastLoginDate(){
        return lastLoginDate;
    }
}

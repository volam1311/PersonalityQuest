package com.example.personalityquest.Model.auth;

import java.time.LocalDate;

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

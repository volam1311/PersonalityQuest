package com.example.personalityquest;

public class EmailDetails {
    public EmailDetails(String email, String userName, String firstName, String lastName){
        this.email = email;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    private final String email;
    private final String userName;
    private final String firstName;
    private final String lastName;

    public String getEmail(){
        return email;
    }

    public String getUserName(){
        return userName;
    }

    public String getFirstName(){
        return firstName;
    }

    public String getLastName(){
        return lastName;
    }
}

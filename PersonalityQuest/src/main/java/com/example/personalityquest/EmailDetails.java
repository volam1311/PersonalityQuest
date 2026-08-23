package com.example.personalityquest;

import com.example.personalityquest.Managers.SystemManager;

public class EmailDetails {
    public EmailDetails(String email, String userName, String firstName, String lastName){
        if (SystemManager.isEmpty(email)){
            System.out.println("Email is null");
            email = "";
        }
        if (SystemManager.isEmpty(userName)){
            System.out.println("UserName is null");
            userName = "";
        }

        if (SystemManager.isEmpty(firstName)){
            System.out.println("FirstName is null");
            firstName = "";
        }
        if (SystemManager.isEmpty(lastName)){
            System.out.println("LastName is null");
            lastName = "";
        }

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

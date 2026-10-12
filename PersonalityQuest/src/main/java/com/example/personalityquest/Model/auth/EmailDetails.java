package com.example.personalityquest.Model.auth;

import com.example.personalityquest.ApplicationManager;

/**
 * Holds the details pertaining to an account
 */
public class EmailDetails {
    /** Creates account details and replaces empty values with empty strings
     * @param email the account email address
     * @param userName the account username
     * @param firstName the account holder's first name
     * @param lastName the account holder's last name
     */
    public EmailDetails(String email, String userName, String firstName, String lastName){
        if (ApplicationManager.isEmpty(email)){
            System.out.println("Email is null");
            email = "";
        }
        if (ApplicationManager.isEmpty(userName)){
            System.out.println("UserName is null");
            userName = "";
        }

        if (ApplicationManager.isEmpty(firstName)){
            System.out.println("FirstName is null");
            firstName = "";
        }
        if (ApplicationManager.isEmpty(lastName)){
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

    /** Returns the account email address
     * @return the account email
     */
    public String getEmail(){
        return email;
    }

    /** Returns the account username
     * @return the account username
     */
    public String getUserName(){
        return userName;
    }

    /** Returns the account holder's first name
     * @return the first name
     */
    public String getFirstName(){
        return firstName;
    }

    /** Returns the account holder's last name
     * @return the last name
     */
    public String getLastName(){
        return lastName;
    }

}

package com.example.personalityquest.Model;
import java.util.*;

/**
 * A simple model class representing the user with username, password, userid.
 */
public class User {
    private String username;
    private String email;
    private String  password;
    private String userId;

    /**
     * Constructs a new User with username, email, password.
     * @param username The username of user
     * @param email The email of user
     * @param password The hashed password of user
     */
    public User(String username, String email,String password) {
        setUsername(username);
        setEmail(email);
        setPassword(password);
        setUserId();
    }

    /**
     * Returns the username of user
     * @return The username of the user
     */
    public String getUsername() {
        return username;
    }

    /**
     * Return the hashed password of user
     * @return The hashed password
     */
    public String getPassword(){
        return password;
    }

    /**
     * Return the email of user
     * @return
     */
    public String getEmail(){
        return email;
    }

    /**
     * Set the username of the user
     * @param username The username to set
     */
    public void setUsername(String username){
        this.username = username;
    }

    /**
     * Set the hashed password of the user
     * @param password The hashed password to set
     */
    public void setPassword(String password){
        this.password = password;
    }

    /**
     * Set the email of the user
     * @param email The email to set
     */
    public void setEmail(String email){
        this.email = email;
    }

    /**
     * Return the id of the user
     * @return The id of user
     */
    public String getUserId(){
        return userId;
    }

    /**
     * Set the id of the user
     */
    public void setUserId(){
        this.userId = UUID.randomUUID().toString();
    }
}

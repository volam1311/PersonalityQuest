package com.example.personalityquest.Model;

/**
 * A simple model class representing the user with username, email, name, password, and userid.
 */
public class User {
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private int userId;

    /**
     * Constructs a new User with username, email, password.
     * @param username The username of user
     * @param email The email of user
     * @param password The hashed password of user
     */
    public User(String username, String email, String password) {
        this(username, email, "", "", password);
    }

    /**
     * Constructs a new User with username, email, name, and password.
     * @param username The username of user
     * @param email The email of user
     * @param firstName The first name of user
     * @param lastName The last name of user
     * @param password The hashed password of user
     */
    public User(String username, String email, String firstName, String lastName, String password) {
        setUsername(username);
        setEmail(email);
        setFirstName(firstName);
        setLastName(lastName);
        setPassword(password);
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
    public String getPassword() {
        return password;
    }

    /**
     * Return the email of user
     * @return The email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Return the first name of user
     * @return The first name of the user
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Return the last name of user
     * @return The last name of the user
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Set the username of the user
     * @param username The username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Set the hashed password of the user
     * @param password The hashed password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Set the email of the user
     * @param email The email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Set the first name of the user
     * @param firstName The first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Set the last name of the user
     * @param lastName The last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Return the id of the user
     * @return The id of user
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Set the id of the user. Used after the database assigns a row id.
     * @param userId The id to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }
}

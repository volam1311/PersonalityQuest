package com.example.personalityquest.Model;
import java.util.*;

public class User {
    private String username;
    private String email;
    private String  password;
    private String userId;
    public User(String username, String email,String password) {
        setUsername(username);
        setEmail(email);
        setPassword(password);
    }
    public String getUsername() {
        return username;
    }
    public String getPassword(){
        return password;
    }
    public String getEmail(){
        return email;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getUserId(){
        return userId;
    }
    public void setUserId(){
        this.userId = UUID.randomUUID().toString();
    }
}

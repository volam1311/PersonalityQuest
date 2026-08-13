package com.example.personalityquest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite {
    private static Connection instance = null;

    private SQLite(){
        String url = "jdbc:sqlite:PersonalityQuest.db";
        try{
            instance = DriverManager.getConnection(url);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static Connection getConnection(){
        if (instance == null){
            new SQLite();
        }

        return instance;
    }

}

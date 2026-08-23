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

    /*
     * Used for tests as they need to connect to an in memory db
     * */
    public static void setConnection(Connection newConnection){
        instance = newConnection;
    }

    public static Connection getConnection(){
        if (instance == null){
            new SQLite();
        }

        return instance;
    }

}

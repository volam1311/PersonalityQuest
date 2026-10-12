package com.example.personalityquest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Provides the shared connection to the application SQLite database */
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
    /** Replaces the shared database connection
     * @param newConnection the connection to use
     */
    public static void setConnection(Connection newConnection){
        instance = newConnection;
    }

    /** Returns the shared database connection
     * @return the active SQLite connection
     */
    public static Connection getConnection(){
        if (instance == null){
            new SQLite();
        }

        return instance;
    }

}

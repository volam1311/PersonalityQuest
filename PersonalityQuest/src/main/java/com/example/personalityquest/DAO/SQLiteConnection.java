package com.example.personalityquest.DAO;

import java.sql.Connection;

import com.example.personalityquest.SQLite;

/**
 * Provides the shared SQLite connection used by DAO classes.
 */
public class SQLiteConnection {
    private SQLiteConnection() {
    }

    /**
     * Returns the shared database connection, creating it if needed.
     * @return The SQLite connection
     */
    public static Connection getInstance() {
        return SQLite.getConnection();
    }
}

package com.example.personalityquest.DAO;

import com.example.personalityquest.SQLite;

import java.sql.Connection;

/** Supplies the shared database connection used by data-access objects. */
public class ParentDAO {

    protected Connection connection;
    public ParentDAO(){
        connection = SQLite.getConnection();
    }

    public ParentDAO(Connection connection){
        this.connection = connection;
    }

    public Connection getConnection(){
        return connection;
    }
}

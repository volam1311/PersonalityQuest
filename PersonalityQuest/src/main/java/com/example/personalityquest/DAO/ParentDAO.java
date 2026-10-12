package com.example.personalityquest.DAO;

import com.example.personalityquest.SQLite;

import java.sql.Connection;

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

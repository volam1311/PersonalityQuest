package com.example.personalityquest.DataClasses;

import com.example.personalityquest.Managers.SystemManager;
import com.example.personalityquest.SQLite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/// This is the class for the actual current running tasks that the user is able to complete
/// Go check out Task if you want to see the class structure for the Tasks that turn into Weekly Tasks.
public class WeeklyTask {
    public WeeklyTask(String email, int taskId, String status, String reflection, String weekStarted) throws Exception {
        if (taskId == 0){
            throw new Exception("Task Id is == 0 or is null");
        }
        if (SystemManager.isEmpty(email)){
            System.out.println("Email is null");
            email = "";
        }
        if (SystemManager.isEmpty(status)){
            System.out.println("Status is null");
            status = "";
        }
        if (SystemManager.isEmpty(reflection)){
            System.out.println("Reflection is null");
            reflection = "";
        }
        if (SystemManager.isEmpty(weekStarted)){
            System.out.println("WeekStarted is null");
            weekStarted = "";
        }

        this.email = email;
        this.taskId = taskId;
        this.status = status;
        this.reflection = reflection;
        this.weekStarted = weekStarted;
    }

    private final String email;
    private final int taskId;
    private String status;
    private String reflection;
    private final String weekStarted;

    public int getTaskId(){ return this.taskId; }
    public String getEmail(){
        return this.email;
    }
    public String getReflection(){
        return this.reflection;
    }

    public String getStatus(){
        return this.status;
    }
    public String getWeekStarted(){
        return this.weekStarted;
    }

    public void setStatus(String status){ this.status = status; }
    public void setReflection(String reflection){ this.reflection = reflection; }

    @Override
    public String toString() {
        Connection connection = SQLite.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM Tasks WHERE taskId = ?");

            statement.setInt(1, getTaskId());

            ResultSet rs = statement.executeQuery();
            return rs.getString("name");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}

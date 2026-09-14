package com.example.personalityquest.Model.quest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.SQLite;

/**
 * A weekly practice assigned to a user for one week.
 * This is not a storyline quest task. The taskId points at a weekly practice in Tasks
 * (taskType = WEEKLY). Quest storyline steps stay in Tasks with taskType = QUEST.
 */
public class WeeklyTask {
    public WeeklyTask(String email, int taskId, String status, String reflection, String weekStarted) throws Exception {
        if (taskId == 0){
            throw new Exception("Task Id is == 0 or is null");
        }
        if (ApplicationManager.isEmpty(email)){
            System.out.println("Email is null");
            email = "";
        }
        if (ApplicationManager.isEmpty(status)){
            System.out.println("Status is null");
            status = "";
        }
        if (ApplicationManager.isEmpty(reflection)){
            System.out.println("Reflection is null");
            reflection = "";
        }
        if (ApplicationManager.isEmpty(weekStarted)){
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

    /**
     * Converts the weekly task to a string
     * in the form of its name
     * @return the name of the weeklyTask
     */
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

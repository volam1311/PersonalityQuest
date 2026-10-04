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
    /** Creates a weekly task assigned to an account
     * @param email the account email
     * @param taskId the weekly task ID
     * @param status the task status
     * @param reflection the saved reflection text
     * @param weekStarted the Monday date for the task's week
     * @throws Exception if the task ID is zero
     */
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

    /** Returns the weekly task ID
     * @return the task ID
     */
    public int getTaskId(){ return this.taskId; }
    /** Returns the account email
     * @return the account email
     */
    public String getEmail(){
        return this.email;
    }
    /** Returns the saved reflection text
     * @return the reflection text
     */
    public String getReflection(){
        return this.reflection;
    }

    /** Returns the weekly task status
     * @return the current status
     */
    public String getStatus(){
        return this.status;
    }
    /** Returns the Monday date associated with the task
     * @return the week start date as text
     */
    public String getWeekStarted(){
        return this.weekStarted;
    }

    /** Sets the weekly task status
     * @param status the new status
     */
    public void setStatus(String status){ this.status = status; }
    /** Sets the reflection text
     * @param reflection the new reflection text
     */
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

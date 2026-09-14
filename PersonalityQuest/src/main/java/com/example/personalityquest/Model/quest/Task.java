package com.example.personalityquest.Model.quest;

import com.example.personalityquest.ApplicationManager;

/**
 * A task definition from the Tasks table.
 * Quest tasks are storyline steps on a labour. 
 */
public class Task {
    public Task(int taskId, String name, String description, int labourId) throws Exception {
        if (taskId == 0){
            throw new Exception("Task Id is == 0 or is null");
        }
        if (ApplicationManager.isEmpty(name)){
            System.out.println("UserName is null");
            name = "";
        }

        if (ApplicationManager.isEmpty(description)){
            System.out.println("FirstName is null");
            description = "";
        }
        if (labourId == 0){
            throw new Exception("Labour Id is == 0 or is null");
        }

        this.taskId = taskId;
        this.name = name;
        this.description = description;
        this.labourId = labourId;
    }


    private final int taskId;
    private final String name;
    private final String description;
    private final int labourId;

    public int getTaskId(){
        return this.taskId;
    }

    public String getName(){
        return this.name;
    }

    public String getDescription(){
        return this.description;
    }

    public int getLabourId(){
        return this.labourId;
    }

    @Override
    public String toString() {
        return getName();
    }
}

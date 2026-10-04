package com.example.personalityquest.Model.quest;

import com.example.personalityquest.ApplicationManager;

/**
 * A task definition from the Tasks table.
 * Quest tasks are storyline steps on a labour. 
 */
public class Task {
    /** Creates a task definition
     * @param taskId the task's unique ID
     * @param name the task name
     * @param description the task description
     * @param labourId the ID of the quest associated with the task
     * @throws Exception if the task ID or quest ID is zero
     */
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

    /** Returns the task ID
     * @return the task ID
     */
    public int getTaskId(){
        return this.taskId;
    }

    /** Returns the task name
     * @return the task name
     */
    public String getName(){
        return this.name;
    }

    /** Returns the task description
     * @return the task description
     */
    public String getDescription(){
        return this.description;
    }

    /** Returns the associated quest ID
     * @return the quest ID
     */
    public int getLabourId(){
        return this.labourId;
    }

    /** Returns the task name for display
     * @return the task name
     */
    @Override
    public String toString() {
        return getName();
    }
}

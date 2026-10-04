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
     * @param overview a short bolded call-to-action shown alongside the description
     * @param labourId the ID of the quest associated with the task
     * @throws Exception if the task ID or quest ID is zero
     */
    public Task(int taskId, String name, String description, String overview, int labourId) throws Exception {
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
        this.overview = ApplicationManager.isEmpty(overview) ? "":overview;
    }

    /** Creates a task definition without an overview
     * @param taskId the task's unique ID
     * @param name the task name
     * @param description the task description
     * @param labourId the ID of the quest associated with the task
     * @throws Exception if the task ID or quest ID is zero
     */
    public Task(int taskId, String name, String description, int labourId) throws Exception {
        this(taskId, name, description, "", labourId);
    }


    private final int taskId;
    private final String name;
    private final String description;
    private final String overview;
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

    /** Returns the task's short overview/call-to-action text
     * @return the task overview
     */
    public String getOverview(){return this.overview;}

    @Override
    public String toString() {
        return getName();
    }
}

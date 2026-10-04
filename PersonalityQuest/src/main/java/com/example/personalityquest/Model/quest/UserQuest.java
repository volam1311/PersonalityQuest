package com.example.personalityquest.Model.quest;

import com.example.personalityquest.ApplicationManager;

/**
 * Holds the details pertaining to UserQuests that have been assigned in the database to users
 */
/*
* This is a user quest which is the ones that get assigned to the user for them to complete.
* Not to be mistaken with a Quest which is only the unassigned information of the quest.
* */
public class UserQuest {
    private final int labourId;
    private final String accountEmail;
    private float percentageComplete;

    // Either 'Active', 'Completed'
    private String status;

    /** Creates quest progress for an account
     * @param labourId the quest ID
     * @param accountEmail the account email
     * @param percentageComplete progress from zero to one
     * @param status the quest status
     * @throws IllegalArgumentException if an ID, email, status, or progress value is invalid
     */
    public UserQuest(int labourId, String accountEmail, float percentageComplete, String status){
        if (labourId <= 0){
            throw new IllegalArgumentException("Labour Id is null");
        }
        if (ApplicationManager.isEmpty(accountEmail)){
            throw new IllegalArgumentException("Email is null");
        }
        if (percentageComplete < 0 || percentageComplete > 1){
            throw new IllegalArgumentException("Percentage complete is out of range of 0 - 1");
        }
        if (ApplicationManager.isEmpty(status)){
            throw new IllegalArgumentException("Status is null");
        }

        this.labourId = labourId;
        this.accountEmail = accountEmail;
        this.percentageComplete = percentageComplete;
        this.status = status;
    }

    /** Returns the quest ID
     * @return the quest ID
     */
    public int getLabourId() {
        return labourId;
    }

    /** Returns the quest progress as a value from zero to one
     * @return the completion ratio
     */
    public float getPercentageComplete() {
        return percentageComplete;
    }

    /** Returns the email of the account assigned to the quest
     * @return the account email
     */
    public String getAccountEmail() {
        return accountEmail;
    }

    /** Returns the quest status
     * @return the current status
     */
    public String getStatus() {
        return status;
    }

    /** Sets the quest progress ratio
     * @param percentageComplete progress from zero to one
     */
    public void setPercentageComplete(float percentageComplete) {
        this.percentageComplete = percentageComplete;
    }

    /** Sets the quest status
     * @param status the new status
     */
    public void setStatus(String status) {
        this.status = status;
    }
}

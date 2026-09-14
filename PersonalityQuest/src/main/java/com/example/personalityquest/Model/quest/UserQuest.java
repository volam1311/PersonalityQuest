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

    public int getLabourId() {
        return labourId;
    }

    public float getPercentageComplete() {
        return percentageComplete;
    }

    public String getAccountEmail() {
        return accountEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setPercentageComplete(float percentageComplete) {
        this.percentageComplete = percentageComplete;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

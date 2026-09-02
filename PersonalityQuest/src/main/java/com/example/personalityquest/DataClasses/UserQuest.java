package com.example.personalityquest.DataClasses;

import com.example.personalityquest.Managers.SystemManager;

/*
* This is a user quest which is the ones that get assigned to the user for them to complete.
* Not to be mistaken with a Quest which is only the unassigned information of the quest.
* */
public class UserQuest {
    private final int labourId;
    private final String accountEmail;
    private float percentageComplete;

    public UserQuest(int labourId, String accountEmail, float percentageComplete){
        if (labourId <= 0){
            throw new IllegalArgumentException("Labour Id is null");
        }
        if (SystemManager.isEmpty(accountEmail)){
            throw new IllegalArgumentException("Email is null");
        }
        if (percentageComplete < 0 || percentageComplete > 1){
            throw new IllegalArgumentException("Percentage complete is out of range of 0 - 1");
        }

        this.labourId = labourId;
        this.accountEmail = accountEmail;
        this.percentageComplete = percentageComplete;
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

    public void setPercentageComplete(float percentageComplete) {
        this.percentageComplete = percentageComplete;
    }
}

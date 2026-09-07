package com.example.personalityquest.DataClasses;

import com.example.personalityquest.Managers.SystemManager;

/*
* This is a quest which is the ones that are unassigned and only used as information to make a User Quest.
* Not to be mistaken with a User Quest which is the ones the user actually completes
* */
public class Quest {

    private final int labourId;
    private final int archetypeId;
    private final String name;

    public Quest(int labourId, int archetypeId, String name){
        if (SystemManager.isEmpty(name)){
            throw new IllegalArgumentException("Name is null");
        }
        if (labourId <= 0){
            throw new IllegalArgumentException("LabourId is null");
        }
        if (archetypeId <= 0){
            throw new IllegalArgumentException("ArchetypeId is null");
        }

        this.labourId = labourId;
        this.archetypeId = archetypeId;
        this.name = name;

    }
    public int getArchetypeId() {
        return archetypeId;
    }
    public int getLabourId() {
        return labourId;
    }
    public String getName() {
        return name;
    }
}

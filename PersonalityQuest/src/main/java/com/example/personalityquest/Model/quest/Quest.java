package com.example.personalityquest.Model.quest;

import com.example.personalityquest.ApplicationManager;

/**
 * Holds the details pertaining to Quests that have not yet been assigned
 */
/*
 * This is a quest which is the ones that are unassigned and only used as information to make a User Quest.
 * Not to be mistaken with a User Quest which is the ones the user actually completes
 * */
public class Quest {

    private final int labourId;
    private final int archetypeId;
    private final String name;
    private final String narrative;
    private final String decisionQuestion;
    private final String resolution;

    public Quest(int labourId, int archetypeId, String name, String narrative){
        this(labourId, archetypeId, name, narrative, "", "");
    }

    public Quest(int labourId, int archetypeId, String name, String narrative, String decisionQuestion, String resolution) {

        if (ApplicationManager.isEmpty(name)){
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
        this.narrative = narrative;
        this.decisionQuestion = decisionQuestion == null ? "" : decisionQuestion;
        this.resolution = resolution == null ? "" : resolution;



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
    public String getNarrative() { return narrative; }

    public String getDecisionQuestion() { return decisionQuestion; }

    public String getResolution() { return resolution; }
}

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

    /** Creates an unassigned quest definition without a decision question or resolution
     * @param labourId the quest's unique ID
     * @param archetypeId the ID of the archetype associated with the quest
     * @param name the quest name
     * @param narrative the quest narrative
     * @throws IllegalArgumentException if an ID is invalid or the name is empty
     */
    public Quest(int labourId, int archetypeId, String name, String narrative){
        this(labourId, archetypeId, name, narrative, "", "");
    }

    /** Creates an unassigned quest definition
     * @param labourId the quest's unique ID
     * @param archetypeId the ID of the archetype associated with the quest
     * @param name the quest name
     * @param narrative the quest narrative
     * @param decisionQuestion the "your reaction" storyline question for this labour
     * @param resolution the "how it ended" storyline text for this labour
     * @throws IllegalArgumentException if an ID is invalid or the name is empty
     */
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
    /** Returns the associated archetype ID
     * @return the archetype ID
     */
    public int getArchetypeId() {
        return archetypeId;
    }
    /** Returns the quest ID
     * @return the quest ID
     */
    public int getLabourId() {
        return labourId;
    }
    /** Returns the quest name
     * @return the quest name
     */
    public String getName() {
        return name;
    }
    /** Returns the quest narrative
     * @return the quest narrative
     */
    public String getNarrative() { return narrative; }

    /** Returns the "your reaction" storyline question for this labour
     * @return the decision question
     */
    public String getDecisionQuestion() { return decisionQuestion; }

    /** Returns the "how it ended" storyline text for this labour
     * @return the resolution text
     */
    public String getResolution() { return resolution; }
}

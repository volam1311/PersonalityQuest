package com.example.personalityquest.Model.quest;

import static java.awt.SystemColor.text;

/**
 * Your reaction choice is tied to the labour's storyline moment
 * Each option pushes toward either the excess or deficit side of that
 * Relates to Labour's archetype Virtue
 * @param questOptionId
 * @param labourId
 * @param archetypeId
 * @param reactionType
 * @param text
 */

public record QuestOption(int questOptionId, int labourId, int archetypeId, ReactionType reactionType, String text) {

    public enum ReactionType{
        EXCESS, DEFICIT
    }

    public QuestOption{
        if (text == null || text.isBlank()){
            throw new IllegalArgumentException("Quest option text cannot be blank");
        }
        if (labourId <= 0){
            throw new IllegalArgumentException("Labour ID is invalid");
        }
        if (archetypeId <=0 ){
            throw new IllegalArgumentException("Archetype ID is invalid");
        }
        if (reactionType == null){
            throw new IllegalArgumentException("Reaction type is invalid");
        }
    }

}

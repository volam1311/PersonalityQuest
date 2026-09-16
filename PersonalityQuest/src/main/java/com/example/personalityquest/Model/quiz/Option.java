package com.example.personalityquest.Model.quiz;

import com.example.personalityquest.ApplicationManager;

/**
 * A single answer choice on a quiz question, scored toward one archetype.
 */
public record Option(int optionsID,
                     int option1Archetype,
                     int option2Archetype,
                     int option3Archetype,
                     String option1,
                     String option2,
                     String option3) {

    public Option {
        // if (ApplicationManager.isEmpty(text) ||
        if (option1.isBlank() || option2.isBlank() || option3.isBlank()) {
            throw new IllegalArgumentException("Option text is empty");
        }
        if (option1Archetype == 0 || option2Archetype == 0 || option3Archetype == 0) {
            throw new IllegalArgumentException("Option archetype is null");
        }
        //text = text.trim();
    }

    // Getters and Setters
    public int getOptionsID() {return optionsID;}

    public int getOption1Archetype() {return option1Archetype;}
    public int getOption2Archetype() {return option2Archetype;}
    public int getOption3Archetype() {return option3Archetype;}

    public String getOption1() {return option1;}
    public String getOption2() {return option2;}
    public String getOption3() {return option3;}

}

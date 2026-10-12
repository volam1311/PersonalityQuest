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
    /** Returns the option set ID
     * @return the option set ID
     */
    public int getOptionsID() {return optionsID;}

    /** Returns the archetype ID for the first answer
     * @return the first answer's archetype ID
     */
    public int getOption1Archetype() {return option1Archetype;}
    /** Returns the archetype ID for the second answer
     * @return the second answer's archetype ID
     */
    public int getOption2Archetype() {return option2Archetype;}
    /** Returns the archetype ID for the third answer
     * @return the third answer's archetype ID
     */
    public int getOption3Archetype() {return option3Archetype;}

    /** Returns the text of the first answer
     * @return the first answer text
     */
    public String getOption1() {return option1;}
    /** Returns the text of the second answer
     * @return the second answer text
     */
    public String getOption2() {return option2;}
    /** Returns the text of the third answer
     * @return the third answer text
     */
    public String getOption3() {return option3;}

}

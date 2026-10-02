package com.example.personalityquest.Model.quiz;

import com.example.personalityquest.ApplicationManager;

import java.util.List;

/**
 * A quiz prompt with scored answer options.
 */
public class Question {
    private final int questionID;
    private final String realmType;
    private final String questionType;
    private final String questionPrompt;
    private final Option options;

    /** Creates a quiz question with its answer options
     * @param questionID the question's unique ID
     * @param realmType the realm associated with the question
     * @param questionType the question type
     * @param questionPrompt the question text shown to the user
     * @param options the answer options for the question
     * @throws IllegalArgumentException if the ID or question prompt is invalid
     */
    public Question(int questionID, String realmType, String questionType, String questionPrompt, Option options) {
        if (questionID <= 0) {
            throw new IllegalArgumentException("Question id is invalid");
        }
        if (ApplicationManager.isEmpty(questionPrompt) || questionPrompt.isBlank()) {
            throw new IllegalArgumentException("Question prompt is empty");
        }
//        if (options == null || options.size() < 2) {
//            throw new IllegalArgumentException("Question needs at least two options");
//        }
//        for (Option option : options) {
//            if (option == null) {
//                throw new IllegalArgumentException("Question has a null option");
//            }
//        }

        this.questionID = questionID;
        this.realmType = realmType;
        this.questionType = questionType;
        this.questionPrompt = questionPrompt.trim();
        this.options = options;
        //this.options = List.copyOf(options);
    }

    /** Returns the question ID
     * @return the question ID
     */
    public int getQuestionID() {
        return questionID;
    }

    /** Returns the question prompt
     * @return the question text
     */
    public String getQuestionPrompt() {
        return questionPrompt;
    }

    /** Returns the question's realm
     * @return the realm type
     */
    public String getRealmType() {return realmType;}

    /** Returns the question type
     * @return the question type
     */
    public String getQuestionType() {return questionType;}

    /** Returns the answer options
     * @return the question's answer options
     */
    public Option getOptions() {
        return options;
    }
}

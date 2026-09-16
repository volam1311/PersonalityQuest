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

    public int getQuestionID() {
        return questionID;
    }

    public String getQuestionPrompt() {
        return questionPrompt;
    }

    public String getRealmType() {return realmType;}

    public String getQuestionType() {return questionType;}

    public Option getOptions() {
        return options;
    }
}

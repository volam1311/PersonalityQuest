package com.example.personalityquest.Model;

import com.example.personalityquest.ApplicationManager;

import java.util.List;

/**
 * A quiz prompt with scored answer options.
 */
public class Question {
    private final int id;
    private final String prompt;
    private final List<Option> options;

    public Question(int id, String prompt, List<Option> options) {
        if (id <= 0) {
            throw new IllegalArgumentException("Question id is invalid");
        }
        if (ApplicationManager.isEmpty(prompt) || prompt.isBlank()) {
            throw new IllegalArgumentException("Question prompt is empty");
        }
        if (options == null || options.size() < 2) {
            throw new IllegalArgumentException("Question needs at least two options");
        }
        for (Option option : options) {
            if (option == null) {
                throw new IllegalArgumentException("Question has a null option");
            }
        }

        this.id = id;
        this.prompt = prompt.trim();
        this.options = List.copyOf(options);
    }

    public int getId() {
        return id;
    }

    public String getPrompt() {
        return prompt;
    }

    public List<Option> getOptions() {
        return options;
    }
}

package com.example.personalityquest.Model.quiz;

import com.example.personalityquest.ApplicationManager;

/**
 * A single answer choice on a quiz question, scored toward one archetype.
 */
public record Option(String text, Archetype archetype) {
    public Option {
        if (ApplicationManager.isEmpty(text) || text.isBlank()) {
            throw new IllegalArgumentException("Option text is empty");
        }
        if (archetype == null) {
            throw new IllegalArgumentException("Option archetype is null");
        }
        text = text.trim();
    }
}

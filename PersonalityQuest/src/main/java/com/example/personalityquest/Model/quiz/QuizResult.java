package com.example.personalityquest.Model.quiz;

import com.example.personalityquest.Model.quest.Quest;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

/**
 * The scored outcome of an archetype quiz, optionally with the quest assigned from it.
 */
public record QuizResult(Archetype archetype, Map<Archetype, Integer> scores, Quest assignedQuest) {
    public QuizResult {
        if (archetype == null) {
            throw new IllegalArgumentException("Quiz result archetype is null");
        }
        if (scores == null || scores.isEmpty()) {
            throw new IllegalArgumentException("Quiz result scores are empty");
        }

        Map<Archetype, Integer> copy = new EnumMap<>(Archetype.class);
        copy.putAll(scores);
        scores = Collections.unmodifiableMap(copy);
    }

    public QuizResult(Archetype archetype, Map<Archetype, Integer> scores) {
        this(archetype, scores, null);
    }

    /**
     * Returns a copy of this result with the quest assigned after scoring.
     */
    public QuizResult withAssignedQuest(Quest assignedQuest) {
        return new QuizResult(archetype, scores, assignedQuest);
    }
}

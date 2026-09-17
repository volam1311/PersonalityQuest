package com.example.personalityquest.Model.profile;

/**
 * An achievement defined in the database, including whether the current account has unlocked it.
 */
public record Achievement(
        int achievementId,
        String name,
        String description,
        String criteriaType,
        double threshold,
        int level,
        boolean unlocked) {

    /**
     * Returns a copy of this achievement with an updated unlock state.
     */
    public Achievement withUnlocked(boolean unlocked) {
        return new Achievement(achievementId, name, description, criteriaType, threshold, level, unlocked);
    }
}

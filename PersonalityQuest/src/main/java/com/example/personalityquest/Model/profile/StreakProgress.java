package com.example.personalityquest.Model.profile;


import java.time.LocalDate;

/**
 * Holds the details pertaining to streaks
 */
public final class StreakProgress {
    private final int currentStreak;
    private final int bestStreak;
    private final LocalDate lastCompletionDate;

    public StreakProgress(int currentStreak, int bestStreak, LocalDate lastCompletionDate) {
        this.currentStreak = currentStreak;
        this.bestStreak = bestStreak;
        this.lastCompletionDate = lastCompletionDate;
    }

    public int GetCurrentStreak() {
        return currentStreak;
    }

    public int GetBestStreak() {
        return bestStreak;
    }

    public LocalDate GetLastCompletionDate() {
        return lastCompletionDate;
    }
}

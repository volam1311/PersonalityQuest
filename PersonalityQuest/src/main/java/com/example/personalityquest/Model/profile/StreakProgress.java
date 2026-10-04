package com.example.personalityquest.Model.profile;


import java.time.LocalDate;

/**
 * Holds the details pertaining to streaks
 */
public final class StreakProgress {
    private final int currentStreak;
    private final int bestStreak;
    private final LocalDate lastCompletionDate;

    /** Creates a snapshot of an account's streak progress
     * @param currentStreak the current consecutive-week streak
     * @param bestStreak the longest consecutive-week streak
     * @param lastCompletionDate the date of the most recent completion
     */
    public StreakProgress(int currentStreak, int bestStreak, LocalDate lastCompletionDate) {
        this.currentStreak = currentStreak;
        this.bestStreak = bestStreak;
        this.lastCompletionDate = lastCompletionDate;
    }

    /** Returns the current consecutive-week streak
     * @return the current streak
     */
    public int GetCurrentStreak() {
        return currentStreak;
    }

    /** Returns the longest consecutive-week streak
     * @return the best streak
     */
    public int GetBestStreak() {
        return bestStreak;
    }

    /** Returns the date of the most recent completion
     * @return the most recent completion date
     */
    public LocalDate GetLastCompletionDate() {
        return lastCompletionDate;
    }
}

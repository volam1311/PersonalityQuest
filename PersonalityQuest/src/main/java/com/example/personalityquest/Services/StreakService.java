package com.example.personalityquest.Services;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.DAO.StreakDAO;
import com.example.personalityquest.Model.StreakProgress;

import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Manages streak calculations and delegates progress persistence to StreakDAO
 */
public class StreakService {

    private static final int NO_STREAK = 0;
    private static final int FIRST_STREAK = 1;

    private StreakService() {
    }

    /**
     * Records a completion and returns the updated current streak
     * @param email The account email
     * @param completionDate The date of the completion
     * @return The updated current streak
     * @throws SQLException If the progress cannot be read or saved
     */
    public static int RecordCompletion(
            String email,
            LocalDate completionDate) throws SQLException {

        if (ApplicationManager.isEmpty(email) || completionDate == null) {
            return NO_STREAK;
        }

        StreakProgress progress = StreakDAO.GetProgress(email);

        if (progress == null) {
            return NO_STREAK;
        }

        LocalDate lastCompletionDate = progress.getLastCompletionDate();

        // Do not increase the streak more than once on the same date
        if (completionDate.equals(lastCompletionDate)) {
            return progress.getCurrentStreak();
        }

        // Do not replace saved progress with an older completion date
        if (lastCompletionDate != null
                && completionDate.isBefore(lastCompletionDate)) {
            return progress.getCurrentStreak();
        }

        int updatedStreak = FIRST_STREAK;

        if (lastCompletionDate != null
                && lastCompletionDate.plusDays(1).equals(completionDate)) {
            updatedStreak = progress.getCurrentStreak() + 1;
        }

        int updatedBestStreak = Math.max(
                progress.getBestStreak(),
                updatedStreak
        );

        StreakDAO.SaveProgress(
                email,
                updatedStreak,
                updatedBestStreak,
                completionDate
        );

        return updatedStreak;
    }

    /**
     * Gets the current streak using today's date
     * @param email The account email
     * @return The current streak
     * @throws SQLException If the progress cannot be read
     */
    public static int GetCurrentStreak(String email) throws SQLException {
        return GetCurrentStreak(email, LocalDate.now());
    }

    /**
     * Gets the current streak for a specified date
     * @param email The account email
     * @param today The date used to determine whether the streak is active
     * @return The current streak, or zero after a missed day
     * @throws SQLException If the progress cannot be read
     */
    public static int GetCurrentStreak(
            String email,
            LocalDate today) throws SQLException {

        if (ApplicationManager.isEmpty(email) || today == null) {
            return NO_STREAK;
        }

        StreakProgress progress = StreakDAO.GetProgress(email);

        if (progress == null) {
            return NO_STREAK;
        }

        LocalDate lastCompletionDate = progress.getLastCompletionDate();

        if (lastCompletionDate != null
                && lastCompletionDate.isBefore(today.minusDays(1))) {
            return NO_STREAK;
        }

        return progress.getCurrentStreak();
    }

    /**
     * Gets the highest streak achieved by an account
     * @param email The account email
     * @return The best streak
     * @throws SQLException If the progress cannot be read
     */
    public static int GetBestStreak(String email) throws SQLException {

        if (ApplicationManager.isEmpty(email)) {
            return NO_STREAK;
        }

        StreakProgress progress = StreakDAO.GetProgress(email);

        if (progress == null) {
            return NO_STREAK;
        }

        return progress.getBestStreak();
    }
}

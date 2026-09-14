package com.example.personalityquest.Services;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.DAO.StreakDAO;
import com.example.personalityquest.Model.StreakProgress;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;

/**
 * Manages streak calculations and delegates progress persistence to StreakDAO
 */
public class StreakService {

    private static final int NO_STREAK = 0;
    private static final int FIRST_STREAK = 1;

    private StreakService() {
    }

    /**
     * @param date The date of current day
     * Helper function to get the start of the week for streaks
     * @return The date of the Monday of the current week
     */
    private static LocalDate WeekStart(LocalDate date) {
        LocalDate today = LocalDate.now(); // Gets the current date

        return date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    /**
     * Records a completion and returns the updated current streak
     * @param email The account email
     * @param completionDate The date of the completion
     * @return The updated current streak
     * @throws SQLException If the progress cannot be read or saved
     */
    public static int RecordCompletion(String email, LocalDate completionDate)
            throws SQLException {

        if (ApplicationManager.isEmpty(email) || completionDate == null) {
            return NO_STREAK;
        }

        StreakProgress progress = StreakDAO.GetProgress(email);

        if (progress == null) {
            return NO_STREAK;
        }

        LocalDate completionWeek = completionDate.with(
                TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
        );
        LocalDate lastCompletionDate = progress.GetLastCompletionDate();
        LocalDate lastCompletionWeek = lastCompletionDate == null
                ? null
                : lastCompletionDate.with(
                        TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
                );

        // Do not increase the streak more than once in the same week
        if (completionWeek.equals(lastCompletionWeek)) {
            return progress.GetCurrentStreak();
        }

        // Do not replace saved progress with an older completion week
        if (lastCompletionWeek != null
                && completionWeek.isBefore(lastCompletionWeek)) {
            return progress.GetCurrentStreak();
        }

        int updatedStreak = FIRST_STREAK;

        if (lastCompletionWeek != null
                && lastCompletionWeek.plusWeeks(1).equals(completionWeek)) {
            updatedStreak = progress.GetCurrentStreak() + 1;
        }

        int updatedBestStreak = Math.max(
                progress.GetBestStreak(),
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
    public static int GetCurrentStreak(String email, LocalDate today) throws SQLException {

        if (ApplicationManager.isEmpty(email) || today == null) {
            return NO_STREAK;
        }

        StreakProgress progress = StreakDAO.GetProgress(email);

        if (progress == null) {
            return NO_STREAK;
        }

        LocalDate currentWeek = WeekStart(today);
        LocalDate lastCompletionDate = progress.GetLastCompletionDate();

        if (lastCompletionDate != null) {
            LocalDate lastCompletionWeek = WeekStart(lastCompletionDate);
            if (lastCompletionWeek.isBefore(currentWeek.minusWeeks(1))) {
                return NO_STREAK;
            }
        }

        return progress.GetCurrentStreak();
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

        return progress.GetBestStreak();
    }

    /**
     * Increases the amount of quests the user has completed
     * @param email The email matching the account you want to update
     * @return What the total quest completion is at for the email
     */
    public static int IncreaseTotalQuestsCompleted(String email) throws SQLException {
        if (ApplicationManager.isEmpty(email)){
            throw new IllegalArgumentException("Email is null");
        }

        int totalQuests = StreakDAO.GetTotalQuestsCompleted(email);
        totalQuests++;
        StreakDAO.SetTotalQuestsCompleted(email, totalQuests);

        return StreakDAO.GetTotalQuestsCompleted(email);
    }

    /**
     * Gets the amount of quests the user has completed
     * @param email The email matching the account you want to update
     * @return What the total quest completion is for the email
     */
    public static int GetTotalQuestsCompleted(String email) throws SQLException {
        if (ApplicationManager.isEmpty(email)){
            throw new IllegalArgumentException("Email is null");
        }

        return StreakDAO.GetTotalQuestsCompleted(email);
    }

}

package com.example.personalityquest.Model;

/**
 * Profile totals loaded from UserProgress, UserQuests, and WeeklyTasks.
 */
public record UserProfile(
        int currentStreak,
        int bestStreak,
        int assignedQuests,
        int completedQuests,
        double activeQuestProgress,
        int finishedWeeklyTasks,
        int totalWeeklyTasks,
        int finishedWeeklyTasksThisWeek,
        int weeklyTasksThisWeek,
        boolean accountExists) {

    /**
     * Returns an empty profile used when no account is signed in.
     */
    public static UserProfile empty() {
        return new UserProfile(0, 0, 0, 0, 0, 0, 0, 0, 0, false);
    }

    /**
     * Returns this snapshot with a live current streak value.
     */
    public UserProfile withCurrentStreak(int currentStreak) {
        return new UserProfile(
                currentStreak,
                bestStreak,
                assignedQuests,
                completedQuests,
                activeQuestProgress,
                finishedWeeklyTasks,
                totalWeeklyTasks,
                finishedWeeklyTasksThisWeek,
                weeklyTasksThisWeek,
                accountExists);
    }
}

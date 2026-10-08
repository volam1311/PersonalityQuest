package com.example.personalityquest.Services.profile;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.DAO.profile.AchievementDAO;
import com.example.personalityquest.DAO.profile.StreakDAO;
import com.example.personalityquest.Model.profile.Achievement;
import com.example.personalityquest.Model.profile.UserProfile;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Loads achievements from the database and unlocks them from stored quest, task, and streak totals.
 */
public class AchievementService {
    public static final String CRITERIA_ACCOUNT = "ACCOUNT";
    public static final String CRITERIA_CURRENT_STREAK = "CURRENT_STREAK";
    public static final String CRITERIA_BEST_STREAK = "BEST_STREAK";
    public static final String CRITERIA_FINISHED_TASKS = "FINISHED_WEEKLY_TASKS";
    public static final String CRITERIA_WEEK_COMPLETE = "WEEK_COMPLETE";
    public static final String CRITERIA_ASSIGNED_QUESTS = "ASSIGNED_QUESTS";
    public static final String CRITERIA_COMPLETED_QUESTS = "COMPLETED_QUESTS";
    public static final String CRITERIA_QUEST_PROGRESS = "QUEST_PROGRESS";

    private final AchievementDAO AchievementDAO;
    private final StreakService StreakService;

    public AchievementService() {
        super();
        this.AchievementDAO = new AchievementDAO();
        this.StreakService = new StreakService();
    }

    public AchievementService(AchievementDAO AchievementDAO) {
        super();
        this.AchievementDAO = AchievementDAO;

        StreakDAO streakDAO = new StreakDAO(AchievementDAO.getConnection());
        this.StreakService = new StreakService(streakDAO);
    }

    /**
     * Loads streak, quest, and weekly-task totals for an account.
     * Current streak uses {@link StreakService} so missed weeks match the rest of the app.
     */
    public UserProfile GetProgress(String email) throws SQLException {
        if (ApplicationManager.isEmpty(email)) {
            return UserProfile.empty();
        }

        UserProfile progress = AchievementDAO.GetProgress(email);

        return progress.withCurrentStreak(StreakService.GetCurrentStreak(email));
    }

    /**
     * Returns catalog achievements from the database, unlocking any the account now qualifies for.
     */
    public List<Achievement> GetAchievementsForEmail(String email) throws SQLException {
        EnsureCatalog();
        List<Achievement> catalog = AchievementDAO.GetCatalog();
        if (ApplicationManager.isEmpty(email)) {
            return catalog;
        }

        UserProfile progress = GetProgress(email);
        Set<Integer> unlockedIds = AchievementDAO.GetUnlockedIds(email);
        List<Achievement> achievements = new ArrayList<>();

        for (Achievement achievement : catalog) {
            boolean alreadyUnlocked = unlockedIds.contains(achievement.achievementId());
            boolean qualifies = MeetsCriteria(achievement, progress);
            if (qualifies && !alreadyUnlocked) {
                AchievementDAO.Unlock(email, achievement.achievementId());
            }
            achievements.add(achievement.withUnlocked(alreadyUnlocked || qualifies));
        }

        return achievements;
    }

    /**
     * Returns the account's most recently unlocked achievements (most recent first),
     * capped at {@code limit}. Also unlocks any achievements the account now qualifies for.
     */
    public List<Achievement> GetRecentlyUnlockedAchievements(String email, int limit) throws SQLException {
        if (ApplicationManager.isEmpty(email)) {
            return List.of();
        }

        List<Achievement> achievements = GetAchievementsForEmail(email);
        List<Integer> recentIds = com.example.personalityquest.DAO.profile.AchievementDAO.GetRecentlyUnlockedIds(email, limit);

        Map<Integer, Achievement> achievementsById = new HashMap<>();
        for (Achievement achievement : achievements) {
            achievementsById.put(achievement.achievementId(), achievement);
        }

        List<Achievement> recent = new ArrayList<>();
        for (Integer achievementId : recentIds) {
            Achievement achievement = achievementsById.get(achievementId);
            if (achievement != null) {
                recent.add(achievement);
            }
        }
        return recent;
    }

    boolean MeetsCriteria(Achievement achievement, UserProfile progress) {
        if (progress == null) {
            return false;
        }

        double threshold = achievement.threshold();
        return switch (achievement.criteriaType()) {
            case CRITERIA_ACCOUNT -> progress.accountExists() && threshold <= 1;
            case CRITERIA_CURRENT_STREAK -> progress.currentStreak() >= threshold;
            case CRITERIA_BEST_STREAK -> progress.bestStreak() >= threshold;
            case CRITERIA_FINISHED_TASKS -> progress.finishedWeeklyTasks() >= threshold;
            case CRITERIA_WEEK_COMPLETE -> progress.weeklyTasksThisWeek() > 0
                    && progress.finishedWeeklyTasksThisWeek() >= progress.weeklyTasksThisWeek()
                    && threshold <= 1;
            case CRITERIA_ASSIGNED_QUESTS -> progress.assignedQuests() >= threshold;
            case CRITERIA_COMPLETED_QUESTS -> progress.completedQuests() >= threshold;
            case CRITERIA_QUEST_PROGRESS -> progress.activeQuestProgress() >= threshold
                    || progress.completedQuests() >= 1;
            default -> false;
        };
    }

    private void EnsureCatalog() throws SQLException {
        AchievementDAO.EnsureTables();

        Insert(1, "Signed in", "Create an account and open your profile", CRITERIA_ACCOUNT, 1, 1);
        Insert(2, "First spark", "Record a completion and start a streak", CRITERIA_CURRENT_STREAK, 1, 1);
        Insert(3, "Three-week flame", "Reach a 3-week streak", CRITERIA_BEST_STREAK, 3, 2);
        Insert(4, "Week warrior", "Reach a 7-week streak", CRITERIA_BEST_STREAK, 7, 3);
        Insert(5, "Fortnight fire", "Reach a 14-week streak", CRITERIA_BEST_STREAK, 14, 4);
        Insert(6, "Personal best", "Hold a best streak of 7 weeks", CRITERIA_BEST_STREAK, 7, 3);
        Insert(7, "Task started", "Finish a weekly task", CRITERIA_FINISHED_TASKS, 1, 1);
        Insert(8, "Week complete", "Finish every weekly task this week", CRITERIA_WEEK_COMPLETE, 1, 1);
        Insert(9, "Quest accepted", "Have a quest assigned", CRITERIA_ASSIGNED_QUESTS, 1, 1);
        Insert(10, "On the path", "Reach 20% on a quest", CRITERIA_QUEST_PROGRESS, 0.2, 1);
        Insert(11, "Halfway hero", "Reach 50% on a quest", CRITERIA_QUEST_PROGRESS, 0.5, 2);
        Insert(12, "Labour complete", "Finish a quest", CRITERIA_COMPLETED_QUESTS, 1, 3);
        Insert(13, "Two labours", "Take on two quests", CRITERIA_ASSIGNED_QUESTS, 2, 2);
        Insert(14, "Triple threat", "Complete three quests", CRITERIA_COMPLETED_QUESTS, 3, 3);
        Insert(15, "Legend", "Reach a 21-week best streak", CRITERIA_BEST_STREAK, 21, 4);
    }

    private void Insert(
            int achievementId,
            String name,
            String description,
            String criteriaType,
            double threshold,
            int level) throws SQLException {
        AchievementDAO.InsertAchievement(achievementId, name, description, criteriaType, threshold, level);
    }
}

package com.example.personalityquest.Services.profile.criteria;

import com.example.personalityquest.Services.profile.AchievementService;

import java.util.Map;

/**
 * Maps each stored achievement criteria type to the strategy that evaluates it.
 */
public final class AchievementCriteria {
    private static final Map<String, AchievementCriterion> BY_TYPE = Map.of(
            AchievementService.CRITERIA_ACCOUNT, new AccountExistsCriterion(),
            AchievementService.CRITERIA_CURRENT_STREAK, new CurrentStreakCriterion(),
            AchievementService.CRITERIA_BEST_STREAK, new BestStreakCriterion(),
            AchievementService.CRITERIA_FINISHED_TASKS, new FinishedWeeklyTasksCriterion(),
            AchievementService.CRITERIA_WEEK_COMPLETE, new WeekCompleteCriterion(),
            AchievementService.CRITERIA_ASSIGNED_QUESTS, new AssignedQuestsCriterion(),
            AchievementService.CRITERIA_COMPLETED_QUESTS, new CompletedQuestsCriterion(),
            AchievementService.CRITERIA_QUEST_PROGRESS, new QuestProgressCriterion()
    );

    private AchievementCriteria() {
    }

    /**
     * @param criteriaType the criteria key stored with an achievement
     * @return the matching strategy, or null when the type is unknown
     */
    public static AchievementCriterion ForType(String criteriaType) {
        if (criteriaType == null) {
            return null;
        }
        return BY_TYPE.get(criteriaType);
    }
}

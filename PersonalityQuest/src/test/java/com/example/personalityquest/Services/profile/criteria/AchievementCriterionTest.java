package com.example.personalityquest.Services.profile.criteria;

import com.example.personalityquest.Model.profile.UserProfile;
import com.example.personalityquest.Services.profile.AchievementService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AchievementCriterionTest {
    @Test
    void eachStoredCriteriaTypeHasAStrategy() {
        assertNotNull(AchievementCriteria.ForType(AchievementService.CRITERIA_ACCOUNT));
        assertNotNull(AchievementCriteria.ForType(AchievementService.CRITERIA_CURRENT_STREAK));
        assertNotNull(AchievementCriteria.ForType(AchievementService.CRITERIA_BEST_STREAK));
        assertNotNull(AchievementCriteria.ForType(AchievementService.CRITERIA_FINISHED_TASKS));
        assertNotNull(AchievementCriteria.ForType(AchievementService.CRITERIA_WEEK_COMPLETE));
        assertNotNull(AchievementCriteria.ForType(AchievementService.CRITERIA_ASSIGNED_QUESTS));
        assertNotNull(AchievementCriteria.ForType(AchievementService.CRITERIA_COMPLETED_QUESTS));
        assertNotNull(AchievementCriteria.ForType(AchievementService.CRITERIA_QUEST_PROGRESS));
    }

    @Test
    void unknownCriteriaTypeHasNoStrategy() {
        assertNull(AchievementCriteria.ForType("NOT_A_RULE"));
        assertNull(AchievementCriteria.ForType(null));
    }

    @Test
    void rulesMatchThePreviousUnlockThresholds() {
        UserProfile progress = new UserProfile(2, 7, 2, 1, 0.5, 3, 0, 2, 2, true);

        assertTrue(AchievementCriteria.ForType(AchievementService.CRITERIA_ACCOUNT).IsMet(progress, 1));
        assertFalse(AchievementCriteria.ForType(AchievementService.CRITERIA_ACCOUNT).IsMet(progress, 2));
        assertTrue(AchievementCriteria.ForType(AchievementService.CRITERIA_CURRENT_STREAK).IsMet(progress, 2));
        assertFalse(AchievementCriteria.ForType(AchievementService.CRITERIA_CURRENT_STREAK).IsMet(progress, 3));
        assertTrue(AchievementCriteria.ForType(AchievementService.CRITERIA_BEST_STREAK).IsMet(progress, 7));
        assertFalse(AchievementCriteria.ForType(AchievementService.CRITERIA_BEST_STREAK).IsMet(progress, 8));
        assertTrue(AchievementCriteria.ForType(AchievementService.CRITERIA_FINISHED_TASKS).IsMet(progress, 3));
        assertFalse(AchievementCriteria.ForType(AchievementService.CRITERIA_FINISHED_TASKS).IsMet(progress, 4));
        assertTrue(AchievementCriteria.ForType(AchievementService.CRITERIA_WEEK_COMPLETE).IsMet(progress, 1));
        assertFalse(AchievementCriteria.ForType(AchievementService.CRITERIA_WEEK_COMPLETE).IsMet(progress, 2));
        assertTrue(AchievementCriteria.ForType(AchievementService.CRITERIA_ASSIGNED_QUESTS).IsMet(progress, 2));
        assertFalse(AchievementCriteria.ForType(AchievementService.CRITERIA_ASSIGNED_QUESTS).IsMet(progress, 3));
        assertTrue(AchievementCriteria.ForType(AchievementService.CRITERIA_COMPLETED_QUESTS).IsMet(progress, 1));
        assertFalse(AchievementCriteria.ForType(AchievementService.CRITERIA_COMPLETED_QUESTS).IsMet(progress, 2));
        assertTrue(AchievementCriteria.ForType(AchievementService.CRITERIA_QUEST_PROGRESS).IsMet(progress, 0.5));
        assertTrue(AchievementCriteria.ForType(AchievementService.CRITERIA_QUEST_PROGRESS).IsMet(progress, 0.9));
    }

    @Test
    void emptyWeekDoesNotCountAsComplete() {
        UserProfile progress = new UserProfile(0, 0, 0, 0, 0, 0, 0, 0, 0, true);

        assertFalse(AchievementCriteria.ForType(AchievementService.CRITERIA_WEEK_COMPLETE).IsMet(progress, 1));
        assertFalse(AchievementCriteria.ForType(AchievementService.CRITERIA_QUEST_PROGRESS)
                .IsMet(new UserProfile(0, 0, 0, 0, 0.1, 0, 0, 0, 0, true), 0.5));
    }
}

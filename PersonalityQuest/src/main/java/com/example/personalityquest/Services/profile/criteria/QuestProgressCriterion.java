package com.example.personalityquest.Services.profile.criteria;

import com.example.personalityquest.Model.profile.UserProfile;

/** Unlocks when active quest progress reaches the threshold, or a quest is already complete. */
public final class QuestProgressCriterion implements AchievementCriterion {
    @Override
    public boolean IsMet(UserProfile progress, double threshold) {
        return progress.activeQuestProgress() >= threshold || progress.completedQuests() >= 1;
    }
}

package com.example.personalityquest.Services.profile.criteria;

import com.example.personalityquest.Model.profile.UserProfile;

/** Unlocks when the number of completed quests reaches the threshold. */
public final class CompletedQuestsCriterion implements AchievementCriterion {
    @Override
    public boolean IsMet(UserProfile progress, double threshold) {
        return progress.completedQuests() >= threshold;
    }
}

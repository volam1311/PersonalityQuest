package com.example.personalityquest.Services.profile.criteria;

import com.example.personalityquest.Model.profile.UserProfile;

/** Unlocks when the number of assigned quests reaches the threshold. */
public final class AssignedQuestsCriterion implements AchievementCriterion {
    @Override
    public boolean IsMet(UserProfile progress, double threshold) {
        return progress.assignedQuests() >= threshold;
    }
}

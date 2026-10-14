package com.example.personalityquest.Services.profile.criteria;

import com.example.personalityquest.Model.profile.UserProfile;

/** Unlocks when the best streak reaches the threshold. */
public final class BestStreakCriterion implements AchievementCriterion {
    @Override
    public boolean IsMet(UserProfile progress, double threshold) {
        return progress.bestStreak() >= threshold;
    }
}

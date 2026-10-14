package com.example.personalityquest.Services.profile.criteria;

import com.example.personalityquest.Model.profile.UserProfile;

/** Unlocks when the current streak reaches the threshold. */
public final class CurrentStreakCriterion implements AchievementCriterion {
    @Override
    public boolean IsMet(UserProfile progress, double threshold) {
        return progress.currentStreak() >= threshold;
    }
}

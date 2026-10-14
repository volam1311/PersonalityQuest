package com.example.personalityquest.Services.profile.criteria;

import com.example.personalityquest.Model.profile.UserProfile;

/** Unlocks when the number of finished weekly tasks reaches the threshold. */
public final class FinishedWeeklyTasksCriterion implements AchievementCriterion {
    @Override
    public boolean IsMet(UserProfile progress, double threshold) {
        return progress.finishedWeeklyTasks() >= threshold;
    }
}

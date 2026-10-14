package com.example.personalityquest.Services.profile.criteria;

import com.example.personalityquest.Model.profile.UserProfile;

/** Unlocks when every weekly task for the current week is finished. */
public final class WeekCompleteCriterion implements AchievementCriterion {
    @Override
    public boolean IsMet(UserProfile progress, double threshold) {
        return progress.weeklyTasksThisWeek() > 0
                && progress.finishedWeeklyTasksThisWeek() >= progress.weeklyTasksThisWeek()
                && threshold <= 1;
    }
}

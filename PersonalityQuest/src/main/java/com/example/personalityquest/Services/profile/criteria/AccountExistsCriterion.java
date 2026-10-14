package com.example.personalityquest.Services.profile.criteria;

import com.example.personalityquest.Model.profile.UserProfile;

/** Unlocks when an account exists. */
public final class AccountExistsCriterion implements AchievementCriterion {
    @Override
    public boolean IsMet(UserProfile progress, double threshold) {
        return progress.accountExists() && threshold <= 1;
    }
}

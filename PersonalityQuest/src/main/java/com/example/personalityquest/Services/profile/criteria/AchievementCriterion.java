package com.example.personalityquest.Services.profile.criteria;

import com.example.personalityquest.Model.profile.UserProfile;

/**
 * Decides whether an account's progress meets one achievement rule.
 */
public interface AchievementCriterion {
    /**
     * @param progress the account totals to test
     * @param threshold the value required by the achievement
     * @return whether the progress meets this rule
     */
    boolean IsMet(UserProfile progress, double threshold);
}

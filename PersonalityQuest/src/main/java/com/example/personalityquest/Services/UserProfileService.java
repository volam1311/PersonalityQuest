package com.example.personalityquest.Services;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.Model.EmailDetails;
import com.example.personalityquest.Model.UserProfile;

import java.util.Locale;

/**
 * Display helpers for the profile page: name formatting, archetype labels, and radar values.
 */
public class UserProfileService {
    public static final String UNASSIGNED_ARCHETYPE = "Unassigned";
    public static final String UNKNOWN_TYPE = "—";
    public static final String DEFAULT_DISPLAY_NAME = "Adventurer";

    private UserProfileService() {
    }

    /**
     * Builds a display name from first and last name, falling back to username.
     */
    public static String DisplayName(EmailDetails details) {
        if (details == null) {
            return DEFAULT_DISPLAY_NAME;
        }

        String first = SafeTrim(details.getFirstName());
        String last = SafeTrim(details.getLastName());
        String full = (first + " " + last).trim();
        if (!full.isEmpty()) {
            return full;
        }

        String userName = SafeTrim(details.getUserName());
        if (!userName.isEmpty()) {
            return userName;
        }

        return DEFAULT_DISPLAY_NAME;
    }

    /**
     * Formats an archetype for display, adding "The" when it is missing.
     */
    public static String FormatArchetypeName(String name) {
        if (ApplicationManager.isEmpty(name)) {
            return UNASSIGNED_ARCHETYPE;
        }

        String titled = TitleCase(name.trim());
        if (titled.toLowerCase(Locale.ROOT).startsWith("the ")) {
            return titled;
        }
        return "The " + titled;
    }

    /**
     * Returns a stored archetype description, or {@link #UNKNOWN_TYPE} when none exists.
     */
    public static String PersonalityType(String archetypeDescription) {
        String description = SafeTrim(archetypeDescription);
        if (description.isEmpty()) {
            return UNKNOWN_TYPE;
        }
        return description;
    }

    /**
     * Builds five radar-chart values between 0 and 1 from database profile totals.
     */
    public static double[] RadarValues(UserProfile profile) {
        if (profile == null) {
            return new double[]{0, 0, 0, 0, 0};
        }

        double weeklyRatio = Ratio(profile.finishedWeeklyTasksThisWeek(), profile.weeklyTasksThisWeek());
        double streakRatio = Ratio(profile.currentStreak(), Math.max(profile.bestStreak(), 1));
        double questRatio = Ratio(profile.completedQuests(), profile.assignedQuests());
        double finishedRatio = Ratio(profile.finishedWeeklyTasks(), profile.totalWeeklyTasks());

        return new double[]{
                Clamp(profile.activeQuestProgress()),
                Clamp(weeklyRatio),
                Clamp(streakRatio),
                Clamp(questRatio),
                Clamp(finishedRatio)
        };
    }

    private static double Ratio(int numerator, int denominator) {
        if (denominator <= 0) {
            return 0;
        }
        return (double) numerator / denominator;
    }

    private static String SafeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private static String TitleCase(String value) {
        String[] words = value.split("\\s+");
        StringBuilder titled = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }
            if (titled.length() > 0) {
                titled.append(' ');
            }
            titled.append(Character.toUpperCase(word.charAt(0)));
            if (word.length() > 1) {
                titled.append(word.substring(1).toLowerCase(Locale.ROOT));
            }
        }
        return titled.toString();
    }

    private static double Clamp(double value) {
        if (Double.isNaN(value) || value < 0) {
            return 0;
        }
        return Math.min(value, 1);
    }
}

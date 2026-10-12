package com.example.personalityquest.Services.navigation;

import java.util.Objects;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/** Stores the selected application theme and its local preference */
public final class ThemeService {
    private static final String THEME_PREFERENCE_KEY = "theme";

    public enum Theme {
        DARK("theme-dark.css"),
        DUSK("theme-dusk.css"),
        FOREST("theme-forest.css"),
        OCEAN("theme-ocean.css"),
        LIGHT("theme-light.css");

        private final String stylesheetName;

        Theme(String stylesheetName) {
            this.stylesheetName = stylesheetName;
        }

        public String getStylesheetName() {
            return stylesheetName;
        }
    }

    private final Preferences preferences;
    private Theme currentTheme;

    public ThemeService() {
        this(Preferences.userNodeForPackage(ThemeService.class));
    }

    public ThemeService(Preferences preferences) {
        this.preferences = Objects.requireNonNull(preferences);
        this.currentTheme = ReadSavedTheme();
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    public void setCurrentTheme(Theme theme) {
        Theme selectedTheme = Objects.requireNonNull(theme);
        preferences.put(THEME_PREFERENCE_KEY, selectedTheme.name());
        try {
            preferences.flush();
            currentTheme = selectedTheme;
        } catch (BackingStoreException exception) {
            throw new IllegalStateException("Unable to save the selected theme", exception);
        }
    }

    private Theme ReadSavedTheme() {
        String savedTheme = preferences.get(THEME_PREFERENCE_KEY, Theme.DARK.name());
        try {
            return Theme.valueOf(savedTheme);
        } catch (IllegalArgumentException exception) {
            return Theme.DARK;
        }
    }
}

package com.example.personalityquest.Services.navigation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/** Stores the selected application theme and its local preference */
public final class ThemeService {
    private static final String THEME_PREFERENCE_KEY = "theme";

    /** A named colour theme and the stylesheet that applies it. */
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
    private final List<ThemeChangeListener> listeners = new ArrayList<>();
    private Theme currentTheme;

    public ThemeService() {
        this(Preferences.userNodeForPackage(ThemeService.class));
    }

    public ThemeService(Preferences preferences) {
        this.preferences = Objects.requireNonNull(preferences);
        this.currentTheme = ReadSavedTheme();
    }

    private static ThemeService instance;
    public static ThemeService GetInstance(){
        if (instance == null){
            instance = new ThemeService();
        }

        return instance;
    }


    public Theme getCurrentTheme() {
        return currentTheme;
    }

    /**
     * Registers a listener that is told after a theme is saved.
     * @param listener the observer to notify
     */
    public void addThemeChangeListener(ThemeChangeListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
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
        for (ThemeChangeListener listener : List.copyOf(listeners)) {
            listener.themeChanged(currentTheme);
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

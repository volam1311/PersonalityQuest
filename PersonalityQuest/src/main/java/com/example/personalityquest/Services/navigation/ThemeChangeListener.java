package com.example.personalityquest.Services.navigation;

/**
 * Receives the theme after {@link ThemeService} has saved a new selection.
 */
public interface ThemeChangeListener {
    /**
     * @param theme the theme that is now current
     */
    void themeChanged(ThemeService.Theme theme);
}

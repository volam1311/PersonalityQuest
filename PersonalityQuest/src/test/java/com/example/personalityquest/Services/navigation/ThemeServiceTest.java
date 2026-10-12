package com.example.personalityquest.Services.navigation;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ThemeServiceTest {
    private Preferences preferences;

    @BeforeEach
    void setUp() {
        preferences = Preferences.userNodeForPackage(ThemeServiceTest.class)
                .node("theme-test-" + UUID.randomUUID());
    }

    @AfterEach
    void tearDown() throws BackingStoreException {
        preferences.removeNode();
    }

    @Test
    void selectedThemePersistsAcrossServiceInstances() {
        ThemeService firstService = new ThemeService(preferences);
        firstService.setCurrentTheme(ThemeService.Theme.FOREST);

        ThemeService reopenedService = new ThemeService(preferences);

        assertEquals(ThemeService.Theme.FOREST, reopenedService.getCurrentTheme());
    }

    @Test
    void invalidSavedThemeFallsBackToDark() {
        preferences.put("theme", "UNSUPPORTED");

        ThemeService service = new ThemeService(preferences);

        assertEquals(ThemeService.Theme.DARK, service.getCurrentTheme());
    }

    @Test
    void everyThemeHasAStylesheetResource() {
        for (ThemeService.Theme theme : ThemeService.Theme.values()) {
            String stylesheetPath = "/com/example/personalityquest/css/" + theme.getStylesheetName();
            assertNotNull(ThemeService.class.getResource(stylesheetPath), stylesheetPath);
        }
    }
}

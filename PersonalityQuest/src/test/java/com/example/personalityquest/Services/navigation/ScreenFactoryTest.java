package com.example.personalityquest.Services.navigation;

import com.example.personalityquest.ScreenEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ScreenFactoryTest {
    @Test
    void everyScreenHasAnFxmlFileAndTitle() {
        ScreenFactory factory = new ScreenFactory();

        for (ScreenEnum screen : ScreenEnum.values()) {
            ScreenFactory.ScreenDefinition definition = factory.Create(screen);
            assertFalse(definition.fxmlFileName().isBlank());
            assertFalse(definition.title().isBlank());
            assertEquals(
                    "/com/example/personalityquest/" + definition.fxmlFileName(),
                    NavigationService.SetFxmlDoc(screen));
        }
    }

    @Test
    void dashboardKeepsItsExistingPathAndTitle() {
        ScreenFactory.ScreenDefinition dashboard = new ScreenFactory().Create(ScreenEnum.DASHBOARD);

        assertEquals("Dashboard.fxml", dashboard.fxmlFileName());
        assertEquals("Dashboard", dashboard.title());
    }
}

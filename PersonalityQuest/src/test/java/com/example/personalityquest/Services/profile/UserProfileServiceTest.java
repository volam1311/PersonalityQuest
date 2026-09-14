package com.example.personalityquest.Services.profile;

import com.example.personalityquest.Model.auth.EmailDetails;
import com.example.personalityquest.Services.profile.UserProfileService;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserProfileServiceTest {
    private Connection connection;

    @Test
    void DisplayNameUsesFirstAndLastName() {
        EmailDetails details =
                new EmailDetails("test@email.com", "player", "John", "Doe");

        assertEquals("John Doe", UserProfileService.DisplayName(details));
    }

    @Test
    void DisplayNameFallsBackToUsername() {
        EmailDetails details =
                new EmailDetails("test@email.com", "player", "", "");

        assertEquals("player", UserProfileService.DisplayName(details));
    }

    @Test
    void DisplayNameUsesDefaultWhenNull() {
        assertEquals(UserProfileService.DisplayName(null), UserProfileService.DisplayName(null));
    }

    @Test
    void FormatArchetypeAddsAPrefix() {
        assertEquals(
                "The Explorer",
                UserProfileService.FormatArchetypeName("explorer")
        );
    }

    @Test
    void FormatArchetypeDoesNotDuplicateThePrefix() {
        assertEquals(
                "The Explorer",
                UserProfileService.FormatArchetypeName("the explorer")
        );
    }

    @Test
    void FormatArchetypeUsesUnassignedForBlankName() {
        assertEquals(
                UserProfileService.UNASSIGNED_ARCHETYPE,
                UserProfileService.FormatArchetypeName("")
        );
    }

}

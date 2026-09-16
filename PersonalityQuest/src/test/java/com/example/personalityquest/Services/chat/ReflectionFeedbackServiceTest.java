package com.example.personalityquest.Services.chat;

import com.example.personalityquest.Model.quiz.Archetype;
import com.example.personalityquest.Model.chat.ReflectionContext;
import com.example.personalityquest.Services.chat.ChatCompletionClient;
import com.example.personalityquest.Services.chat.ReflectionFeedbackService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ReflectionFeedbackServiceTest {
    @AfterEach
    void tearDown() {
        ReflectionFeedbackService.Reset();
    }

    @Test
    void FeedbackForRejectsEmptyReflection() {
        ReflectionContext context = new ReflectionContext(
                "Speak to a stranger",
                "Start a short conversation",
                "The Explorer's Path",
                "Explorer",
                "",
                "",
                "",
                "   ");

        assertThrowsExactly(IllegalArgumentException.class, () -> ReflectionFeedbackService.FeedbackFor(context));
        assertThrowsExactly(IllegalArgumentException.class, () -> ReflectionFeedbackService.FeedbackFor(null));
    }

    @Test
    void FeedbackForReturnsTrimmedClientReply() throws Exception {
        ReflectionFeedbackService.SetClient(new ChatCompletionClient() {
            @Override
            public String Complete(String systemPrompt, String userPrompt) {
                assertTrue(systemPrompt.contains("PersonalityQuest"));
                assertTrue(userPrompt.contains("I tried talking to a classmate"));
                return "  You showed curiosity.  ";
            }
        });

        String feedback = ReflectionFeedbackService.FeedbackFor(new ReflectionContext(
                "Speak to a stranger",
                "Start a short conversation",
                "The Explorer's Path",
                "Explorer",
                Archetype.EXPLORER.getSmallDescription(),
                Archetype.EXPLORER.getStrengths(),
                Archetype.EXPLORER.getWeaknesses(),
                "I tried talking to a classmate"));

        assertEquals("You showed curiosity.", feedback);
    }

    @Test
    void UserPromptIncludesTaskAndReflection() {
        String prompt = ReflectionFeedbackService.UserPrompt(new ReflectionContext(
                "Speak to a stranger",
                "Start a short conversation",
                "The Explorer's Path",
                "Explorer",
                "You seek freedom.",
                "Independent and curious.",
                "Can become restless.",
                "I said hello to someone new."));

        assertTrue(prompt.contains("Speak to a stranger"));
        assertTrue(prompt.contains("Start a short conversation"));
        assertTrue(prompt.contains("The Explorer's Path"));
        assertTrue(prompt.contains("Explorer"));
        assertTrue(prompt.contains("I said hello to someone new."));
    }
}

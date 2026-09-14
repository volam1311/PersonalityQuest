package com.example.personalityquest;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EnvLoaderTest {
    @Test
    void ParseIgnoresCommentsAndBlankLines() {
        Map<String, String> values = EnvLoader.Parse("""
                # a comment
                OPENAI_API_KEY=test-key

                export OTHER=value
                QUOTED="hello world"
                """);

        assertEquals("test-key", values.get("OPENAI_API_KEY"));
        assertEquals("value", values.get("OTHER"));
        assertEquals("hello world", values.get("QUOTED"));
        assertEquals(3, values.size());
    }

    @Test
    void ParseHandlesEmptyAndNullContents() {
        assertTrue(EnvLoader.Parse(null).isEmpty());
        assertTrue(EnvLoader.Parse("").isEmpty());
        assertTrue(EnvLoader.Parse("   \n# only a comment").isEmpty());
    }

    @Test
    void ParseUsesTheFirstEqualsSignInTheValue() {
        Map<String, String> values = EnvLoader.Parse("OPENAI_API_KEY=abc=def");
        assertEquals("abc=def", values.get("OPENAI_API_KEY"));
    }
}

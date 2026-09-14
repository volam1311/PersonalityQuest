package com.example.personalityquest.Services.chat;

import com.example.personalityquest.Services.chat.OpenAiClient;
import com.example.personalityquest.Services.chat.OpenAiJson;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OpenAiJsonTest {
    @Test
    void EscapeEncodesQuotesAndNewlines() {
        assertEquals("hello", OpenAiJson.Escape("hello"));
        assertEquals("say \\\"hi\\\"", OpenAiJson.Escape("say \"hi\""));
        assertEquals("line1\\nline2", OpenAiJson.Escape("line1\nline2"));
        assertEquals("", OpenAiJson.Escape(null));
    }

    @Test
    void ExtractStringReadsAssistantContent() {
        String json = """
                {"id":"chatcmpl-1","choices":[{"index":0,"message":{"role":"assistant","content":"Nice work.\\nKeep going."}}]}
                """;

        assertEquals("Nice work.\nKeep going.", OpenAiJson.ExtractString(json, "content", "choices"));
    }

    @Test
    void ExtractStringUnescapesQuotes() {
        String json = "{\"choices\":[{\"message\":{\"content\":\"She said \\\"hello\\\"\"}}]}";
        assertEquals("She said \"hello\"", OpenAiJson.ExtractString(json, "content", "choices"));
    }

    @Test
    void ExtractStringReadsErrorMessage() {
        String json = "{\"error\":{\"message\":\"Incorrect API key provided\",\"type\":\"invalid_request_error\"}}";
        assertEquals("Incorrect API key provided", OpenAiJson.ExtractString(json, "message", "error"));
    }

    @Test
    void RequestBodyIncludesEscapedPrompts() {
        String body = OpenAiClient.RequestBody("gpt-4o-mini", "Be kind", "I wrote \"notes\"");
        assertTrue(body.contains("\"model\": \"gpt-4o-mini\""));
        assertTrue(body.contains("\"content\": \"Be kind\""));
        assertTrue(body.contains("I wrote \\\"notes\\\""));
    }

    @Test
    void ErrorMessageHidesRejectedKeys() {
        String message = OpenAiClient.ErrorMessage(401, "{\"error\":{\"message\":\"Incorrect API key provided\"}}");
        assertEquals("The OpenAI API key was rejected. Check OPENAI_API_KEY in PersonalityQuest/.env.", message);
    }
}

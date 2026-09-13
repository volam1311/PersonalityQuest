package com.example.personalityquest.Services;

/**
 * Sends a system and user prompt to a chat model and returns the assistant reply.
 */
public interface ChatCompletionClient {
    /**
     * Completes a two-message chat prompt.
     * @param systemPrompt Instructions for the model
     * @param userPrompt The user-facing request
     * @return The assistant message text
     * @throws Exception If the request cannot be completed
     */
    String Complete(String systemPrompt, String userPrompt) throws Exception;
}

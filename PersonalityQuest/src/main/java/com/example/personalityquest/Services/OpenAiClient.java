package com.example.personalityquest.Services;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.EnvLoader;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Calls OpenAI's Chat Completions API using {@code OPENAI_API_KEY} from the environment or .env file.
 */
public class OpenAiClient implements ChatCompletionClient {
    static final String DEFAULT_MODEL = "gpt-4o-mini";
    private static final URI CHAT_COMPLETIONS = URI.create("https://api.openai.com/v1/chat/completions");
    private static final Duration TIMEOUT = Duration.ofSeconds(45);

    private final HttpClient httpClient;
    private final String model;

    public OpenAiClient() {
        this(HttpClient.newBuilder().connectTimeout(TIMEOUT).build(), DEFAULT_MODEL);
    }

    OpenAiClient(HttpClient httpClient, String model) {
        this.httpClient = httpClient;
        this.model = ApplicationManager.isEmpty(model) ? DEFAULT_MODEL : model;
    }

    @Override
    public String Complete(String systemPrompt, String userPrompt) throws Exception {
        String apiKey = EnvLoader.Get("OPENAI_API_KEY");
        if (ApplicationManager.isEmpty(apiKey)) {
            throw new IllegalStateException(
                    "Add OPENAI_API_KEY to PersonalityQuest/.env to get AI feedback.");
        }

        String body = RequestBody(model, systemPrompt, userPrompt);
        HttpRequest request = HttpRequest.newBuilder(CHAT_COMPLETIONS)
                .timeout(TIMEOUT)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String json = response.body() == null ? "" : response.body();
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException(ErrorMessage(response.statusCode(), json));
        }

        String content = OpenAiJson.ExtractString(json, "content", "choices");
        if (ApplicationManager.isEmpty(content)) {
            throw new IllegalStateException("OpenAI returned an empty reply.");
        }
        return content.trim();
    }

    public static String RequestBody(String model, String systemPrompt, String userPrompt) {
        return """
                {
                  "model": "%s",
                  "temperature": 0.7,
                  "max_tokens": 400,
                  "messages": [
                    {"role": "system", "content": "%s"},
                    {"role": "user", "content": "%s"}
                  ]
                }
                """.formatted(
                OpenAiJson.Escape(model),
                OpenAiJson.Escape(systemPrompt == null ? "" : systemPrompt),
                OpenAiJson.Escape(userPrompt == null ? "" : userPrompt)
        );
    }

    public static String ErrorMessage(int statusCode, String json) {
        String apiMessage = OpenAiJson.ExtractString(json, "message", "error");
        if (!ApplicationManager.isEmpty(apiMessage)) {
            if (statusCode == 401 || statusCode == 403) {
                return "The OpenAI API key was rejected. Check OPENAI_API_KEY in PersonalityQuest/.env.";
            }
            return apiMessage;
        }
        if (statusCode == 401 || statusCode == 403) {
            return "The OpenAI API key was rejected. Check OPENAI_API_KEY in PersonalityQuest/.env.";
        }
        return "OpenAI request failed (HTTP " + statusCode + ").";
    }
}

package com.example.personalityquest.Services;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.DAO.ReflectionFeedbackDAO;
import com.example.personalityquest.Model.Archetype;
import com.example.personalityquest.Model.Quest;
import com.example.personalityquest.Model.ReflectionContext;
import com.example.personalityquest.Model.Task;
import com.example.personalityquest.Model.WeeklyTask;

import java.sql.SQLException;

/**
 * Builds a coaching prompt from a weekly-task reflection and asks OpenAI for feedback.
 */
public final class ReflectionFeedbackService {
    static final String SYSTEM_PROMPT = """
            You are a supportive coach inside PersonalityQuest, a growth app based on Jungian brand archetypes.
            The user just wrote a reflection after attempting a weekly practice.
            Weekly practices are the tasks people do each week. They are not storyline quest tasks.
            Reply in 2-4 short paragraphs of plain text (no bullet lists, no markdown headings).
            Acknowledge what they actually wrote, name one strength you can see, connect it to their archetype when that helps, and offer one concrete next step.
            Keep a warm, grounded tone. Do not mention being an AI unless asked.
            """;

    private static ChatCompletionClient client = new OpenAiClient();

    private ReflectionFeedbackService() {
    }

    /**
     * Replaces the chat client. Used by tests to avoid calling the live OpenAI API.
     */
    public static void SetClient(ChatCompletionClient chatClient) {
        client = chatClient == null ? new OpenAiClient() : chatClient;
    }

    /**
     * Restores the default OpenAI client.
     */
    public static void Reset() {
        client = new OpenAiClient();
    }

    /**
     * Asks the model for feedback on the given reflection context.
     * @param context Task, quest, archetype, and reflection details
     * @return Coaching feedback for the user
     * @throws IllegalArgumentException If the reflection is empty
     * @throws Exception If the chat client cannot complete the request
     */
    public static String FeedbackFor(ReflectionContext context) throws Exception {
        if (context == null || ApplicationManager.isEmpty(context.reflection())) {
            throw new IllegalArgumentException("Write a reflection before requesting AI feedback.");
        }

        return client.Complete(SYSTEM_PROMPT, UserPrompt(context)).trim();
    }

    /**
     * Builds a prompt containing the task, quest, archetype, and user reflection.
     */
    public static String UserPrompt(ReflectionContext context) {
        ReflectionContext safe = context == null
                ? new ReflectionContext("", "", "", "", "", "", "", "")
                : context;

        return """
                Task: %s
                Task description: %s
                Current labour: %s
                Archetype: %s
                Archetype overview: %s
                Archetype strengths: %s
                Archetype growth edges: %s

                User reflection:
                %s
                """.formatted(
                Fallback(safe.taskName(), "Weekly task"),
                Fallback(safe.taskDescription(), "No description provided."),
                Fallback(safe.questName(), "Unknown labour"),
                Fallback(safe.archetypeName(), "Unknown"),
                Fallback(safe.archetypeOverview(), "Not available."),
                Fallback(safe.archetypeStrengths(), "Not available."),
                Fallback(safe.archetypeWeaknesses(), "Not available."),
                safe.reflection()
        );
    }

    /**
     * Enriches a task with quest and archetype details for the feedback prompt.
     */
    public static ReflectionContext ContextFor(Task task, String questName, String reflection) {
        String resolvedQuestName = questName;
        String archetypeName = "";
        String overview = "";
        String strengths = "";
        String weaknesses = "";

        if (task != null) {
            try {
                Quest quest = QuestService.GetQuestForLabourId(task.getLabourId());
                if (quest != null) {
                    if (ApplicationManager.isEmpty(resolvedQuestName)) {
                        resolvedQuestName = quest.getName();
                    }
                    archetypeName = QuestService.GetArchetypeName(quest.getArchetypeId());
                    overview = QuestService.GetArchetypeDescription(quest.getArchetypeId());
                    Archetype matched = MatchArchetype(archetypeName);
                    if (matched != null) {
                        if (ApplicationManager.isEmpty(overview)) {
                            overview = matched.getOverview();
                        }
                        strengths = matched.getStrengths();
                        weaknesses = matched.getWeaknesses();
                    }
                }
            } catch (Exception ignored) {
                // Feedback still works with the task and reflection alone.
            }
        }

        return new ReflectionContext(
                task == null ? "" : task.getName(),
                task == null ? "" : task.getDescription(),
                resolvedQuestName,
                archetypeName,
                overview,
                strengths,
                weaknesses,
                reflection
        );
    }

    /**
     * Saves AI feedback for a weekly task so it can be shown again later.
     */
    public static void Save(WeeklyTask weeklyTask, String feedback) throws SQLException {
        if (weeklyTask == null || ApplicationManager.isEmpty(feedback)) {
            return;
        }
        ReflectionFeedbackDAO.Save(
                weeklyTask.getEmail(),
                weeklyTask.getTaskId(),
                weeklyTask.getWeekStarted(),
                feedback.trim());
    }

    /**
     * Loads stored AI feedback for a weekly task, or an empty string when none exists.
     */
    public static String Find(WeeklyTask weeklyTask) throws SQLException {
        if (weeklyTask == null) {
            return "";
        }
        return ReflectionFeedbackDAO.Find(
                weeklyTask.getEmail(),
                weeklyTask.getTaskId(),
                weeklyTask.getWeekStarted());
    }

    static Archetype MatchArchetype(String name) {
        if (ApplicationManager.isEmpty(name)) {
            return null;
        }

        String normalised = name.trim();
        if (normalised.regionMatches(true, 0, "The ", 0, 4)) {
            normalised = normalised.substring(4).trim();
        }

        for (Archetype archetype : Archetype.values()) {
            if (archetype.getDisplayName().equalsIgnoreCase(normalised)
                    || archetype.name().equalsIgnoreCase(normalised)) {
                return archetype;
            }
        }
        return null;
    }

    private static String Fallback(String value, String fallback) {
        return ApplicationManager.isEmpty(value) ? fallback : value;
    }
}

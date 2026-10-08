package com.example.personalityquest.Services.chat;

import com.example.personalityquest.Services.quest.QuestService;
import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.DAO.chat.ReflectionFeedbackDAO;
import com.example.personalityquest.Model.quiz.Archetype;
import com.example.personalityquest.Model.quest.Quest;
import com.example.personalityquest.Model.chat.ReflectionContext;
import com.example.personalityquest.Model.quest.Task;
import com.example.personalityquest.Model.quest.WeeklyTask;

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

    private ChatCompletionClient client;
    private ReflectionFeedbackDAO ReflectionFeedbackDAO;

    public ReflectionFeedbackService() {
        client = new OpenAiClient();
        ReflectionFeedbackDAO = new ReflectionFeedbackDAO();
    }

    public ReflectionFeedbackService(ChatCompletionClient client){
        this.client = client;
    }

    public ReflectionFeedbackService(ReflectionFeedbackDAO reflectionFeedbackDAO) {
        this.ReflectionFeedbackDAO = reflectionFeedbackDAO;
    }

    public ReflectionFeedbackService(ReflectionFeedbackDAO reflectionFeedbackDAO, ChatCompletionClient client) {
        this.ReflectionFeedbackDAO = reflectionFeedbackDAO;
        this.client = client;
    }

    /**
     * Replaces the chat client. Used by tests to avoid calling the live OpenAI API.
     */
    public void SetClient(ChatCompletionClient chatClient) {
        client = chatClient == null ? new OpenAiClient() : chatClient;
    }

    /**
     * Restores the default OpenAI client.
     */
    public void Reset() {
        client = new OpenAiClient();
    }

    /**
     * Asks the model for feedback on the given reflection context.
     * @param context Task, quest, archetype, and reflection details
     * @return Coaching feedback for the user
     * @throws IllegalArgumentException If the reflection is empty
     * @throws Exception If the chat client cannot complete the request
     */
    public String FeedbackFor(ReflectionContext context) throws Exception {
        if (context == null || ApplicationManager.isEmpty(context.reflection())) {
            throw new IllegalArgumentException("Write a reflection before requesting AI feedback.");
        }

        return client.Complete(SYSTEM_PROMPT, UserPrompt(context)).trim();
    }

    /**
     * Builds a prompt containing the task, quest, archetype, and user reflection.
     */
    public String UserPrompt(ReflectionContext context) {
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
    public ReflectionContext ContextFor(Task task, String questName, String reflection) {
        String resolvedQuestName = questName;
        String archetypeName = "";
        String overview = "";
        String strengths = "";
        String weaknesses = "";

        QuestService QuestService = new QuestService();
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
                            overview = matched.getSmallDescription();
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
    public void Save(WeeklyTask weeklyTask, String feedback) throws SQLException {
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
    public String Find(WeeklyTask weeklyTask) throws SQLException {
        if (weeklyTask == null) {
            return "";
        }
        return ReflectionFeedbackDAO.Find(
                weeklyTask.getEmail(),
                weeklyTask.getTaskId(),
                weeklyTask.getWeekStarted());
    }

    Archetype MatchArchetype(String name) {
        if (ApplicationManager.isEmpty(name)) {
            return null;
        }

        String normalised = name.trim();
        if (normalised.regionMatches(true, 0, "The ", 0, 4)) {
            normalised = normalised.substring(4).trim();
        }

        for (Archetype archetype : Archetype.values()) {
            if (archetype.getName().equalsIgnoreCase(normalised)
                    || archetype.name().equalsIgnoreCase(normalised)) {
                return archetype;
            }
        }
        return null;
    }

    private String Fallback(String value, String fallback) {
        return ApplicationManager.isEmpty(value) ? fallback : value;
    }

    private final String QUEST_FEEDBACK_KEY ="QUEST";

    public void SaveForQuest(String email, int labourId, String feedback) throws SQLException {
        if (ApplicationManager.isEmpty(email) || ApplicationManager.isEmpty(feedback)) {
            return;
        }
        ReflectionFeedbackDAO.Save(email, labourId, QUEST_FEEDBACK_KEY, feedback.trim());
    }

    public String FindForQuest(String email, int labourId) throws SQLException {
        if (ApplicationManager.isEmpty(email)) {
            return "";
        }
        return ReflectionFeedbackDAO.Find(email, labourId, QUEST_FEEDBACK_KEY);
    }
}

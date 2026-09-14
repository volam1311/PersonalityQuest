package com.example.personalityquest.Model;

/**
 * The task, quest, archetype, and user writing sent to the reflection feedback model.
 */
public record ReflectionContext(
        String taskName,
        String taskDescription,
        String questName,
        String archetypeName,
        String archetypeOverview,
        String archetypeStrengths,
        String archetypeWeaknesses,
        String reflection
) {
    public ReflectionContext {
        taskName = Safe(taskName);
        taskDescription = Safe(taskDescription);
        questName = Safe(questName);
        archetypeName = Safe(archetypeName);
        archetypeOverview = Safe(archetypeOverview);
        archetypeStrengths = Safe(archetypeStrengths);
        archetypeWeaknesses = Safe(archetypeWeaknesses);
        reflection = Safe(reflection);
    }

    private static String Safe(String value) {
        return value == null ? "" : value.trim();
    }
}

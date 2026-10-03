package com.example.personalityquest.Model.quest;

/**
 * A reflection prompt tied to a labour. The "How would you react" question links a
 * prompt that is either about the deficit or excess side of the labour, matching the
 * choice the user makes.
 */

public record ReflectionPrompt(int reflectionId, int labourId, String reactionType, String prompt) {

    public ReflectionPrompt {
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Reflection prompt text is empty");
        }
        if (labourId <= 0) {
            throw new IllegalArgumentException("Labour ID is invalid");
        }
    }

}

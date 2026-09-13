package com.example.personalityquest.Model;

/**
 * Jungian brand archetypes used by the personality quiz and quest assignment.
 */
public enum Archetype {

    INNOCENT(
            "Innocent",
            "You approach life with optimism, simplicity, and trust in others.",
            "Positive, honest, hopeful, and able to make others feel safe.",
            "Can be overly trusting, avoid difficult realities, or appear naive."
    ),

    EVERYMAN(
            "Everyman",
            "You value belonging, fairness, and building genuine connections with others.",
            "Approachable, dependable, cooperative, and good at bringing people together.",
            "Can suppress individuality, fear standing out, or follow the group too readily."
    ),

    HERO(
            "Hero",
            "You meet challenges directly and aim to prove yourself through courageous action.",
            "Brave, determined, disciplined, and able to inspire others.",
            "Can become overly competitive, arrogant, or focused on winning at any cost."
    ),

    EXPLORER(
            "Explorer",
            "You seek freedom, discovery, and new experiences beyond familiar boundaries.",
            "Independent, adventurous, curious, and willing to try new paths.",
            "Can become restless, unreliable, or dissatisfied with stability."
    ),

    CAREGIVER(
            "Caregiver",
            "You protect and support others, especially when they are facing hardship.",
            "Compassionate, generous, patient, and dependable.",
            "Can neglect personal needs, become overprotective, or feel taken for granted."
    ),

    MAGICIAN(
            "Magician",
            "You transform ideas and perspectives to make meaningful change possible.",
            "Visionary, imaginative, persuasive, and skilled at creating transformation.",
            "Can become manipulative, unrealistic, or overly secretive."
    ),

    SAGE(
            "Sage",
            "You seek truth, knowledge, and a deeper understanding of how things work.",
            "Intelligent, thoughtful, analytical, and able to provide useful insight.",
            "Can overthink decisions, appear emotionally distant, or delay action."
    ),

    OUTLAW(
            "Outlaw",
            "You challenge unfair systems and reject rules that prevent meaningful change.",
            "Bold, independent, disruptive, and courageous enough to question authority.",
            "Can become destructive, reckless, confrontational, or rebellious without purpose."
    ),

    LOVER(
            "Lover",
            "You value emotional connection, honesty, beauty, and meaningful relationships.",
            "Passionate, empathetic, loyal, and attentive to others.",
            "Can become dependent, jealous, overly emotional, or afraid of rejection."
    ),

    JESTER(
            "Jester",
            "You use humour, playfulness, and creativity to bring joy and challenge stale thinking.",
            "Funny, energetic, spontaneous, and able to lighten difficult situations.",
            "Can avoid serious responsibilities, become insensitive, or use humour defensively."
    ),

    RULER(
            "Ruler",
            "You create order, accept responsibility, and guide others towards shared goals.",
            "Confident, organised, responsible, and effective at leadership and delegation.",
            "Can become controlling, authoritarian, inflexible, or overly concerned with status."
    ),

    CREATOR(
            "Creator",
            "You turn original ideas into meaningful work and express yourself through what you build.",
            "Creative, innovative, expressive, and committed to producing high-quality work.",
            "Can become perfectionistic, impractical, self-critical, or afraid to finish."
    );

    private final String displayName;
    private final String overview;
    private final String strengths;
    private final String weaknesses;

    Archetype(
            String displayName,
            String overview,
            String strengths,
            String weaknesses
    ) {
        this.displayName = displayName;
        this.overview = overview;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
    }

    /**
     * Short display name without a leading "The".
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * General overview of the archetype.
     */
    public String getOverview() {
        return overview;
    }

    /**
     * Main strengths associated with the archetype.
     */
    public String getStrengths() {
        return strengths;
    }

    /**
     * Potential weaknesses associated with the archetype.
     */
    public String getWeaknesses() {
        return weaknesses;
    }

    /**
     * Maintained for compatibility with existing code.
     */
    public String getDescription() {
        return overview;
    }
}

package com.example.personalityquest.Model.quest;

/**
 * Hardcoded "Theme" and "Growth" copy shown on the Quest Viewer's "Why this Quest?" cover
 * page. Not persisted to the database - entries line up 1:1 with the labourId values seeded
 * by {@code QuestDAO} (which in turn match the twelve {@code Archetype} ids).
 */
public enum LabourInsight {
    CERYNEIAN_HIND(1,
            "Faith — a grounded trust in goodness and hope, held with open eyes rather than illusions.",
            "Working through this labour's reflections and challenges practices trusting people and outcomes "
                    + "without flinching into cynicism or sliding into naivety."),
    AUGEAN_STABLES(2,
            "Solidarity — belonging as an equal, connecting with others on level footing rather than above or below them.",
            "These reflections and challenges build the instinct to meet others as equals - bridging in without "
                    + "losing your own voice or standing apart as superior."),
    NEMEAN_LION(3,
            "Courage — facing fear and acting well in spite of it, meeting danger with judgment rather than being ruled by it.",
            "Facing this labour's challenges trains you to act through fear with judgment, rather than freezing "
                    + "in cowardice or charging in recklessly."),
    ERYMANTHIAN_BOAR(4,
            "Compassion — tending to others' needs with warmth, and knowing where your care ends and their own strength begins.",
            "This labour's reflections help you find the line between caring for others and caring for yourself, "
                    + "so support doesn't tip into smothering or neglect."),
    LERNAEAN_HYDRA(5,
            "Vision — seeing what could be and working with the deeper patterns of things to change them for the better.",
            "Working through these challenges sharpens your ability to turn vision into honest change, without "
                    + "drifting into rigid habits or manipulative shortcuts."),
    HESPERIDEAN_APPLES(6,
            "Wisdom — seeking truth for its own sake and holding it with an open mind.",
            "These reflections sharpen your ability to question carefully and act on what you learn, instead of "
                    + "believing too easily or retreating into pure thought."),
    HIPPOLYTAS_GIRDLE(7,
            "Passion — loving deeply and appreciating beauty, pleasure, and connection.",
            "This labour's challenges help you stay open to connection and beauty, without closing off from "
                    + "feeling or chasing desire without restraint."),
    STYMPHALIAN_BIRDS(8,
            "Witty — bringing lightness, humour, and play; lifting a moment and helping others enjoy being alive.",
            "Reflecting on this labour builds the skill of bringing lightness to hard moments, without going "
                    + "flat or getting your laughs at someone else's expense."),
    CATTLE_OF_GERYON(9,
            "Curiosity — the pull to seek, learn, and discover beyond the familiar.",
            "These challenges build the discipline to keep discovering without drifting into restlessness or "
                    + "settling for comfort."),
    CRETAN_BULL(10,
            "Craftiness — making something new and real, and shaping it with the skill and discipline to see it through.",
            "This labour's reflections build the follow-through to turn ideas into finished work, rather than "
                    + "leaving things unmade or chasing visions that never land."),
    MARES_OF_DIOMEDES(11,
            "Order — bringing structure and stability, and using authority in service of those you're responsible for.",
            "Working through these challenges builds the steady authority to bring order without tipping into "
                    + "control for its own sake."),
    CERBERUS(12,
            "Liberation — breaking what's unjust to free yourself and others, defying corrupt power for something better.",
            "This labour's reflections sharpen your instinct to push back against what's unjust, without "
                    + "sliding into quiet submission or vengeance for its own sake.");

    private final int labourId;
    private final String theme;
    private final String growth;

    LabourInsight(int labourId, String theme, String growth) {
        this.labourId = labourId;
        this.theme = theme;
        this.growth = growth;
    }

    /** Returns the labourId this insight belongs to
     * @return the labourId
     */
    public int getLabourId() {
        return labourId;
    }

    /** Returns the quest's theme, based on its archetype's balanced value
     * @return the theme text
     */
    public String getTheme() {
        return theme;
    }

    /** Returns the personal growth gained from completing this labour's reflections and challenges
     * @return the growth text
     */
    public String getGrowth() {
        return growth;
    }

    /** Looks up the insight for a labourId
     * @param labourId the labourId to look up
     * @return the matching insight, or null if none is defined
     */
    public static LabourInsight ForLabourId(int labourId) {
        for (LabourInsight insight : values()) {
            if (insight.labourId == labourId) {
                return insight;
            }
        }
        return null;
    }
}

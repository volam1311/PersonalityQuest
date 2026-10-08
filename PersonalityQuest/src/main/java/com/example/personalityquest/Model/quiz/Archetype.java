package com.example.personalityquest.Model.quiz;

/**
 * Jungian brand archetypes used by the personality quiz and quest assignment.
 */
public enum Archetype {

    // Realm: Ego
    INNOCENT(
            1,
            "Ego",
            "Innocent",
            "You approach life with optimism, simplicity, and trust in others.",
            "The Innocent believes the world can still be good, and acts as if that belief is worth protecting. "
                    + "That isn't a failure to see the dark — it's a choice to keep faith anyway: trusting without "
                    + "becoming naive, and staying open without hardening into cynicism when the world disappoints.",
            "Positive, honest, hopeful, and able to make others feel safe.",
            "Can be overly trusting, avoid difficult realities, or appear naive.",
            "Faith",
            "A grounded trust in goodness and hope for how things can turn out, held with open eyes rather than illusions.",
            "Naive",
            "Trusting without discernment; believing what feels safe and ignoring the warning signs.",
            "Cynic",
            "Trust withdrawn as armour; treating hope as foolish and expecting the worst.",
            "\uD83D\uDD4A\uFE0F"
    ),

    EVERYMAN(
            2,
            "Ego",
            "Everyman",
            "You value belonging, fairness, and building genuine connections with others.",
            "The Everyman finds dignity in ordinary life and connection in shared experience. Their gift is making "
                    + "people feel like they belong — not by standing apart as an elite, and not by dissolving into "
                    + "the crowd without a voice of their own, but by meeting others as equals.",
            "Approachable, dependable, cooperative, and good at bringing people together.",
            "Can suppress individuality, fear standing out, or follow the group too readily.",
            "Solidarity",
            "Belonging as an equal; connecting with others on level footing, neither above nor below them.",
            "Elitism",
            "Holding yourself apart as superior; breaking the common bond to stand above the crowd.",
            "Conformity",
            "Dissolving into the group; letting the crowd think for you and losing your own judgment.",
            "\uD83E\uDD1D"
    ),

    HERO(
            3,
            "Ego",
            "Hero",
            "You meet challenges directly and aim to prove yourself through courageous action.",
            "The Hero steps forward when something needs to be done, even when the outcome is uncertain. Courage, "
                    + "for them, isn't the absence of fear but the refusal to let it decide everything — holding the "
                    + "line between cowardice, which lets fear choose for you, and recklessness, which ignores it altogether.",
            "Brave, determined, disciplined, and able to inspire others.",
            "Can become overly competitive, arrogant, or focused on winning at any cost.",
            "Courage",
            "Facing fear and acting well in spite of it; meeting danger with judgment rather than being ruled by it.",
            "Cowardice",
            "Letting fear govern; shrinking from what should be faced.",
            "Recklessness",
            "Feeling too little fear; rushing into danger without heeding the risk.",
            "\u2694\uFE0F"
    ),

    // Realm: Soul
    CAREGIVER(
            4,
            "Soul",
            "Caregiver",
            "You protect and support others, especially when they are facing hardship.",
            "The Caregiver shows love through action — showing up, tending wounds, and carrying what others can't "
                    + "carry alone. Their challenge is finding the line between care and self-erasure: present "
                    + "without disappearing into others' needs, and supportive without smothering the independence it's meant to protect.",
            "Compassionate, generous, patient, and dependable.",
            "Can neglect personal needs, become overprotective, or feel taken for granted.",
            "Compassion",
            "Tending to others' needs with warmth, and knowing where your care ends and their own strength begins.",
            "Submissive",
            "Leaving others to fend for themselves; care withheld out of coldness or self-interest.",
            "Smothering",
            "Care that crowds and controls; help so constant it keeps others from standing on their own.",
            "\uD83E\uDEC2"
    ),

    MAGICIAN(
            5,
            "Soul",
            "Magician",
            "You transform ideas and perspectives to make meaningful change possible.",
            "The Magician sees the hidden mechanics behind things and uses that understanding to make real change "
                    + "happen. Vision is their gift, but it has to stay honest — grounded enough to resist rigidity "
                    + "when the old ways stop working, and restrained enough to avoid bending people and truth for their own ends.",
            "Visionary, imaginative, persuasive, and skilled at creating transformation.",
            "Can become manipulative, unrealistic, or overly secretive.",
            "Vision",
            "Seeing what could be and having the power to make it real; working with the deeper patterns of things to change them for the better.",
            "Rigidity",
            "Stuck and unable to change or be moved; clinging to how things are even as they stop working.",
            "Manipulation",
            "Using the power to shape reality for your own ends; bending people and truth rather than transforming them.",
            "\uD83D\uDD2E"
    ),

    SAGE(
            6,
            "Soul",
            "Sage",
            "You seek truth, knowledge, and a deeper understanding of how things work.",
            "The Sage is driven by the need to understand — to look past the surface of things until the truth "
                    + "becomes clear. Wisdom means questioning carefully rather than believing too easily, and "
                    + "staying curious without retreating so far into thought that understanding never becomes action.",
            "Intelligent, thoughtful, analytical, and able to provide useful insight.",
            "Can overthink decisions, appear emotionally distant, or delay action.",
            "Wisdom",
            "Seeking truth for its own sake and holding it with an open mind; understanding the world clearly and letting that understanding grow.",
            "Credulity",
            "Believing too easily; taking things on faith without questioning them or looking closer.",
            "Detachment",
            "Retreating into pure thought; knowing much but never bringing it back to bear on life.",
            "\uD83E\uDD89"
    ),

    // Realm: Self
    LOVER(
            7,
            "Self",
            "Lover",
            "You value emotional connection, honesty, beauty, and meaningful relationships.",
            "The Lover moves through the world led by feeling — drawn to beauty, intimacy, and the people they care "
                    + "about most. Passion held with integrity means staying open enough to resist going numb, "
                    + "but disciplined enough not to chase every desire at others' expense.",
            "Passionate, empathetic, loyal, and attentive to others.",
            "Can become dependent, jealous, overly emotional, or afraid of rejection.",
            "Passion",
            "Loving deeply and appreciating beauty, pleasure, and connection; giving yourself fully to what and whom you cherish.",
            "Insensibility",
            "Feeling too little; closed to desire, beauty, and closeness, holding others at arm's length.",
            "Self Indulgence",
            "Desire without restraint; chasing pleasure and intensity for their own sake, heedless of who it costs.",
            "\u2764\uFE0F"
    ),

    JESTER(
            8,
            "Self",
            "Jester",
            "You use humour, playfulness, and creativity to bring joy and challenge stale thinking.",
            "The Jester knows laughter can hold truths a serious face never could. Their lightness is a skill, not "
                    + "an escape — bringing joy and perspective to hard moments without going so flat that nothing "
                    + "lands, and never trading someone else's dignity for a laugh.",
            "Funny, energetic, spontaneous, and able to lighten difficult situations.",
            "Can avoid serious responsibilities, become insensitive, or use humour defensively.",
            "Witty",
            "Bringing lightness, humour, and play; lifting a moment and helping others enjoy being alive.",
            "Boring",
            "Bringing no lightness; too stiff or humourless to play, draining the joy from a moment.",
            "Belittling",
            "Humour turned into a weapon; getting the laugh by mocking and cutting others down.",
            "\uD83C\uDFAD"
    ),

    EXPLORER(
            9,
            "Self",
            "Explorer",
            "You seek freedom, discovery, and new experiences beyond familiar boundaries.",
            "The Explorer is happiest with a horizon still to cross, pulled forward by the belief that there's "
                    + "always something worth discovering. That curiosity has to be tempered — awake enough to "
                    + "resist settling for comfort, but grounded enough to actually arrive somewhere instead of forever chasing what's next.",
            "Independent, adventurous, curious, and willing to try new paths.",
            "Can become restless, unreliable, or dissatisfied with stability.",
            "Curiosity",
            "The pull to seek, learn, and discover; venturing beyond the familiar to see what's there.",
            "Sloth",
            "Staying put and letting the world come to you; curiosity gone cold, comfort chosen over discovery.",
            "Restlessness",
            "Never able to settle or arrive; forever chasing the next thing before the last one is finished.",
            "\uD83E\uDDED"
    ),

    // Realm: Mark
    CREATOR(
            10,
            "Mark",
            "Creator",
            "You turn original ideas into meaningful work and express yourself through what you build.",
            "The Creator turns what's only imagined into something real, shaped with care until it holds together. "
                    + "Their work lives between two failures: never starting at all, and starting everything but "
                    + "finishing nothing — ideas that stay dreams instead of becoming things.",
            "Creative, innovative, expressive, and committed to producing high-quality work.",
            "Can become perfectionistic, impractical, self-critical, or afraid to finish.",
            "Craftiness",
            "Making something new and real, and shaping it with the skill and discipline to see it through.",
            "Dullness",
            "Nothing new takes shape; unable or unwilling to make, create, or bring ideas to life.",
            "Delusion",
            "Lost in visions that never become anything; ideas untethered from reality and never made real.",
            "\uD83C\uDFA8"
    ),

    RULER(
            11,
            "Mark",
            "Ruler",
            "You create order, accept responsibility, and guide others towards shared goals.",
            "The Ruler steps up to bring order where things would otherwise fall apart, and carries that "
                    + "responsibility seriously. Real leadership stays accountable to the people it serves — firm "
                    + "enough to prevent chaos, but never so controlling that authority becomes its own reward.",
            "Confident, organised, responsible, and effective at leadership and delegation.",
            "Can become controlling, authoritarian, inflexible, or overly concerned with status.",
            "Order",
            "Bringing structure and stability, and using authority in service of those you're responsible for.",
            "Chaos",
            "Order abandoned; failing to take charge and leaving things to fall apart.",
            "Tyranny",
            "Control seized for its own sake; ruling over others to serve yourself rather than them.",
            "\uD83D\uDC51"
    ),

    OUTLAW(
            12,
            "Mark",
            "Outlaw",
            "You challenge unfair systems and reject rules that prevent meaningful change.",
            "The Outlaw refuses to accept that \"this is just how things are\" when the way things are isn't "
                    + "working. Their defiance is meant to free people, not just tear things down — pushing back "
                    + "hard enough to resist quiet submission, while staying focused enough that it doesn't collapse into vengeance for its own sake.",
            "Bold, independent, disruptive, and courageous enough to question authority.",
            "Can become destructive, reckless, confrontational, or rebellious without purpose.",
            "Liberation",
            "Breaking what's unjust to free yourself and others; defying corrupt power for the sake of something better.",
            "Submission",
            "Going along with what's wrong; obeying a broken order rather than standing against it.",
            "Vengeance",
            "Tearing down for its own sake; destruction with no cause to serve and nothing built in its place.",
            "\uD83D\uDD25"
    );

    private final int archetypeId;
    private final String realm;
    private final String name;
    private final String smallDescription;
    private final String longDescription;
    private final String strengths;
    private final String weaknesses;
    private final String valueMean;
    private final String valueMeanDefinition;
    private final String valueDeficit;
    private final String valueDeficitDefinition;
    private final String valueExcess;
    private final String valueExcessDefinition;
    private final String emoji;

    Archetype(
            int archetypeId,
            String realm,
            String name,
            String smallDescription,
            String longDescription,
            String strengths,
            String weaknesses,
            String valueMean,
            String valueMeanDefinition,
            String valueDeficit,
            String valueDeficitDefinition,
            String valueExcess,
            String valueExcessDefinition,
            String emoji
    ) {
        this.archetypeId = archetypeId;
        this.realm = realm;
        this.name = name;
        this.smallDescription = smallDescription;
        this.longDescription = longDescription;
        this.strengths = strengths;
        this.weaknesses = weaknesses;
        this.valueMean = valueMean;
        this.valueMeanDefinition = valueMeanDefinition;
        this.valueDeficit = valueDeficit;
        this.valueDeficitDefinition = valueDeficitDefinition;
        this.valueExcess = valueExcess;
        this.valueExcessDefinition = valueExcessDefinition;
        this.emoji = emoji;
    }

    // Public Getters and Setters

    /** Returns the archetype ID
     * @return the archetype ID
     */
    public int getArchetypeId() {return archetypeId;}

    /**
     * The realm this archetype belongs to: "Ego", "Soul", "Self", or "Mark".
     */
    public String getRealm() {return realm;}

    /** Returns the archetype name
     * @return the archetype name
     */
    public String getName() {
        return name;
    }
    /** Returns the short archetype description
     * @return the short description
     */
    public String getSmallDescription() {return smallDescription;}
    /** Returns the full archetype description
     * @return the full description
     */
    public String getLongDescription() {return longDescription;}

    /** Returns the archetype strengths
     * @return the strengths
     */
    public String getStrengths() {
        return strengths;
    }
    /** Returns the archetype weaknesses
     * @return the weaknesses
     */
    public String getWeaknesses() {
        return weaknesses;
    }

    /** Returns the archetype's balanced value
     * @return the balanced value
     */
    public String getValue() {return valueMean;}
    /** Returns the description of the balanced value
     * @return the balanced-value description
     */
    public String getValueDefinition() {return valueMeanDefinition;}

    /** Returns the archetype's deficient value
     * @return the deficient value
     */
    public String getValueDeficit() {return valueDeficit;}
    /** Returns the description of the deficient value
     * @return the deficient-value description
     */
    public String getValueDeficitDefinition() {return valueDeficitDefinition;}

    /** Returns the archetype's excessive value
     * @return the excessive value
     */
    public String getValueExcess() {return valueExcess;}
    /** Returns the description of the excessive value
     * @return the excessive-value description
     */
    public String getValueExcessDefinition() {return valueExcessDefinition;}

    /** Returns the emoji representing the archetype
     * @return the archetype emoji
     */
    public String getEmoji() {return emoji;}

}

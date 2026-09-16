package com.example.personalityquest.Model.quiz;

/**
 * Jungian brand archetypes used by the personality quiz and quest assignment.
 */
public enum Archetype {

    // Innocent: Modesty
    // Everyman: Friendly
    // Hero: Courage
    // Explorer: Ambition
    // Caregiver: Protective
    // Magician: Wittiness
    // Sage: Truthful
    // Outlaw: Justice
    // Lover:

    INNOCENT(
            1,
            "Innocent",
            "You approach life with optimism, simplicity, and trust in others.",
            "Filler",
            "Positive, honest, hopeful, and able to make others feel safe.",
            "Can be overly trusting, avoid difficult realities, or appear naive.",
            "Faith",
            "A grounded trust in goodness and hope for how things can turn out, held with open eyes rather than illusions.",
            "Naive",
            "Trusting without discernment; believing what feels safe and ignoring the warning signs.",
            "Cynic",
            "Trust withdrawn as armour; treating hope as foolish and expecting the worst."
    ),

    EVERYMAN(
            2,
            "Everyman",
            "You value belonging, fairness, and building genuine connections with others.",
            "Filler",
            "Approachable, dependable, cooperative, and good at bringing people together.",
            "Can suppress individuality, fear standing out, or follow the group too readily.",
            "Solidarity",
            "Belonging as an equal; connecting with others on level footing, neither above nor below them.",
            "Elitism",
            "Holding yourself apart as superior; breaking the common bond to stand above the crowd.",
            "Conformity",
            "Dissolving into the group; letting the crowd think for you and losing your own judgment."
    ),

    HERO(
            3,
            "Hero",
            "You meet challenges directly and aim to prove yourself through courageous action.",
            "Filler",
            "Brave, determined, disciplined, and able to inspire others.",
            "Can become overly competitive, arrogant, or focused on winning at any cost.",
            "Courage",
            "Facing fear and acting well in spite of it; meeting danger with judgment rather than being ruled by it.",
            "Cowardice",
            "Letting fear govern; shrinking from what should be faced.",
            "Recklessness",
            "Feeling too little fear; rushing into danger without heeding the risk."
    ),

    CAREGIVER(
            4,
            "Caregiver",
            "You protect and support others, especially when they are facing hardship.",
            "Filler",
            "Compassionate, generous, patient, and dependable.",
            "Can neglect personal needs, become overprotective, or feel taken for granted.",
            "Compassion",
            "Tending to others' needs with warmth, and knowing where your care ends and their own strength begins.",
            "Submissive",
            "Leaving others to fend for themselves; care withheld out of coldness or self-interest.",
            "Smothering",
            "Care that crowds and controls; help so constant it keeps others from standing on their own."
    ),

    MAGICIAN(
            5,
            "Magician",
            "You transform ideas and perspectives to make meaningful change possible.",
            "Filler",
            "Visionary, imaginative, persuasive, and skilled at creating transformation.",
            "Can become manipulative, unrealistic, or overly secretive.",
            "Vision",
            "Seeing what could be and having the power to make it real; working with the deeper patterns of things to change them for the better.",
            "Rigidity",
            "Stuck and unable to change or be moved; clinging to how things are even as they stop working.",
            "Manipulation",
            "Using the power to shape reality for your own ends; bending people and truth rather than transforming them."
    ),

    SAGE(
            6,
            "Sage",
            "You seek truth, knowledge, and a deeper understanding of how things work.",
            "Filler",
            "Intelligent, thoughtful, analytical, and able to provide useful insight.",
            "Can overthink decisions, appear emotionally distant, or delay action.",
            "Wisdom",
            "Seeking truth for its own sake and holding it with an open mind; understanding the world clearly and letting that understanding grow.",
            "Credulity",
            "Believing too easily; taking things on faith without questioning them or looking closer.",
            "Detachment",
            "Retreating into pure thought; knowing much but never bringing it back to bear on life."
    ),

    LOVER(
            7,
            "Lover",
            "You value emotional connection, honesty, beauty, and meaningful relationships.",
            "Filler",
            "Passionate, empathetic, loyal, and attentive to others.",
            "Can become dependent, jealous, overly emotional, or afraid of rejection.",
            "Passion",
            "Loving deeply and appreciating beauty, pleasure, and connection; giving yourself fully to what and whom you cherish.",
            "Insensibility",
            "Feeling too little; closed to desire, beauty, and closeness, holding others at arm's length.",
            "Self Indulgence",
            "Desire without restraint; chasing pleasure and intensity for their own sake, heedless of who it costs."
    ),

    JESTER(
            8,
            "Jester",
            "You use humour, playfulness, and creativity to bring joy and challenge stale thinking.",
            "Filler",
            "Funny, energetic, spontaneous, and able to lighten difficult situations.",
            "Can avoid serious responsibilities, become insensitive, or use humour defensively.",
            "Witty",
            "Bringing lightness, humour, and play; lifting a moment and helping others enjoy being alive.",
            "Boring",
            "Bringing no lightness; too stiff or humourless to play, draining the joy from a moment.",
            "Belittling",
            "Humour turned into a weapon; getting the laugh by mocking and cutting others down."
    ),

    EXPLORER(
            9,
            "Explorer",
            "You seek freedom, discovery, and new experiences beyond familiar boundaries.",
            "Filler",
            "Independent, adventurous, curious, and willing to try new paths.",
            "Can become restless, unreliable, or dissatisfied with stability.",
            "Curiosity",
            "The pull to seek, learn, and discover; venturing beyond the familiar to see what's there.",
            "Sloth",
            "Staying put and letting the world come to you; curiosity gone cold, comfort chosen over discovery.",
            "Restlessness",
            "Never able to settle or arrive; forever chasing the next thing before the last one is finished."
    ),

    CREATOR(
            10,
            "Creator",
            "You turn original ideas into meaningful work and express yourself through what you build.",
            "Filler",
            "Creative, innovative, expressive, and committed to producing high-quality work.",
            "Can become perfectionistic, impractical, self-critical, or afraid to finish.",
            "Craftiness",
            "Making something new and real, and shaping it with the skill and discipline to see it through.",
            "Dullness",
            "Nothing new takes shape; unable or unwilling to make, create, or bring ideas to life.",
            "Delusion",
            "Lost in visions that never become anything; ideas untethered from reality and never made real."
    ),


    RULER(
            11,
            "Ruler",
            "You create order, accept responsibility, and guide others towards shared goals.",
            "Filler",
            "Confident, organised, responsible, and effective at leadership and delegation.",
            "Can become controlling, authoritarian, inflexible, or overly concerned with status.",
            "Order",
            "Bringing structure and stability, and using authority in service of those you're responsible for.",
            "Chaos",
            "Order abandoned; failing to take charge and leaving things to fall apart.",
            "Tyranny",
            "Control seized for its own sake; ruling over others to serve yourself rather than them."
    ),

    OUTLAW(
            12,
            "Outlaw",
            "You challenge unfair systems and reject rules that prevent meaningful change.",
            "Filler",
            "Bold, independent, disruptive, and courageous enough to question authority.",
            "Can become destructive, reckless, confrontational, or rebellious without purpose.",
            "Liberation",
            "Breaking what's unjust to free yourself and others; defying corrupt power for the sake of something better.",
            "Submission",
            "Going along with what's wrong; obeying a broken order rather than standing against it.",
            "Vengeance",
            "Tearing down for its own sake; destruction with no cause to serve and nothing built in its place."
    );



    private final int archetypeId;
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



    Archetype(
            int archetypeId,
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
            String valueExcessDefinition
    ) {
        this.archetypeId = archetypeId;
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
    }


    // Public Getters and Setters

    public int getArchetypeId() {return archetypeId;}

    public String getName() {
        return name;
    }
    public String getSmallDescription() {return smallDescription;}
    public String getLongDescription() {return longDescription;}

    public String getStrengths() {
        return strengths;
    }
    public String getWeaknesses() {
        return weaknesses;
    }


    public String getValue() {return valueMean;}
    public String getValueDefinition() {return valueMeanDefinition;}

    public String getValueDeficit() {return valueDeficit;}
    public String getValueDeficitDefinition() {return valueDeficitDefinition;}

    public String getValueExcess() {return valueExcess;}
    public String getValueExcessDefinition() {return valueExcessDefinition;}



}

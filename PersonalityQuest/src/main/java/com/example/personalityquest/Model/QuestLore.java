package com.example.personalityquest.Model;

import com.example.personalityquest.ApplicationManager;

import java.util.Locale;
import java.util.Map;

/**
 * Story, challenge, and reflection copy for a labour.
 * Used by the Quest page when the database only stores a quest name.
 */
public record QuestLore(
        String labourTitle,
        String story,
        String challenge,
        String reflectionPrompt,
        String traitFocus) {

    private static final QuestLore DEFAULT_LORE = new QuestLore(
            "Your Current Labour",
            "This labour is part of your questline. Complete this week's tasks and write an honest reflection to move the story forward.",
            "Show up for the assigned tasks this week. Small, consistent steps count more than one grand gesture.",
            "What did this labour ask of you, and what will you try differently next week?",
            "Personal growth"
    );

    private static final Map<String, QuestLore> BY_ARCHETYPE = Map.ofEntries(
            Map.entry("the innocent", new QuestLore(
                    "Capture the Ceryneian Hind",
                    "Hercules spent a year tracking Artemis's golden-horned hind without wounding it. The labour is patience: staying with something you want without forcing the ending.",
                    "Spend this week showing up for something you genuinely want without demanding a result.",
                    "What are you chasing that needs steady patience instead of one big grand gesture?",
                    "Patience"
            )),
            Map.entry("the sage", new QuestLore(
                    "Slay the Lernaean Hydra",
                    "Every head Hercules smashed grew two more until Iolaus cauterized the roots. The labour is finding the real problem instead of fighting symptoms.",
                    "Pick the assignment or problem that keeps multiplying. Stop, find the root, and solve that.",
                    "Where are you working hard on the wrong part of the problem?",
                    "Discernment"
            )),
            Map.entry("the explorer", new QuestLore(
                    "Steal the Cattle of Geryon",
                    "Hercules crossed unknown lands and an ocean in a golden cup to reach Geryon's cattle. The labour is leaving the circle you already know.",
                    "Go somewhere unfamiliar this week — a society, a part of the city, or a class outside your degree.",
                    "What's stopping you from stepping outside your usual circle?",
                    "Curiosity"
            )),
            Map.entry("the outlaw", new QuestLore(
                    "Steal the Mares of Diomedes",
                    "Hercules faced Diomedes and his man-eating mares instead of talking around the threat. The labour is confronting what is unfair.",
                    "Push back on something unfair this week — a dodgy clause, a freeloader, or a rule that does not make sense.",
                    "What do you complain about but never actually confront?",
                    "Courage"
            )),
            Map.entry("the magician", new QuestLore(
                    "Clean the Augean Stables",
                    "Hercules diverted two rivers to clear decades of filth in a day. The labour is building a system so the mess cannot rebuild.",
                    "Tackle a backlog by setting up a system that clears it, not just a one-off blitz.",
                    "What could you set up once so the mess stops rebuilding?",
                    "Systems thinking"
            )),
            Map.entry("the hero", new QuestLore(
                    "Capture the Cretan Bull",
                    "Hercules met the raging bull head-on and brought it back alive. The labour is facing the thing you have been dreading.",
                    "Face the thing you have been avoiding this week — the hard conversation, the exam, or the session you keep skipping.",
                    "What have you been avoiding because it feels too big?",
                    "Bravery"
            )),
            Map.entry("the lover", new QuestLore(
                    "Obtain the Girdle of Hippolyta",
                    "Hercules had to win Hippolyta's belt through relationship, not just force. The labour is honesty over pride.",
                    "Repair or deepen one relationship through honesty rather than avoidance.",
                    "Where would openness work better than pride?",
                    "Connection"
            )),
            Map.entry("the jester", new QuestLore(
                    "Slay the Stymphalian Birds",
                    "Hercules used Athena's clappers to flush the birds from their rut, then took the shot. The labour is breaking a stale pattern.",
                    "Do something playful and disruptive this week that shakes you out of a routine that is dragging you down.",
                    "What are you taking too seriously right now?",
                    "Play"
            )),
            Map.entry("the everyman", new QuestLore(
                    "Capture the Erymanthian Boar",
                    "Hercules trapped the boar with snow and what was already around him. The labour is using what you have.",
                    "Solve a problem using what you already have — no new app, no expensive fix.",
                    "What simple, unglamorous approach have you been overlooking?",
                    "Resourcefulness"
            )),
            Map.entry("the caregiver", new QuestLore(
                    "Slay the Nemean Lion",
                    "Hercules strangled the lion whose hide no weapon could pierce, then wore the hide. The labour is surviving something hard and making it useful.",
                    "Get through a tough stretch this week, then share what you learned with someone going through the same thing.",
                    "How can something hard you survived become useful to someone else?",
                    "Resilience"
            )),
            Map.entry("the ruler", new QuestLore(
                    "Steal the Golden Apples of the Hesperides",
                    "Hercules needed Atlas for the apples he could not reach alone. The labour is knowing what you must own and what you can hand over.",
                    "Delegate part of a group project or ask for help — but keep the piece only you can do.",
                    "When should you carry it, and when should you let someone else?",
                    "Leadership"
            )),
            Map.entry("the creator", new QuestLore(
                    "Capture Cerberus",
                    "Hercules brought Cerberus from the underworld without weapons. The labour is finishing the work and letting it be seen.",
                    "Finish and actually share something you have made this week.",
                    "What are you keeping hidden because you are scared of the response?",
                    "Creative courage"
            ))
    );

    /**
     * Chooses lore from a quest name or archetype name, with a generic fallback.
     */
    public static QuestLore forQuest(String questName, String archetypeName) {
        QuestLore fromQuest = match(questName);
        if (fromQuest != null) {
            return fromQuest;
        }

        QuestLore fromArchetype = match(archetypeName);
        if (fromArchetype != null) {
            return fromArchetype;
        }

        if (ApplicationManager.isEmpty(questName)) {
            return DEFAULT_LORE;
        }

        return new QuestLore(
                questName,
                DEFAULT_LORE.story(),
                DEFAULT_LORE.challenge(),
                DEFAULT_LORE.reflectionPrompt(),
                DEFAULT_LORE.traitFocus()
        );
    }

    private static QuestLore match(String value) {
        if (ApplicationManager.isEmpty(value)) {
            return null;
        }

        String haystack = value.toLowerCase(Locale.ROOT);
        for (Map.Entry<String, QuestLore> entry : BY_ARCHETYPE.entrySet()) {
            if (haystack.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        for (QuestLore lore : BY_ARCHETYPE.values()) {
            if (haystack.contains(lore.labourTitle().toLowerCase(Locale.ROOT))) {
                return lore;
            }
        }

        return null;
    }
}

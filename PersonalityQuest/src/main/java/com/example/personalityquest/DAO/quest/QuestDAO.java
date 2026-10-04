package com.example.personalityquest.DAO.quest;

import com.example.personalityquest.Model.quest.Quest;
import com.example.personalityquest.Model.quiz.Archetype;
import com.example.personalityquest.SQLite;
//import com.example.personalityquest.Model.quest.quiz.Archetype

import java.sql.*;
import java.util.Locale;

/** Stores and retrieves quest catalog records */
public class QuestDAO {
    /**
     * Gets an Array of Quests for the given archetypeId
     * @param archetypeId The archetypeId you want to get quests for
     * @return Array of quests matching archetypeId
     * @throws SQLException "Database Access Failure"
     */

    private static final String CREATE_QUESTS = """
            CREATE TABLE IF NOT EXISTS Quests (
            labourId INTEGER PRIMARY KEY,
            archetypeId INTEGER NOT NULL,
            name TEXT NOT NULL,
            narrative TEXT NOT NULL Default '',
            decisionQuestion TEXT NOT NULL Default '',
            resolution TEXT NOT NULL Default ''
            )
            """;

    private static final String[] LABOUR_NAMES ={
            "Labour of the Ceryneian Hind",      // 1  Innocent
            "Labour of the Augean Stables",      // 2  Everyman
            "Labour of the Nemean Lion",         // 3  Hero
            "Labour of the Erymanthian Boar",    // 4  Caregiver
            "Labour of the Lernaean Hydra",      // 5  Magician
            "Labour of the Hesperidean Apples",  // 6  Sage
            "Labour of Hippolyta's Girdle",      // 7  Lover
            "Labour of the Stymphalian Birds",   // 8  Jester
            "Labour of the Cattle of Geryon",    // 9  Explorer
            "Labour of the Cretan Bull",         // 10 Creator
            "Labour of the Mares of Diomedes",   // 11 Ruler
            "Labour of Cerberus"                 // 12 Outlaw
    };

    private static final String[] LABOUR_NARRATIVES = {
            // 1  Innocent — Ceryneian Hind (Apollodorus 2.5.3)
            "As a third labour he ordered him to bring the Cerynitian hind alive to Mycenae. Now the hind was at Oenoe; it had golden horns and was sacred to Artemis; so wishing neither to kill nor wound it, Hercules hunted it a whole year. But when, weary with the chase, the beast took refuge on the mountain called Artemisius, and thence passed to the river Ladon, Hercules shot it just as it was about to cross the stream, and catching it put it on his shoulders and hastened through Arcadia. But Artemis with Apollo met him, and would have wrested the hind from him, and rebuked him for attempting to kill her sacred animal. Howbeit, by pleading necessity and laying the blame on Eurystheus, he appeased the anger of the goddess and carried the beast alive to Mycenae.",

            // 2  Everyman — Augean Stables (Apollodorus 2.5.5)
            "For his fifth labour, Eurystheus ordered Hercules to clean the stables of Augeas in a single day — a task meant to humiliate him. Augeas, king of Elis, owned more cattle than any man alive, and their stables had not been cleaned in years. The filth was beyond counting, and the command carried an insult: let the great son of Zeus stoop to shovelling dung like the lowest hand.\n\nHercules could have refused the task as beneath him. Instead, he met Augeas as an equal and struck an honest bargain: clean the stables in one day, and the king would owe him a tenth of his herds. Augeas, certain it was impossible, agreed — and Hercules called the king's own son, Phyleus, to stand as witness to the deal.\n\nThen he set to work, not with his hands but with his wits. He breached the foundations at both ends of the yard, and turned the courses of two rivers, the Alpheus and the Peneus, straight through the stables. The waters surged in and swept the years of filth away before the day was out.\n\nBut when the labour was done, Augeas broke his word. He denied he had ever promised a reward, and demanded the matter go to arbitration.\n\nHere was the pivotal moment. The judges took their seats. Augeas was a king; Hercules a stranger passing through. And the one man who could speak the truth was Phyleus — the king's own son, caught between his father and what was fair.",

            // 3  Hero — Nemean Lion (Apollodorus 2.5.1)
            "First, Eurystheus ordered him to bring the skin of the Nemean lion; now that was an invulnerable beast begotten by Typhon. On his way to attack the lion he came to Cleonae and lodged at the house of a day-laborer, Molorchus; and when his host would have offered a victim in sacrifice, Hercules told him to wait for thirty days, and then, if he had returned safe from the hunt, to sacrifice to Saviour Zeus, but if he were dead, to sacrifice to him as to a hero. And having come to Nemea and tracked the lion, he first shot an arrow at him, but when he perceived that the beast was invulnerable, he heaved up his club and made after him. And when the lion took refuge in a cave with two mouths, Hercules built up the one entrance and came in upon the beast through the other, and putting his arm round its neck held it tight till he had choked it; so laying it on his shoulders he carried it to Cleonae.",

            // 4  Caregiver — Erymanthian Boar (Apollodorus 2.5.4, trimmed)
            "As a fourth labour he ordered him to bring the Erymanthian boar alive; now that animal ravaged Psophis, sallying from a mountain which they call Erymanthus. And when he had chased the boar with shouts from a certain thicket, he drove the exhausted animal into deep snow, trapped it, and brought it to Mycenae.",

            // 5  Magician — Lernaean Hydra (Apollodorus 2.5.2)
            "As a second labour he ordered him to kill the Lernaean hydra. That creature, bred in the swamp of Lerna, used to go forth into the plain and ravage both the cattle and the country. Now the hydra had a huge body, with nine heads, eight mortal, but the middle one immortal. So mounting a chariot driven by Iolaus, he came to Lerna, and having halted his horses, he discovered the hydra on a hill beside the springs of the Amymone, where was its den. By pelting it with fiery shafts he forced it to come out, and in the act of doing so he seized and held it fast. But the hydra wound itself about one of his feet and clung to him. Nor could he effect anything by smashing its heads with his club, for as fast as one head was smashed there grew up two. A huge crab also came to the help of the hydra by biting his foot. So he killed it, and in his turn called for help on Iolaus who, by setting fire to a piece of the neighboring wood and burning the roots of the heads with the brands, prevented them from sprouting. Having thus got the better of the sprouting heads, he chopped off the immortal head, and buried it, and put a heavy rock on it, beside the road that leads through Lerna to Elaeus. But the body of the hydra he slit up and dipped his arrows in the gall.",

            // 6  Sage — Hesperidean Apples (Apollodorus 2.5.11, trimmed)
            "Eurystheus ordered Hercules, as an eleventh labour, to fetch golden apples from the Hesperides. These apples were on Atlas among the Hyperboreans. They were presented by Earth to Zeus after his marriage with Hera, and guarded by an immortal dragon with a hundred heads, offspring of Typhon and Echidna, which spoke with many and divers sorts of voices. With it the Hesperides also were on guard, to wit, Aegle, Erythia, Hesperia, and Arethusa. Now Prometheus had told Hercules not to go himself after the apples but to send Atlas, first relieving him of the burden of the sphere; so when he was come to Atlas in the land of the Hyperboreans, he took the advice and relieved Atlas. But when Atlas had received three apples from the Hesperides, he came to Hercules, and not wishing to support the sphere he said that he would himself carry the apples to Eurystheus, and bade Hercules hold up the sky in his stead. Hercules promised to do so, but succeeded by craft in putting it on Atlas instead. For at the advice of Prometheus he begged Atlas to hold up the sky till he should put a pad on his head. When Atlas heard that, he laid the apples down on the ground and took the sphere from Hercules. And so Hercules picked up the apples and departed.",

            // 7  Lover — Hippolyta's Girdle (Apollodorus 2.5.9, trimmed)
            "The ninth labour he enjoined on Hercules was to bring the belt of Hippolyte. She was queen of the Amazons, who dwelt about the river Thermodon, a people great in war. Now Hippolyte had the belt of Ares in token of her superiority to all the rest. Hercules was sent to fetch this belt because Admete, daughter of Eurystheus, desired to get it. So taking with him a band of volunteer comrades in a single ship he set sail. Having put in at the harbor of Themiscyra, he received a visit from Hippolyte, who inquired why he was come, and promised to give him the belt. But Hera in the likeness of an Amazon went up and down the multitude saying that the strangers who had arrived were carrying off the queen. So the Amazons in arms charged on horseback down on the ship. But when Hercules saw them in arms, he suspected treachery, and killing Hippolyte stripped her of her belt. And after fighting the rest he sailed away. And having brought the belt to Mycenae he gave it to Eurystheus.",

            // 8  Jester — Stymphalian Birds (Apollodorus 2.5.6)
            "The sixth labour he enjoined on him was to chase away the Stymphalian birds. Now at the city of Stymphalus in Arcadia was the lake called Stymphalian, embosomed in a deep wood. To it countless birds had flocked for refuge, fearing to be preyed upon by the wolves. So when Hercules was at a loss how to drive the birds from the wood, Athena gave him brazen castanets, which she had received from Hephaestus. By clashing these on a certain mountain that overhung the lake, he scared the birds. They could not abide the sound, but fluttered up in a fright, and in that way Hercules shot them.",

            // 9  Explorer — Cattle of Geryon (Apollodorus 2.5.10)
            "As a tenth labour he was ordered to fetch the kine of Geryon from Erythia. Now Erythia was an island near the ocean; it is now called Gadira. This island was inhabited by Geryon, son of Chrysaor by Callirrhoe, daughter of Ocean. He had the body of three men grown together and joined in one at the waist, but parted in three from the flanks and thighs. He owned red kine, of which Eurytion was the herdsman and Orthus, the two-headed hound, begotten by Typhon on Echidna, was the watchdog. So journeying through Europe to fetch the kine of Geryon he destroyed many wild beasts and set foot in Libya, and proceeding to Tartessus he erected as tokens of his journey two pillars over against each other at the boundaries of Europe and Libya. But being heated by the Sun on his journey, he bent his bow at the god, who in admiration of his hardihood, gave him a golden goblet in which he crossed the ocean. And having reached Erythia he lodged on Mount Abas. However the dog, perceiving him, rushed at him; but he smote it with his club, and when the herdsman Eurytion came to the help of the dog, Hercules killed him also. But Menoetes, who was there pasturing the kine of Hades, reported to Geryon what had occurred, and he, coming up with Hercules beside the river Anthemus, as he was driving away the kine, joined battle with him and was shot dead. And Hercules, embarking the kine in the goblet and sailing across to Tartessus, gave back the goblet to the Sun.",

            // 10  Creator — Cretan Bull (Apollodorus 2.5.7)
            "The seventh labour he enjoined on him was to bring the Cretan bull. Acusilaus says that this was the bull that ferried across Europa for Zeus; but some say it was the bull that Poseidon sent up from the sea when Minos promised to sacrifice to Poseidon what should appear out of the sea. And they say that when he saw the beauty of the bull he sent it away to the herds and sacrificed another to Poseidon; at which the god was angry and made the bull savage. To attack this bull Hercules came to Crete, and when, in reply to his request for aid, Minos told him to fight and catch the bull for himself, he caught it and brought it to Eurystheus, and having shown it to him he let it afterwards go free. But the bull roamed to Sparta and all Arcadia, and traversing the Isthmus arrived at Marathon in Attica and harried the inhabitants.",

            // 11  Ruler — Mares of Diomedes (Apollodorus 2.5.8)
            "The eighth labour he enjoined on him was to bring the mares of Diomedes the Thracian to Mycenae. Now this Diomedes was a son of Ares and Cyrene, and he was king of the Bistones, a very warlike Thracian people, and he owned man-eating mares. So Hercules sailed with a band of volunteers, and having overpowered the grooms who were in charge of the mangers, he drove the mares to the sea. When the Bistones in arms came to the rescue, he committed the mares to the guardianship of Abderus, who was a son of Hermes, a native of Opus in Locris, and a minion of Hercules; but the mares killed him by dragging him after them. But Hercules fought against the Bistones, slew Diomedes and compelled the rest to flee. And he founded a city Abdera beside the grave of Abderus who had been done to death, and bringing the mares he gave them to Eurystheus. But Eurystheus let them go, and they came to Mount Olympus, as it is called, and there they were destroyed by the wild beasts.",

            // 12  Outlaw — Cerberus (Apollodorus 2.5.12)
            "A twelfth labour imposed on Hercules was to bring Cerberus from Hades. Now this Cerberus had three heads of dogs, the tail of a dragon, and on his back the heads of all sorts of snakes. And having come to Taenarum in Laconia, where is the mouth of the descent to Hades, he descended through it. But when the souls saw him, they fled, save Meleager and the Gorgon Medusa. And being come near to the gates of Hades he found Theseus and Pirithous. And Theseus, indeed, he took by the hand and raised up, but when he would have brought up Pirithous, the earth quaked and he let go. When Hercules asked Pluto for Cerberus, Pluto ordered him to take the animal provided he mastered him without the use of the weapons which he carried. Hercules found him at the gates of Acheron, and, cased in his cuirass and covered by the lion's skin, he flung his arms round the head of the brute, and though the dragon in its tail bit him, he never relaxed his grip and pressure till it yielded. So he carried it off and ascended through Troezen. And Hercules, after showing Cerberus to Eurystheus, carried him back to Hades.",
    };

    private static final String[] LABOUR_DECISION_QUESTIONS = {
            // 1  Innocent
            "The hind is caught and bound across your shoulders. As you turn for home, Apollo appears at Artemis's side, angered that you have laid hands on her sacred beast. You will not abandon the labour — but how do you carry it through?",
            // 2  Everyman
            "Augeas has broken his word, and the dispute goes before the judges. You did the work fairly and in the open — but now a king's word stands against yours, and only Phyleus can confirm the truth. How do you carry it through?",
            // 3  Hero
            "Your arrows are useless and your club cannot pierce its hide. The lion turns on you, and the fear rises. Your weapons have failed — how do you meet it?",
            "", // 4  Caregiver
            "", // 5  Magician
            "", // 6  Sage
            "", // 7  Lover
            "", // 8  Jester
            "", // 9  Explorer
            "", // 10 Creator
            "", // 11 Ruler
            ""  // 12 Outlaw
    };

    private static final String[] LABOUR_RESOLUTIONS = {
            // 1 Innocent
            "Hercules did not push past the gods, and he did not brace for a fight. He simply spoke the truth. He explained that he meant the hind no harm, that the labour had been forced on him by Eurystheus, and that he would never have raised a hand against something sacred by his own choice. He laid the matter bare and trusted Artemis to see his heart.\n\nAnd she did. The goddess's anger softened, and she let him pass. Hercules carried the hind alive to Mycenae — not by force, not by rank, and not by fear, but by holding honestly to good faith and trusting it would be understood.\n\nThis is the Innocent's lesson. Faith isn't blind, and it isn't armour against the world. It's a grounded trust — in others, and in how things can turn out — held with open eyes. Hercules faced down two gods with nothing but the truth and the belief that it would be enough. The naive over-promise and the cynic braces for a fight; the one who keeps honest faith simply says what is true, and finds that it carries further than either.",
            // 2  Everyman
            "Phyleus chose the truth. Called before the judges, the king's own son testified against his father, affirming that Augeas had indeed promised the reward. It was a brave and fair act — exactly the kind of level, honest bond the labour was built on.\n\nAnd it still wasn't enough. Augeas, enraged, didn't wait for the verdict. Before a single vote was cast, he banished both Hercules and Phyleus from Elis. Hercules lost his reward; Phyleus lost his home for siding with what was right. Worse was to come: Eurystheus later refused to count the labour at all, claiming Hercules had done it for payment rather than pure service.\n\nHere is the Everyman's hardest lesson. You can act fairly, build the honest bond, and trust the group to reach the right decision — and sometimes the decision still goes against you. Fairness is not a guarantee that you'll be treated fairly in return.\n\nBut the story doesn't end there. Years later, Hercules returned to Elis, set things right, and restored Phyleus to his rightful place — and on that ground founded the Olympic Games, a gathering of equals from every city that endures to this day. The fair bond was vindicated in the end; it just took longer than it should have.\n\nThe lesson is not to stop trusting or to stop building with others. It's to hold to fair dealing because it's right — not only because you expect fairness back — and to keep faith that what's just can endure, even when the room gets it wrong today.",
            // 3  Hero
            "Hercules did not win by being fearless, and he did not win by throwing himself at the beast. He won by facing the fear and thinking clearly inside it. When his weapons failed, he didn't freeze and he didn't charge — he changed the fight. He drove the lion into the dark of its own cave, where its speed and claws counted for less, and there he closed his arms around its throat and held on through everything it did to him, until it fell still.\n\nThen he returned to Molorchus on the thirtieth day — the very day the labourer was about to mourn him as dead — and the two sacrificed together to Zeus the Saviour, as they had planned. Hercules had measured the danger honestly from the start, and come back alive because of it.\n\nThis is the Hero's lesson. Courage is not the absence of fear, and it is not rushing in to prove you have none. It's feeling the fear fully, judging the danger clearly, and acting well anyway — choosing the ground where your strength can win, and then committing to it completely. The reckless and the fearful both lose to the lion. The one who faces it with open eyes brings home its skin — and wears it as armour ever after.",
            "", // 4  Caregiver
            "", // 5  Magician
            "", // 6  Sage
            "", // 7  Lover
            "", // 8  Jester
            "", // 9  Explorer
            "", // 10 Creator
            "", // 11 Ruler
            ""  // 12 Outlaw
    };


    private static void EnsureTables() throws SQLException {
        Connection connection = SQLite.getConnection();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_QUESTS);
        }
    }

    private static void InsertQuest(int labourId, int archetypeId, String name, String narrative,
                                    String decisionQuestion, String resolution) throws SQLException {
        EnsureTables();
        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT OR IGNORE INTO Quests (LabourId, archetypeId, name, narrative, decisionQuestion, resolution) VALUES (?, ?, ?, ?, ?, ?)")){
            statement.setInt(1, labourId);
            statement.setInt(2, archetypeId);
            statement.setString(3, name);
            statement.setString(4, narrative);
            statement.setString(5, decisionQuestion);
            statement.setString(6, resolution);
            statement.executeUpdate();
        }
    }

    private static void SeedCatalog() throws SQLException {
        for (Archetype archetype : Archetype.values()) {
            int archetypeId = archetype.getArchetypeId();
            InsertQuest(archetypeId, archetypeId, LABOUR_NAMES[archetypeId - 1], LABOUR_NARRATIVES[archetypeId - 1],
                    LABOUR_DECISION_QUESTIONS[archetypeId - 1], LABOUR_RESOLUTIONS[archetypeId - 1]);
        }
    }

    /** Recreates the quest catalog and inserts its default records
     * @throws SQLException if the catalog cannot be recreated
     */
    public static void ResetAndSeedCatalog() throws SQLException {
        Connection connection = SQLite.getConnection();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE IF EXISTS Quests");
        }
        EnsureTables();
        SeedCatalog();
    }


    /** Returns quests assigned to the supplied archetype
     * @param archetypeId the archetype ID to look up
     * @return the quests assigned to the archetype
     * @throws SQLException if the quests cannot be loaded
     */
    public static Quest[] GetQuestsForArchetypeId(int archetypeId) throws SQLException {

        int count = GetCountOfQuestsForArchetypeId(archetypeId);
        // no quests exist for this given ID
        if (count == 0){
            return null;
        }

        Connection connection = SQLite.getConnection();
        PreparedStatement getQuestsForArchetypeId = connection.prepareStatement(
                """
                    SELECT * FROM Quests
                    WHERE archetypeId = ?
                    """);
        getQuestsForArchetypeId.setInt(1, archetypeId);

        ResultSet rs = getQuestsForArchetypeId.executeQuery();
        Quest[] quests = new Quest[count];

        int index = 0;
        while (rs.next()){
            if (index == count) {
                System.out.println("Early break for quests as their where more quests then the count that was gotten");
                break;
            }

            quests[index] = new Quest(
                    rs.getInt("labourId"),
                    rs.getInt("archetypeId"),
                    rs.getString("name"),
                    rs.getString("narrative"),
                    rs.getString("decisionQuestion"),
                    rs.getString("resolution")
            );

            index++;
        }


        return quests;
    }

    /**
     * Retrives Quest Details for the given questId
     * @param labourId the labourId for the quest you want
     * @return The Quest is successful or null if labourId doesn't match a quest in the database
     * @throws SQLException Database Access Failure
     */
    public static Quest GetQuestForLabourId(int labourId) throws SQLException {
        Connection connection = SQLite.getConnection();
        PreparedStatement getQuestForLabourID = connection.prepareStatement(
                """
                    SELECT * FROM Quests
                    WHERE labourId = ?
                    """);
        getQuestForLabourID.setInt(1, labourId);

        ResultSet rs = getQuestForLabourID.executeQuery();

        Quest quest = null;
        if (rs.next()){
            quest = new Quest(
                    rs.getInt("labourId"),
                    rs.getInt("archetypeId"),
                    rs.getString("name"),
                    rs.getString("narrative"),
                    rs.getString("decisionQuestion"),
                    rs.getString("resolution")
            );
        }

        return quest;
    }

    /**
     * Looks up the display name of an archetype.
     * @param archetypeId The archetype to look up
     * @return The archetype name, or null if no row matches
     * @throws SQLException Database Access Failure
     */
    public static String GetArchetypeName(int archetypeId) throws SQLException {
        for (Archetype archetype : Archetype.values()) {
            if (archetype.getArchetypeId() == archetypeId){
                return archetype.getName();
            }
        }
        return null;
//        Connection connection = SQLite.getConnection();
//        try (PreparedStatement statement = connection.prepareStatement(
//                """
//                    SELECT name FROM Archetype
//                    WHERE archetypeId = ?
//                    """)) {
//            statement.setInt(1, archetypeId);
//            ResultSet rs = statement.executeQuery();
//            if (rs.next()) {
//                return rs.getString("name");
//            }
//            return null;
//        }
    }

    /**
     * Looks up an archetype id from a display name such as "Explorer" or "The Innocent".
     * @param name The archetype name to match
     * @return The archetypeId, or null if none matches
     * @throws SQLException Database Access Failure
     */
    public static Integer GetArchetypeIdForName(String name) throws SQLException {
        //Connection connection = SQLite.getConnection();
        String needle = name.trim().toLowerCase(Locale.ROOT);
        if (needle.startsWith("the ")) {
            needle = needle.substring(4).trim();
        }

        for (Archetype archetype : Archetype.values()) {
            String storedName = archetype.getName().trim().toLowerCase(Locale.ROOT);
            if (storedName.equals(needle)) {
                return archetype.getArchetypeId();
            }
        }
        return null;

//        try (PreparedStatement statement = connection.prepareStatement(
//                """
//                    SELECT archetypeId, name FROM Archetype
//                    """)) {
//            ResultSet rs = statement.executeQuery();
//            Integer fallback = null;
//            while (rs.next()) {
//                String stored = rs.getString("name");
//                if (stored == null || stored.isBlank()) {
//                    continue;
//                }
//
//                String storedName = stored.trim().toLowerCase(Locale.ROOT);
//                if (storedName.startsWith("the ")) {
//                    storedName = storedName.substring(4).trim();
//                }
//
//                if (storedName.equals(needle)) {
//                    return rs.getInt("archetypeId");
//                }
//                if (fallback == null && (storedName.contains(needle) || needle.contains(storedName))) {
//                    fallback = rs.getInt("archetypeId");
//                }
//            }
//            return fallback;
//        }
    }


    /**
     * Looks up the stored description of an archetype.
     * @param archetypeId The archetype to look up
     * @return The description, or null if no row matches
     * @throws SQLException Database Access Failure
     */
    public static String GetArchetypeDescription(int archetypeId) throws SQLException {
        for (Archetype archetype : Archetype.values()) {
            if (archetype.getArchetypeId() == archetypeId){
                return archetype.getSmallDescription();
            }
        }
        return null;
//        Connection connection = SQLite.getConnection();
//        try (PreparedStatement statement = connection.prepareStatement(
//                """
//                    SELECT smallDescription FROM Archetype
//                    WHERE archetypeId = ?
//                    """)) {
//            statement.setInt(1, archetypeId);
//            ResultSet rs = statement.executeQuery();
//            if (rs.next()) {
//                return rs.getString("smallDescription");
//            }
//            return null;
//        }
    }

    /**
     * The amount of quests that match the archetypeId
     * @param archetypeId The archetypeId that matches quests you want to retrieve the counts for
     * @return the amount of quests in the database for the archetypeId
     * @throws SQLException Database Access Failure
     */
    private static int GetCountOfQuestsForArchetypeId(int archetypeId) throws SQLException {
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(
                """
                    SELECT COUNT(*) FROM Quests
                    WHERE archetypeId = ?
                    """);
        statement.setInt(1, archetypeId);
        ResultSet rs = statement.executeQuery();

        int count = 0;
        if (rs.next()){
            count = rs.getInt(1);
        }

        return count;
    }
}

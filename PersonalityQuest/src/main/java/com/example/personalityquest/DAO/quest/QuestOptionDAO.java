package com.example.personalityquest.DAO.quest;

import com.example.personalityquest.Model.quest.QuestOption;
import com.example.personalityquest.SQLite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuestOptionDAO {
    private static final String CREATE_QUEST_OPTIONS = """
            CREATE TABLE IF NOT EXISTS QuestOptions (
            questOptionId INTEGER PRIMARY KEY,
            labourId INTEGER NOT NULL,
            archetypeId INTEGER NOT NULL,
            reactionType TEXT NOT NULL,
            text TEXT NOT NULL
            )
            """;

    private QuestOptionDAO() {}

    public static void EnsureTables() throws SQLException {
        Connection connection = SQLite.getConnection();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_QUEST_OPTIONS);
        }
    }

    public static boolean HasCatalog() throws SQLException {
        EnsureTables();
        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM QuestOptions")){
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        }
    }

    public static void InsertQuestOption(int questOptionId, int labourId, int archetypeId,
                                         QuestOption.ReactionType reactionType, String text) throws SQLException {
        EnsureTables();
        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT OR IGNORE INTO QuestOptions (questOptionId, labourId, archetypeId, reactionType, text)
                    VALUES (?, ?, ?, ?, ?)
                    """)) {
            statement.setInt(1, questOptionId);
            statement.setInt(2, labourId);
            statement.setInt(3, archetypeId);
            statement.setString(4, reactionType.name());
            statement.setString(5, text);
            statement.executeUpdate();
        }
    }

    public static List<QuestOption> GetQuestOptionsForLabourId(int labourId) throws SQLException {
        EnsureTables();
        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                    """
                        SELECT * FROM QuestOptions
                        WHERE labourId = ?
                        ORDER BY questOptionId
                        """)){
            statement.setInt(1, labourId);
            ResultSet resultSet = statement.executeQuery();
            List<QuestOption> options = new ArrayList<>();
            while (resultSet.next()) {
                options.add(new QuestOption(
                        resultSet.getInt("questOptionId"),
                        resultSet.getInt("labourId"),
                        resultSet.getInt("archetypeId"),
                        QuestOption.ReactionType.valueOf(resultSet.getString("reactionType")),
                        resultSet.getString("text")
                ));
            }
            return options;
        }
    }

    public static void SeedCatalog() throws SQLException {


        // Hero Questions
        InsertQuestOption(1, 3, 3, QuestOption.ReactionType.EXCESS,
                "Charge it head-on — Throw the weapons aside and rush straight at the beast in the open, trusting raw strength and fury to overpower it before it can overpower you. No time to think; attack now, hard, and don't stop.");
        InsertQuestOption(2, 3, 3, QuestOption.ReactionType.EXCESS,
                "Force the fight now — Press the attack immediately where you stand, trading blows in the open rather than losing momentum — hesitation feels like weakness, and a hero fights the enemy in front of him.");
        InsertQuestOption(3, 3, 3, QuestOption.ReactionType.DEFICIT,
                "Hold at a distance — Keep your weapons up and your ground, looking for the beast to tire or expose a weak point first — there's no sense closing with something you can't wound until you're certain of an opening.");

        // Everyman Questions
        InsertQuestOption(10, 2, 2, QuestOption.ReactionType.EXCESS,
                "Stand on your name — State plainly that you are the son of Zeus and that your word alone should outweigh any king's; the judges need no witness when a hero of your standing gives his account.");
        InsertQuestOption(11, 2, 2, QuestOption.ReactionType.EXCESS,
                "Press your own case hard — Argue the facts forcefully and let your reputation carry the day, speaking over the locals rather than leaning on Phyleus; this is your victory to win, and you'd rather not owe it to anyone else.");
        InsertQuestOption(12, 2, 2, QuestOption.ReactionType.DEFICIT,
                "Defer to the room — Sense that the crowd will side with their king, and hold back from pushing Phyleus to speak; better to let the judgment fall where it may than turn the whole of Elis against you.");


        // Innocent Questions
        InsertQuestOption(13, 1, 1, QuestOption.ReactionType.EXCESS,
                "Vow to Apollo — In return for letting you complete the labour, offer to perform a penance for him. You've done the hind no harm, so surely a promise of future service will set things right.");
        InsertQuestOption(14, 1, 1, QuestOption.ReactionType.EXCESS,
                "Assert dominance — Stand tall and remind Apollo that you are the son of Zeus, and that he answers to your father. Insist that no one, god or mortal, will stand against his will.");
        InsertQuestOption(15, 1, 1, QuestOption.ReactionType.DEFICIT,
                "Brace for the worst — Expect them to deny you. Ready your grip and prepare to challenge Artemis to an archery competition to prove your right to finish the labour — this hind is sacred to them, and they won't let it go so easily.");



        // Outlaw questions
        InsertQuestOption(45, 12, 12, QuestOption.ReactionType.DEFICIT,
                " Option 1 Deficit");
        InsertQuestOption(46, 12, 12, QuestOption.ReactionType.EXCESS,
                " Option 2 Excess");
        InsertQuestOption(47, 12, 12, QuestOption.ReactionType.DEFICIT,
                " Option 1 Deficit");
        InsertQuestOption(48, 12, 12, QuestOption.ReactionType.EXCESS,
                " Option 2 Excess");
    }

    public static void ResetAndSeedCatalog() throws SQLException {
        Connection connection = SQLite.getConnection();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE IF EXISTS QuestOptions");
        }
        EnsureTables();
        SeedCatalog();
    }
}

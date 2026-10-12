package com.example.personalityquest.DAO.quest;

import com.example.personalityquest.DAO.ParentDAO;
import com.example.personalityquest.Model.quiz.Archetype;
import com.example.personalityquest.Model.quiz.QuizResult;
import com.example.personalityquest.SQLite;

import java.sql.*;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class QuizResultDAO extends ParentDAO {
    private static final String CREATE_QUIZ_RESULTS = """
            CREATE TABLE IF NOT EXISTS QuizResults(
            accountEmail TEXT PRIMARY KEY,
            winnerArchetypeId INTEGER NOT NULL,
            scores TEXT NOT NULL
            )
            """;

    public QuizResultDAO(){
        super();
    }

    public QuizResultDAO(Connection connection){
        super(connection);
    }
    public void EnsureTables() throws SQLException {
        try (Statement statement = connection.createStatement()){
            statement.execute(CREATE_QUIZ_RESULTS);
        }
    }

    /**
     * Save or overwrites the logged in accounts quiz results
     * @param email The account the results belong to
     * @param result The scored result to persist
     * @throws SQLException Database Access Failure
     */

    public void SaveResult(String email, QuizResult result) throws SQLException {
        EnsureTables();
        try (PreparedStatement statement = connection.prepareStatement(
                "INSERT OR REPLACE INTO QuizResults (accountEmail, winnerArchetypeId, scores) VALUES (?, ?, ?)")){
            statement.setString(1, email);
            statement.setInt(2, result.archetype().getArchetypeId());
            statement.setString(3, SerializeScores(result));
            statement.executeUpdate();
        }
    }

    /**
     * Loads the signed in accounts stored quiz result.
     * @param email The account to look up
     * @return The stored QuizResult, or null if this account has not completed the quiz
     * @throws SQLException Database Access Failure
     */

    public QuizResult LoadResult(String email) throws SQLException {
        EnsureTables();
        try (PreparedStatement statement = connection.prepareStatement("" +
                "SELECT * FROM QuizResults WHERE accountEmail = ?")){
            statement.setString(1, email);
            ResultSet rs = statement .executeQuery();
            if (!rs.next()) {
                return null;
            }

            Archetype winner = ArchetypeById(rs.getInt("winnerArchetypeId"));
            Map<Archetype, Integer> scores = DeserializeScores(rs.getString("scores"));
            return new QuizResult(winner, scores);
        }
    }

    /**
     *  Packs every archetypes score into an ordered, comma seperated array string,
     *  indexed by Archetype.values() order
     */

    public static String SerializeScores(QuizResult result){
        StringBuilder builder = new StringBuilder("[");
        Archetype[] archetypes = Archetype.values();
        for (int index = 0; index < archetypes.length; index++) {
            if (index > 0){
                builder.append(",");
            }
            Integer score = result.scores().get(archetypes[index]);
            builder.append(score == null ? 0 : score);
        }
        return builder.append("]").toString();
    }

    /**
     * Unpacks a serialized score string back into a per archetype map
     */

    private static Map<Archetype, Integer> DeserializeScores(String raw){
        Map<Archetype, Integer> scores = new EnumMap<>(Archetype.class);
        String trimmed = raw.replace("[", "").replace("]", "");
        String[] parts = trimmed.split(",");
        Archetype[] archetypes = Archetype.values();
        for (int index = 0; index < archetypes.length && index <parts.length; index++) {
            scores.put(archetypes[index], Integer.parseInt(parts[index].trim()));
        }
        return scores;
    }

    public static Archetype ArchetypeById(int archetypeId){
        for (Archetype archetype : Archetype.values()){
            if (archetype.getArchetypeId() == archetypeId){
                return archetype;
            }
        }
        throw new IllegalArgumentException("Archetype with id " + archetypeId + " not found");
    }


}

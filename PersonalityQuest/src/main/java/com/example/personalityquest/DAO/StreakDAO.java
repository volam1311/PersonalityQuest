package com.example.personalityquest.DAO;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.Model.StreakProgress;
import com.example.personalityquest.SQLite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

/**
 * Provides database operations for account streak progress.
 */
public class StreakDAO {

    private static final String ENSURE_PROGRESS_ROW = """
            INSERT OR IGNORE INTO UserProgress (accountEmail)
            SELECT email FROM Accounts WHERE email = ?
            """;

    private static final String GET_PROGRESS =
            "SELECT * FROM UserProgress WHERE accountEmail = ?";

    private static final String UPDATE_PROGRESS = """
            UPDATE UserProgress
            SET currentStreak = ?, bestStreak = ?, lastCompletionDate = ?
            WHERE accountEmail = ?
            """;

    private static final String GET_TOTAL_QUESTS_COMPLETED = """
            SELECT totalQuestsCompleted
            FROM UserProgress
            WHERE accountEmail = ?
            """;
    private static final String UPDATE_TOTAL_QUESTS_COMPLETED = """
            UPDATE UserProgress
            SET totalQuestsCompleted = ?
            WHERE accountEmail = ?
            """;

    private StreakDAO() {
    }

    /**
     * Creates a progress row when an account does not have one yet.
     * @param connection The database connection
     * @param email The account email
     * @throws SQLException If the progress row cannot be created
     */
    private static void EnsureProgressRow(
            Connection connection,
            String email) throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(ENSURE_PROGRESS_ROW)) {

            statement.setString(1, email);
            statement.executeUpdate();
        }
    }

    /**
     * Gets the saved streak progress for an account.
     * @param email The account email
     * @return The saved streak progress, or null if the account does not exist
     * @throws SQLException If the progress cannot be retrieved
     */
    public static StreakProgress GetProgress(String email)
        throws SQLException {
        if (ApplicationManager.isEmpty(email)) {
            return null;
        }

        Connection connection = SQLite.getConnection();
        EnsureProgressRow(connection, email);

        int currentStreak;
        int bestStreak;
        String savedDate;
        LocalDate lastCompletionDate = null;

        try (PreparedStatement statement = connection.prepareStatement(GET_PROGRESS)) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                currentStreak = resultSet.getInt("currentStreak");
                bestStreak = resultSet.getInt("bestStreak");
                savedDate = resultSet.getString("lastCompletionDate");

                if (savedDate != null && !savedDate.isBlank()) {
                    lastCompletionDate = LocalDate.parse(savedDate);
                }

                return new StreakProgress(currentStreak, bestStreak, lastCompletionDate);
            }
        }
    }

    /**
     * Saves the streak progress for an account.
     * @param email The account email
     * @param currentStreak The current streak value
     * @param bestStreak The highest streak value achieved
     * @param completionDate The latest completion date
     * @throws SQLException If the progress cannot be saved
     */
    public static void SaveProgress(String email, int currentStreak, int bestStreak, LocalDate completionDate)
            throws SQLException {
        if (ApplicationManager.isEmpty(email)) {
            return;
        }

        Connection connection = SQLite.getConnection();
        EnsureProgressRow(connection, email);

        try (PreparedStatement statement = connection.prepareStatement(UPDATE_PROGRESS)) {
            statement.setInt(1, currentStreak);
            statement.setInt(2, bestStreak);
            statement.setString(3, completionDate == null ? null : completionDate.toString());
            statement.setString(4, email);
            statement.executeUpdate();
        }
    }

    /**
     * Gets the total amount of quests completed
     * @param email The account email you want the total quests completed for
     * @return total quests for that account
     * @throws SQLException Database Access Failure
     */
    public static int GetTotalQuestsCompleted(String email) throws SQLException {
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(GET_TOTAL_QUESTS_COMPLETED);
        statement.setString(1, email);

        ResultSet rs = statement.executeQuery();

        if (rs.next()){
            return rs.getInt("totalQuestsCompleted");
        }
        return 0;
    }

    /**
     * Sets the total amount of quests completed for an email
     * @param email The account email you want the total quests completed for
     * @param totalQuestsCompleted The new total quests completed
     * @throws SQLException Database Update Failure
     */
    public static void SetTotalQuestsCompleted(String email, int totalQuestsCompleted) throws SQLException {
        Connection connection = SQLite.getConnection();
        PreparedStatement statement = connection.prepareStatement(UPDATE_TOTAL_QUESTS_COMPLETED);
        statement.setInt(1, totalQuestsCompleted);
        statement.setString(2, email);

        statement.executeUpdate();
    }
}

package com.example.personalityquest.DAO;

import com.example.personalityquest.SQLite;
import com.example.personalityquest.ApplicationManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class StreakDAO {

    private static final int NO_STREAK = 0;
    private static final int FIRST_STREAK = 1;

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

    private StreakDAO() {
    }

    public static int RecordCompletion(String email, LocalDate completionDate) throws SQLException {
        // Calculate and save the streak for a completion date
        if (ApplicationManager.isEmpty(email) || completionDate == null) {
            return NO_STREAK;
        }

        Connection connection = SQLite.getConnection();

        EnsureProgressRow(connection, email);

        int currentStreak;
        int bestStreak;
        LocalDate lastCompletionDate = null;

        try (PreparedStatement statement = connection.prepareStatement(GET_PROGRESS)) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return 0;
                }

                currentStreak = resultSet.getInt("currentStreak");
                bestStreak = resultSet.getInt("bestStreak");

                String savedDate = resultSet.getString("lastCompletionDate");

                if (!ApplicationManager.isEmpty(savedDate)) {
                    lastCompletionDate = LocalDate.parse(savedDate);
                }
            }

            if (completionDate.equals(lastCompletionDate)) {
                return currentStreak;
            }

        }


        int updatedStreak;

        if (lastCompletionDate != null
                && lastCompletionDate.plusDays(1).equals(completionDate)) {
            updatedStreak = currentStreak + 1;
        } else {
            updatedStreak = FIRST_STREAK;
        }

        int updatedBestStreak = Math.max(bestStreak, updatedStreak);

            try (PreparedStatement statement =
                     connection.prepareStatement(UPDATE_PROGRESS)) {

            statement.setInt(1, updatedStreak);
            statement.setInt(2, updatedBestStreak);
            statement.setString(3, completionDate.toString());
            statement.setString(4, email);

            statement.executeUpdate();
        }

        return updatedStreak;
    }


    public static int GetCurrentStreak(String email) throws SQLException {
        // Read the current streak for an account
        if (ApplicationManager.isEmpty(email)) {
            return NO_STREAK;
        }
        Connection connection = SQLite.getConnection();

        EnsureProgressRow(connection, email);

        int currentStreak;

        try (PreparedStatement statement = connection.prepareStatement(GET_PROGRESS)) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return NO_STREAK;
                }

                currentStreak = resultSet.getInt("currentStreak");
            }

            return currentStreak;
        }
    }

    // Create a progress row when an account does not have one yet
    private static void EnsureProgressRow(
            Connection connection,
            String email) throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(ENSURE_PROGRESS_ROW)) {

            statement.setString(1, email);
            statement.executeUpdate();
        }
    }
}

package com.example.personalityquest.Managers;

import com.example.personalityquest.SQLite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class StreakManager {

    private static final String ensureProgressRow = """
            INSERT OR IGNORE INTO UserProgress (accountEmail)
            SELECT email FROM Accounts WHERE email = ?
            """;

    private static final String getProgress =
            "SELECT * FROM UserProgress WHERE accountEmail = ?";

    private static final String updateProgress ="""
            UPDATE UserProgress
            SET currentStreak = ?, bestStreak = ?, lastCompletionDate = ?
            WHERE accountEmail = ?
            """;

    private StreakManager() {
    }

    public static int RecordCompletion(String email, LocalDate completionDate) throws SQLException {

//        Check for nulls
        if (SystemManager.isEmpty(email) || completionDate == null) {
            return 0;
        }

        Connection connection = SQLite.getConnection();

        EnsureProgressRow(connection, email);

        int currentStreak;
        int bestStreak;
        LocalDate lastCompletionDate = null;

        try (PreparedStatement statement = connection.prepareStatement(getProgress)) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return 0;
                }

                currentStreak = resultSet.getInt("currentStreak");
                bestStreak = resultSet.getInt("bestStreak");

                String savedDate = resultSet.getString("lastCompletionDate");

                if (!SystemManager.isEmpty(savedDate)) {
                    lastCompletionDate = LocalDate.parse(savedDate);
                }
            }

            if (completionDate.equals(lastCompletionDate)) {
                return currentStreak;
            }

        }


        int updatedStreak;

        if (lastCompletionDate != null && lastCompletionDate.plusDays(1).equals(completionDate)) {
            updatedStreak = currentStreak + 1;
        } else {
            updatedStreak = 1;
        }

        int updatedBestStreak = Math.max(bestStreak, updatedStreak);

        try (PreparedStatement statement =
                     connection.prepareStatement(updateProgress)) {

            statement.setInt(1, updatedStreak);
            statement.setInt(2, updatedBestStreak);
            statement.setString(3, completionDate.toString());
            statement.setString(4, email);

            statement.executeUpdate();
        }

        return updatedStreak;
    }


    public static int GetCurrentStreak(String email) throws SQLException {
//        Check for nulls
        if (SystemManager.isEmpty(email)) {
            return 0;
        }
        Connection connection = SQLite.getConnection();

        EnsureProgressRow(connection, email);

        int currentStreak;

        try (PreparedStatement statement = connection.prepareStatement(getProgress)) {
            statement.setString(1, email);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return 0;
                }

                currentStreak = resultSet.getInt("currentStreak");
            }

            return currentStreak;
        }
    }

    private static  void EnsureProgressRow(
            Connection connection,
            String email) throws SQLException {

        try (PreparedStatement statement =
                     connection.prepareStatement(ensureProgressRow)) {

            statement.setString(1, email);
            statement.executeUpdate();
        }
    }
}

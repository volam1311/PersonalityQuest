package com.example.personalityquest.DAO;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.Model.Achievement;
import com.example.personalityquest.Model.UserProfile;
import com.example.personalityquest.SQLite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * SQLite persistence for achievement definitions, unlocks, and profile progress totals.
 */
public class AchievementDAO {
    private static final String CREATE_ACHIEVEMENTS = """
            CREATE TABLE IF NOT EXISTS Achievements (
                achievementId INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                description TEXT NOT NULL,
                criteriaType TEXT NOT NULL,
                threshold REAL NOT NULL
            )
            """;

    private static final String CREATE_USER_ACHIEVEMENTS = """
            CREATE TABLE IF NOT EXISTS UserAchievements (
                accountEmail TEXT NOT NULL,
                achievementId INTEGER NOT NULL,
                unlockedAt TEXT NOT NULL,
                PRIMARY KEY (accountEmail, achievementId),
                FOREIGN KEY (accountEmail) REFERENCES Accounts(email),
                FOREIGN KEY (achievementId) REFERENCES Achievements(achievementId)
            )
            """;

    private AchievementDAO() {
    }

    /**
     * Creates the achievement tables when they do not already exist.
     * @throws SQLException Database Access Failure
     */
    public static void EnsureTables() throws SQLException {
        Connection connection = SQLite.getConnection();
        try (Statement statement = connection.createStatement()) {
            statement.execute(CREATE_ACHIEVEMENTS);
            statement.execute(CREATE_USER_ACHIEVEMENTS);
        }
    }

    /**
     * @return Whether the Achievements table currently has any rows
     * @throws SQLException Database Access Failure
     */
    public static boolean HasCatalog() throws SQLException {
        EnsureTables();
        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM Achievements")) {
            ResultSet rs = statement.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    /**
     * Inserts an achievement definition. Existing ids are left unchanged.
     */
    public static void InsertAchievement(
            int achievementId,
            String name,
            String description,
            String criteriaType,
            double threshold) throws SQLException {
        EnsureTables();
        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT OR IGNORE INTO Achievements
                        (achievementId, name, description, criteriaType, threshold)
                    VALUES (?, ?, ?, ?, ?)
                    """)) {
            statement.setInt(1, achievementId);
            statement.setString(2, name);
            statement.setString(3, description);
            statement.setString(4, criteriaType);
            statement.setDouble(5, threshold);
            statement.executeUpdate();
        }
    }

    /**
     * @return Every achievement definition, ordered by id
     * @throws SQLException Database Access Failure
     */
    public static List<Achievement> GetCatalog() throws SQLException {
        EnsureTables();
        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                """
                    SELECT achievementId, name, description, criteriaType, threshold
                    FROM Achievements
                    ORDER BY achievementId
                    """)) {
            ResultSet rs = statement.executeQuery();
            List<Achievement> achievements = new ArrayList<>();
            while (rs.next()) {
                achievements.add(new Achievement(
                        rs.getInt("achievementId"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getString("criteriaType"),
                        rs.getDouble("threshold"),
                        false
                ));
            }
            return achievements;
        }
    }

    /**
     * @param email The account to look up
     * @return Achievement ids this account has already unlocked
     * @throws SQLException Database Access Failure
     */
    public static Set<Integer> GetUnlockedIds(String email) throws SQLException {
        EnsureTables();
        Set<Integer> unlocked = new HashSet<>();
        if (ApplicationManager.isEmpty(email)) {
            return unlocked;
        }

        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                """
                    SELECT achievementId FROM UserAchievements
                    WHERE accountEmail = ?
                    """)) {
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                unlocked.add(rs.getInt("achievementId"));
            }
        }
        return unlocked;
    }

    /**
     * Stores an unlock for the given account. Repeated calls are ignored.
     */
    public static void Unlock(String email, int achievementId) throws SQLException {
        if (ApplicationManager.isEmpty(email)) {
            return;
        }

        EnsureTables();
        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT OR IGNORE INTO UserAchievements
                        (accountEmail, achievementId, unlockedAt)
                    VALUES (?, ?, ?)
                    """)) {
            statement.setString(1, email);
            statement.setInt(2, achievementId);
            statement.setString(3, LocalDate.now().toString());
            statement.executeUpdate();
        }
    }

    /**
     * Loads streak, quest, and weekly-task totals for an account from the database.
     * @param email The account email
     * @return Progress totals, or {@link UserProfile#empty()} when the email is blank
     * @throws SQLException Database Access Failure
     */
    public static UserProfile GetProgress(String email) throws SQLException {
        if (ApplicationManager.isEmpty(email)) {
            return UserProfile.empty();
        }

        String weekStart = LocalDate.now()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .toString();

        return new UserProfile(
                CountInt("""
                        SELECT currentStreak FROM UserProgress
                        WHERE accountEmail = ?
                        """, email, 0),
                CountInt("""
                        SELECT bestStreak FROM UserProgress
                        WHERE accountEmail = ?
                        """, email, 0),
                CountInt("""
                        SELECT COUNT(*) FROM UserQuests
                        WHERE accountEmail = ?
                        """, email, 0),
                CountInt("""
                        SELECT COUNT(*) FROM UserQuests
                        WHERE accountEmail = ?
                        AND (status = 'Complete' OR status = 'Completed')
                        """, email, 0),
                CountDouble("""
                        SELECT percentageComplete FROM UserQuests
                        WHERE accountEmail = ? AND status = 'Active'
                        """, email),
                CountInt("""
                        SELECT COUNT(*) FROM WeeklyTasks
                        WHERE accountEmail = ? AND status = 'Finished'
                        """, email, 0),
                CountInt("""
                        SELECT COUNT(*) FROM WeeklyTasks
                        WHERE accountEmail = ?
                        """, email, 0),
                CountIntWithWeek("""
                        SELECT COUNT(*) FROM WeeklyTasks
                        WHERE accountEmail = ? AND weekStart = ? AND status = 'Finished'
                        """, email, weekStart),
                CountIntWithWeek("""
                        SELECT COUNT(*) FROM WeeklyTasks
                        WHERE accountEmail = ? AND weekStart = ?
                        """, email, weekStart),
                CountInt("""
                        SELECT COUNT(*) FROM Accounts
                        WHERE email = ?
                        """, email, 0) == 1
        );
    }

    private static int CountInt(String sql, String email, int defaultValue) throws SQLException {
        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return defaultValue;
        }
    }

    private static double CountDouble(String sql, String email) throws SQLException {
        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0;
        }
    }

    private static int CountIntWithWeek(String sql, String email, String weekStart) throws SQLException {
        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            statement.setString(2, weekStart);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
}

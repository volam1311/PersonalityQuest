package com.example.personalityquest.DAO.profile;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.DAO.ParentDAO;
import com.example.personalityquest.Model.profile.Achievement;
import com.example.personalityquest.Model.profile.UserProfile;
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
public class AchievementDAO extends ParentDAO {
    private static final String CREATE_ACHIEVEMENTS = """
            CREATE TABLE IF NOT EXISTS Achievements (
                achievementId INTEGER PRIMARY KEY,
                name TEXT NOT NULL,
                description TEXT NOT NULL,
                criteriaType TEXT NOT NULL,
                threshold REAL NOT NULL,
                level INTEGER NOT NULL DEFAULT 1
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



    public AchievementDAO(){
        super();
    }

    public AchievementDAO(Connection connection) {
        super(connection);
    }
    /**
     * Creates the achievement tables when they do not already exist.
     * @throws SQLException Database Access Failure
     */
    public void EnsureTables() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute(CREATE_ACHIEVEMENTS);
            EnsureLevelColumn(connection);
            statement.execute(CREATE_USER_ACHIEVEMENTS);
        }
    }

    private void EnsureLevelColumn(Connection connection) throws SQLException {
        boolean hasLevelColumn = false;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("PRAGMA table_info(Achievements)")) {
            while (resultSet.next()) {
                if ("level".equalsIgnoreCase(resultSet.getString("name"))) {
                    hasLevelColumn = true;
                    break;
                }
            }
        }

        if (!hasLevelColumn) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate(
                        "ALTER TABLE Achievements ADD COLUMN level INTEGER NOT NULL DEFAULT 1"
                );
            }
        }
    }

    /**
     * @return Whether the Achievements table currently has any rows
     * @throws SQLException Database Access Failure
     */
    public boolean HasCatalog() throws SQLException {
        EnsureTables();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM Achievements")) {
            ResultSet rs = statement.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    /**
     * Inserts or updates an achievement definition.
     */
    public void InsertAchievement(
            int achievementId,
            String name,
            String description,
            String criteriaType,
            double threshold, int level) throws SQLException {
        EnsureTables();
        try (PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO Achievements
                        (achievementId, name, description, criteriaType, threshold, level)
                    VALUES (?, ?, ?, ?, ?, ?)
                    ON CONFLICT(achievementId) DO UPDATE SET
                        name = excluded.name,
                        description = excluded.description,
                        criteriaType = excluded.criteriaType,
                        threshold = excluded.threshold,
                        level = excluded.level
                    """)) {
            statement.setInt(1, achievementId);
            statement.setString(2, name);
            statement.setString(3, description);
            statement.setString(4, criteriaType);
            statement.setDouble(5, threshold);
            statement.setInt(6, level);
            statement.executeUpdate();
        }
    }

    /**
     * @return Every achievement definition, ordered by id
     * @throws SQLException Database Access Failure
     */
    public List<Achievement> GetCatalog() throws SQLException {
        EnsureTables();
        try (PreparedStatement statement = connection.prepareStatement(
                """
                    SELECT achievementId, name, description, criteriaType, threshold, level
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
                        rs.getInt("level"),
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
    public Set<Integer> GetUnlockedIds(String email) throws SQLException {
        EnsureTables();
        Set<Integer> unlocked = new HashSet<>();
        if (ApplicationManager.isEmpty(email)) {
            return unlocked;
        }

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
    public void Unlock(String email, int achievementId) throws SQLException {
        if (ApplicationManager.isEmpty(email)) {
            return;
        }

        EnsureTables();
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
     * @param email The account to look up
     * @param limit The maximum number of ids to return
     * @return Achievement ids this account has unlocked, most recently unlocked first
     * @throws SQLException Database Access Failure
     */
    public List<Integer> GetRecentlyUnlockedIds(String email, int limit) throws SQLException {
        EnsureTables();
        List<Integer> recentIds = new ArrayList<>();
        if (ApplicationManager.isEmpty(email)) {
            return recentIds;
        }

        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                """
                    SELECT achievementId FROM UserAchievements
                    WHERE accountEmail = ?
                    ORDER BY unlockedAt DESC, achievementId DESC
                    LIMIT ?
                    """)) {
            statement.setString(1, email);
            statement.setInt(2, limit);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                recentIds.add(rs.getInt("achievementId"));
            }
        }
        return recentIds;
    }

    /**
     * Loads streak, quest, and weekly-task totals for an account from the database.
     * @param email The account email
     * @return Progress totals, or {@link UserProfile#empty()} when the email is blank
     * @throws SQLException Database Access Failure
     */
    public UserProfile GetProgress(String email) throws SQLException {
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

    private int CountInt(String sql, String email, int defaultValue) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return defaultValue;
        }
    }

    private double CountDouble(String sql, String email) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
            return 0;
        }
    }

    private int CountIntWithWeek(String sql, String email, String weekStart) throws SQLException {
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

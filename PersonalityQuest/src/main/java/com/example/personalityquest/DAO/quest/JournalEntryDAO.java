package com.example.personalityquest.DAO.quest;

import com.example.personalityquest.DAO.ParentDAO;
import com.example.personalityquest.Model.quest.JournalEntry;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class JournalEntryDAO extends ParentDAO {

    private static final String CREATE_JOURNAL_TABLE = """
            CREATE TABLE IF NOT EXISTS JournalEntries(
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            accountEmail TEXT NOT NULL,
            labourId INTEGER,
            entryType TEXT NOT NULL,
            title TEXT NOT NULL DEFAULT '',
            body TEXT NOT NULL DEFAULT '',
            aiFeedback TEXT NOT NULL DEFAULT '',
            createdAt TEXT NOT NULL
            )""";



    public JournalEntryDAO(){
        super();
    }

    public JournalEntryDAO(Connection connection) {
        super(connection);
    }


    public void EnsureTables() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_JOURNAL_TABLE);
        }
    }

    public JournalEntry Insert(String email, Integer labourId, JournalEntry.EntryType entryType,
                                      String title, String body) throws SQLException {
        String createdAt = LocalDateTime.now().toString();
        try (PreparedStatement statement = connection.prepareStatement(
                """
                INSERT INTO JournalEntries (accountEmail, labourId, entryType, title, body, aiFeedback, createdAt)
                VALUES (?, ?, ?, ?, ?, '', ?)
                """, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, email);
            if (labourId == null) {
                statement.setNull(2, Types.INTEGER);
            } else {
                statement.setInt(2, labourId);
            }
            statement.setString(3, entryType.name());
            statement.setString(4, title);
            statement.setString(5, body);
            statement.setString(6, createdAt);
            statement.executeUpdate();

            int id = 0;
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) id = keys.getInt(1);
            }
            return new JournalEntry(id, email, labourId, entryType, title, body, "", createdAt);
        }
    }

    /**
     * Inserts a new journal entry for this labour/type, or updates the existing one if the
     * user has already added one - so editing and re-saving updates the same entry instead
     * of creating duplicates.
     */
    public JournalEntry Upsert(String email, Integer labourId, JournalEntry.EntryType entryType,
                                       String title, String body) throws SQLException {
        Integer existingId = FindId(connection, email, labourId, entryType);
        if (existingId == null) {
            return Insert(email, labourId, entryType, title, body);
        }

        try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE JournalEntries SET title = ?, body = ? WHERE id = ?")) {
            statement.setString(1, title);
            statement.setString(2, body);
            statement.setInt(3, existingId);
            statement.executeUpdate();
        }

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT createdAt FROM JournalEntries WHERE id = ?")) {
            statement.setInt(1, existingId);
            ResultSet rs = statement.executeQuery();
            String createdAt = rs.next() ? rs.getString("createdAt") : LocalDateTime.now().toString();
            return new JournalEntry(existingId, email, labourId, entryType, title, body, "", createdAt);
        }
    }

    private Integer FindId(Connection connection, String email, Integer labourId,
                                   JournalEntry.EntryType entryType) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT id FROM JournalEntries WHERE accountEmail = ? AND labourId = ? AND entryType = ?")) {
            statement.setString(1, email);
            if (labourId == null) {
                statement.setNull(2, Types.INTEGER);
            } else {
                statement.setInt(2, labourId);
            }
            statement.setString(3, entryType.name());
            ResultSet rs = statement.executeQuery();
            return rs.next() ? rs.getInt(1) : null;
        }
    }

    public List<JournalEntry> GetForEmailAndType(String email, JournalEntry.EntryType entryType) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT * FROM JournalEntries WHERE accountEmail = ? AND entryType = ? ORDER BY id DESC")) {
            statement.setString(1, email);
            statement.setString(2, entryType.name());
            return ReadAll(statement);
        }
    }

    public boolean ExistsForLabourAndType(String email, int labourId, JournalEntry.EntryType entryType) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM JournalEntries WHERE accountEmail = ? AND labourId = ? AND entryType = ?")) {
            statement.setString(1, email);
            statement.setInt(2, labourId);
            statement.setString(3, entryType.name());
            ResultSet rs = statement.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    private List<JournalEntry> ReadAll(PreparedStatement statement) throws SQLException {
        ResultSet rs = statement.executeQuery();
        List<JournalEntry> entries = new ArrayList<>();
        while (rs.next()) {
            int labourIdValue = rs.getInt("labourId");
            Integer labourId = rs.wasNull() ? null : labourIdValue;
            entries.add(new JournalEntry(
                    rs.getInt("id"),
                    rs.getString("accountEmail"),
                    labourId,
                    JournalEntry.EntryType.valueOf(rs.getString("entryType")),
                    rs.getString("title"),
                    rs.getString("body"),
                    rs.getString("aiFeedback"),
                    rs.getString("createdAt")
            ));
        }
        return entries;
    }
}
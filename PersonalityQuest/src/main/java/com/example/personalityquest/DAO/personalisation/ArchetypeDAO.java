package com.example.personalityquest.DAO.personalisation;

import com.example.personalityquest.Model.quiz.Archetype;
import com.example.personalityquest.Model.quiz.ArchetypeRecord;
import com.example.personalityquest.SQLite;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class ArchetypeDAO {
    private static final String CREATE_ARCHETYPES = """
            CREATE TABLE IF NOT EXISTS Archetypes (
            archetypeId INTEGER PRIMARY KEY,
            name TEXT NOT NULL,
            smallDescription TEXT,
            longDescription TEXT,
            strengths TEXT,
            weaknesses TEXT,
            valueMean TEXT NOT NULL,
            valueMeanDefinition TEXT NOT NULL,
            valueDeficit TEXT NOT NULL,
            valueDeficitDefinition TEXT NOT NULL,
            valueExcess TEXT NOT NULL,
            valueExcessDefinition TEXT NOT NULL,
            emoji TEXT NOT NULL
            )
            """;

    private ArchetypeDAO() {}

    public static void SeedCatalog() throws SQLException {
        for (Archetype archetype : Archetype.values()) {
            InsertArchetype( archetype.getArchetypeId(), archetype.getName(), archetype.getSmallDescription(),
                    archetype.getLongDescription(), archetype.getStrengths(), archetype.getWeaknesses(),
                    archetype.getValue(), archetype.getValueDefinition(), archetype.getValueDeficit(),
                    archetype.getValueDeficitDefinition(), archetype.getValueExcess(), archetype.getValueExcessDefinition(),
                    archetype.getEmoji());
        }
    }

    public static void EnsureTables() throws SQLException{
        Connection connection = SQLite.getConnection();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_ARCHETYPES);
        }
    }

    public static boolean HasCatalog() throws SQLException{
        EnsureTables();
        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM Archetypes")) {
            ResultSet rs = statement.executeQuery();
            return rs.next() && rs.getInt(1) >0;
        }
    }



    public static void InsertArchetype(
            int archetypeID,
            String name,
            String smallDescription,
            String longDescription,
            String strengths,
            String weaknesses,
            String valueMean,
            String valueMeanDefinition,
            String valueDeficit,
            String valueDeficitDefinition,
            String valueExcess,
            String valueExcessDefinition,
            String emoji) throws SQLException{
        EnsureTables();
        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                """
                        INSERT OR IGNORE INTO Archetypes
                        (archetypeId, name, smallDescription, longDescription, strengths, weaknesses, valueMean, valueMeanDefinition, valueDeficit, valueDeficitDefinition, valueExcess, valueExcessDefinition, emoji)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """)) {
            statement.setInt(1, archetypeID);
            statement.setString(2, name);
            statement.setString(3, smallDescription);
            statement.setString(4, longDescription);
            statement.setString(5, strengths);
            statement.setString(6, weaknesses);
            statement.setString(7, valueMean);
            statement.setString(8, valueMeanDefinition);
            statement.setString(9, valueDeficit);
            statement.setString(10, valueDeficitDefinition);
            statement.setString(11, valueExcess);
            statement.setString(12, valueExcessDefinition);
            statement.setString(13, emoji);
            statement.executeUpdate();
        }
    }


    public static List<ArchetypeRecord> GetCatalog() throws SQLException{
        EnsureTables();
        Connection connection = SQLite.getConnection();
        try(PreparedStatement statement = connection.prepareStatement(
                """
                    SELECT * FROM Archetypes ORDER BY archetypeID
                    """ )){
            ResultSet rs = statement.executeQuery();
            List<ArchetypeRecord> archetypes = new ArrayList<>();
            while (rs.next()) {
                archetypes.add(new ArchetypeRecord(
                        rs.getInt("archetypeID"),
                        rs.getString("name"),
                        rs.getString("smallDescription"),
                        rs.getString("longDescription"),
                        rs.getString("strengths"),
                        rs.getString("weaknesses"),
                        rs.getString("valueMean"),
                        rs.getString("valueMeanDefinition"),
                        rs.getString("valueDeficit"),
                        rs.getString("valueDeficitDefinition"),
                        rs.getString("valueExcess"),
                        rs.getString("valueExcessDefinition")
                ));
            }
            return archetypes;
        }
    }

    public static void ResetAndSeedCatalog() throws SQLException{
        Connection connection = SQLite.getConnection();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE IF EXISTS Archetypes");
        }
        EnsureTables();
        SeedCatalog();
    }



}

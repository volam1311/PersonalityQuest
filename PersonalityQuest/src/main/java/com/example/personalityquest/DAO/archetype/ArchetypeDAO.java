package com.example.personalityquest.DAO.archetype;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.Model.profile.Achievement;
import com.example.personalityquest.Model.profile.UserProfile;
import com.example.personalityquest.Model.quiz.Archetype;
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


public class ArchetypeDAO {
    private static final String CREATE_ARCHETYPES = """
            CREATE TABLE IF NOT EXISTS Archetype (
            archetypeId INTEGER PRIMARY KEY,
            name TEXT NOT NULL,
            smallDescription TEXT,
            longDescription TEXT,
            valueMean TEXT NOT NULL,
            valueMeanDefinition TEXT NOT NULL,
            valueDefecit TEXT NOT NULL,
            valueDefecitDefinition TEXT NOT NULL,
            valueExcess TEXT NOT NULL,
            valueExcessDefinition TEXT NOT NULL            
            )
            """;

    private ArchetypeDAO() {}

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
            String valueMean,
            String valueMeanDefinition,
            String valueDefecit,
            String valueDefecitDefenition,
            String valueExcess,
            String valueExcessDefinition) throws SQLException{
        EnsureTables();
        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                """
                        INSERT OR IGNORE INTO Archetypes
                        (archetypeId, name, smallDescription, longDescription, valueMean, valueMeanDefinition, valueDefecit, valueDefecitDefenition, valueExcess, valueExcessDefinition)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """)) {
            statement.setInt(1, archetypeID);
            statement.setString(2, name);
            statement.setString(3, smallDescription);
            statement.setString(4, longDescription);
            statement.setString(5, valueMean);
            statement.setString(6, valueMeanDefinition);
            statement.setString(7, valueDefecit);
            statement.setString(8, valueDefecitDefenition);
            statement.setString(9, valueExcess);
            statement.setString(10, valueExcessDefinition);
            statement.executeUpdate();
        }
    }

    public static List<Archetype> GetCatalog() throws SQLException{
        EnsureTables();
        Connection connection = SQLite.getConnection();
        try(PreparedStatement statement = connection.prepareStatement(
                """
                    SELECT * FROM Archetypes ORDER BY archetypeID
                    """ )){
            ResultSet rs = statement.executeQuery();
            List<Archetype> archetypes = new ArrayList<>();
            while (rs.next()) {
                archetypes.add(new Archetype(
                        rs.getInt("archetypeID"),
                        rs.getString("name"),
                        rs.getString("smallDescription"),
                        rs.getString("longDescription"),
                        rs.getString("meanValue"),
                        rs.getString("meanValueDefinition"),
                        rs.getString("defecitValue"),
                        rs.getString("defecitValueDefinition"),
                        rs.getString("excessValue"),
                        rs.getString("excessValueDefinition"),
                        false
                ));
            }
            return archetypes;
        }
    }


}

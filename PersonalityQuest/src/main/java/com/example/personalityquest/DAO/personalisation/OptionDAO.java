package com.example.personalityquest.DAO.personalisation;

import com.example.personalityquest.Model.quiz.Option;
import com.example.personalityquest.SQLite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OptionDAO {
    public static final String CREATE_OPTIONS =
            """
            CREATE TABLE IF NOT EXISTS Options (
            optionsId INTEGER PRIMARY KEY,
            option1Archetype INTEGER NOT NULL,
            option2Archetype INTEGER NOT NULL,
            option3Archetype INTEGER NOT NULL,
            option1 TEXT NOT NULL,
            option2 TEXT NOT NULL,
            option3 TEXT NOT NULL)
            """;

    private OptionDAO() {}


    public static void EnsureTables() throws SQLException {
        Connection conn = SQLite.getConnection();
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(CREATE_OPTIONS);
        }
    }

    public static boolean HasCatalog() throws SQLException {
        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM Options"
        )) {
            ResultSet rs = statement.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    public static void InsertOptions(
            int optionsID,
            int option1Archetype,
            int option2Archetype,
            int option3Archetype,
            String option1,
            String option2,
            String option3) throws SQLException {
        EnsureTables();

        Connection conn = SQLite.getConnection();
        try (PreparedStatement statement = conn.prepareStatement(
                """
                   INSERT OR IGNORE INTO Options 
                   (optionsID, option1Archetype, option2Archetype, option3Archetype, option1, option2, option3)
                   VALUES (?, ?, ?, ?, ?, ?, ?)
                   """)) {
            statement.setInt(1, optionsID);
            statement.setInt(2, option1Archetype);
            statement.setInt(3, option2Archetype);
            statement.setInt(4, option3Archetype);
            statement.setString(5, option1);
            statement.setString(6, option2);
            statement.setString(7, option3);

            statement.executeUpdate();
        }

    }

    public static List<Option> GetOptions() throws SQLException {
        EnsureTables();
        Connection conn = SQLite.getConnection();
        try (PreparedStatement statement = conn.prepareStatement(
                """
                    SELECT * FROM Options ORDER BY optionsID
                    """)){
            ResultSet rs = statement.executeQuery();
            List<Option> options = new ArrayList<>();
            while (rs.next()) {
                options.add(new Option(
                        rs.getInt("optionsID"),
                        rs.getInt("option1Archetype"),
                        rs.getInt("option2Archetype"),
                        rs.getInt("option3Archetype"),
                        rs.getString("option1"),
                        rs.getString("option2"),
                        rs.getString("option3")
                ));
            }
            return options;
        }
    }

    public static Option GetOptionById(int optionsID) throws SQLException {
        EnsureTables();
        Connection conn = SQLite.getConnection();
        try (PreparedStatement statement = conn.prepareStatement(
                """
                    SELECT * FROM Options WHERE optionsID = ?
                    """
        )) {
            statement.setInt(1, optionsID);
            ResultSet rs = statement.executeQuery();

            Option option = null;
            if (rs.next()) {
                option = new Option(
                        rs.getInt("optionsID"),
                        rs.getInt("option1Archetype"),
                        rs.getInt("option2Archetype"),
                        rs.getInt("option3Archetype"),
                        rs.getString("option1"),
                        rs.getString("option2"),
                        rs.getString("option3")
                );
            }
            return option;
        }
    }

    public static void SeedCatalog() throws SQLException {

        InsertOptions(1, 1, 2, 3,
                "Trust that things will work out and see the good in people",
                "Stand alongside others as an equal and belong",
                "Face challenges head-on and prove myself through action");

        InsertOptions(2, 1,2,3,
                "Remind everyone of what's still going right and keep faith it will improve",
                "Bring people together so no one carries the setback alone",
                "Take the hardest part on yourself and lead the push through it");

        InsertOptions(3, 1,2,3,
                "Keep hope alive and believe it can turn out well",
                "Pull together with the people around me",
                "Meet the fear directly and act anyway");

        InsertOptions(4, 1, 2,3,
                "Trust that it's fixable and approach it without assuming the worst",
                "Rally the group so it's handled fairly, by everyone together",
                "Step up and confront it yourself, even if it's daunting");

        InsertOptions(5, 5, 6, 7,
                "Support and protect the people who need it",
                "Transform how people see what's possible",
                "Seek out the truth and understand how things really work");

        InsertOptions(6, 5, 6, 7,
                "Comfort them first and make sure they don't face it alone",
                "Help them reframe the whole situation so a new way forward opens up",
                "Work through it with them calmly until they understand it clearly");

        InsertOptions(7, 5, 6, 7,
                "Meet them with warmth and give what they genuinely need",
                "Shift their perspective so the whole picture changes",
                "Offer clear understanding they can think with");

        InsertOptions(8, 5, 6, 7,
                "Tend to the people affected and steady them first",
                "Look for the deeper pattern and imagine what it could become",
                "Step back, seek the facts, and keep an open mind until it's clear");

        InsertOptions(9, 9, 10, 4,
                "Deep connection with the people and things I cherish",
                "Lightness, play, and helping others enjoy the moment",
                "Discovery and venturing beyond the familiar");

        InsertOptions(10, 9, 10, 4,
                "Bring people close and reconnect them to why it matters",
                "Break the tension with humour and remind everyone to enjoy the moment",
                "Suggest a change of scene or something new to shake off the rut");

        InsertOptions(11, 9, 10, 4,
                "Give myself fully to what and whom I love",
                "Bring joy and keep things from getting heavy",
                "Keep seeking, learning, and finding what's out there");

        InsertOptions(12, 9, 10, 4,
                "Spend it fully present with someone who matters to me",
                "Do something spontaneous and let the fun lead",
                "Head somewhere unfamiliar just to see what's there");

        InsertOptions(13, 12, 11, 8,
                "Create something new based on my own experiences",
                "Bring structure and stability to a disordered environment",
                "Stand up for what is right to combat injustice around me");

        InsertOptions(14, 12, 11, 8,
                "Set the old approach aside and design a fresh process from the ground up",
                "Find what's already working and bring everything else up to that standard",
                "Identify the patterns and behaviours that caused this mess and focus on removing them");

        InsertOptions(15, 12, 11, 8,
                "Make something original that carries my own mark",
                "Build something dependable that others can rely on",
                "Change what is broken so it serves people better");

        InsertOptions(16, 12, 11, 8,
                "Build a better alternative and let its results make the case",
                "Reform the rule properly through the system so the change holds",
                "Pinpoint exactly why the rule fails people and push to strike it out");

    }

}

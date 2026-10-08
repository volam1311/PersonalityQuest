package com.example.personalityquest.DAO.personalisation;

import com.example.personalityquest.DAO.ParentDAO;
import com.example.personalityquest.Model.quiz.Option;
import com.example.personalityquest.SQLite;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** Stores and retrieves quiz answer-option records */
public class OptionDAO extends ParentDAO {
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

    public OptionDAO() {
        super();
    }

    public OptionDAO(Connection connection){
        super(connection);
    }


    /** Creates option tables if they do not already exist
     * @throws SQLException if the tables cannot be created
     */
    public void EnsureTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(CREATE_OPTIONS);
        }
    }

    /** Checks whether option catalog records exist
     * @return true when the catalog contains records
     * @throws SQLException if the catalog cannot be checked
     */
    public boolean HasCatalog() throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM Options"
        )) {
            ResultSet rs = statement.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    /**
     * insert a set of answer options if its ID is not present
     *
     * @param optionsID the option set's unique ID
     * @param option1Archetype the archetype ID associated with the first answer
     * @param option2Archetype the archetype ID associated with the second answer
     * @param option3Archetype the archetype ID associated with the third answer
     * @param option1 the text of the first answer
     * @param option2 the text of the second answer
     * @param option3 the text of the third answer
     * @throws SQLException if the option set cannot be inserted
     */
    public void InsertOptions(
            int optionsID,
            int option1Archetype,
            int option2Archetype,
            int option3Archetype,
            String option1,
            String option2,
            String option3) throws SQLException {
        EnsureTables();

        try (PreparedStatement statement = connection.prepareStatement(
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

    /** Returns option records ordered by ID
     * @return the options in the catalog
     * @throws SQLException if the options cannot be read
     */
    public List<Option> GetOptions() throws SQLException {
        EnsureTables();
        try (PreparedStatement statement = connection.prepareStatement(
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

    /** Returns the option record matching the supplied ID
     * @param optionsID the option ID to find
     * @return the matching option, or null if no option is found
     * @throws SQLException if the option cannot be read
     */
    public Option GetOptionById(int optionsID) throws SQLException {
        EnsureTables();
        try (PreparedStatement statement = connection.prepareStatement(
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

    /** Inserts the default option catalog
     * @throws SQLException if the catalog cannot be inserted
     */
    public void SeedCatalog() throws SQLException {

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

        InsertOptions(5, 4, 5, 6,
                "Support and protect the people who need it",
                "Transform how people see what's possible",
                "Seek out the truth and understand how things really work");

        InsertOptions(6, 4, 5, 6,
                "Comfort them first and make sure they don't face it alone",
                "Help them reframe the whole situation so a new way forward opens up",
                "Work through it with them calmly until they understand it clearly");

        InsertOptions(7, 4, 5, 6,
                "Meet them with warmth and give what they genuinely need",
                "Shift their perspective so the whole picture changes",
                "Offer clear understanding they can think with");

        InsertOptions(8, 4, 5, 6,
                "Tend to the people affected and steady them first",
                "Look for the deeper pattern and imagine what it could become",
                "Step back, seek the facts, and keep an open mind until it's clear");

        InsertOptions(9, 7, 8, 9,
                "Deep connection with the people and things I cherish",
                "Lightness, play, and helping others enjoy the moment",
                "Discovery and venturing beyond the familiar");

        InsertOptions(10, 7, 8, 9,
                "Bring people close and reconnect them to why it matters",
                "Break the tension with humour and remind everyone to enjoy the moment",
                "Suggest a change of scene or something new to shake off the rut");

        InsertOptions(11, 7, 8, 9,
                "Give myself fully to what and whom I love",
                "Bring joy and keep things from getting heavy",
                "Keep seeking, learning, and finding what's out there");

        InsertOptions(12, 7, 8, 9,
                "Spend it fully present with someone who matters to me",
                "Do something spontaneous and let the fun lead",
                "Head somewhere unfamiliar just to see what's there");

        InsertOptions(13, 10, 11, 12,
                "Create something new based on my own experiences",
                "Bring structure and stability to a disordered environment",
                "Stand up for what is right to combat injustice around me");

        InsertOptions(14, 10, 11, 12,
                "Set the old approach aside and design a fresh process from the ground up",
                "Find what's already working and bring everything else up to that standard",
                "Identify the patterns and behaviours that caused this mess and focus on removing them");

        InsertOptions(15, 10, 11, 12,
                "Make something original that carries my own mark",
                "Build something dependable that others can rely on",
                "Change what is broken so it serves people better");

        InsertOptions(16, 10, 11, 12,
                "Build a better alternative and let its results make the case",
                "Reform the rule properly through the system so the change holds",
                "Pinpoint exactly why the rule fails people and push to strike it out");

    }

}

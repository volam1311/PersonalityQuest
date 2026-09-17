package com.example.personalityquest.DAO.personalisation;

import com.example.personalityquest.Model.quiz.Option;
import com.example.personalityquest.Model.quiz.Question;
import com.example.personalityquest.SQLite;


import java.util.ArrayList;
import java.util.List;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class QuestionDAO {
    private static final String CREATE_QUESTIONS = """
            CREATE TABLE IF NOT EXISTS Questions (
            questionID INTEGER PRIMARY KEY,
            realmType TEXT NOT NULL,
            questionType TEXT NOT NULL,
            questionPrompt TEXT NOT NULL
            )""";

    private QuestionDAO() {
    }

    public static void SeedCatalog() throws SQLException {

        // The Four Realms are Ego, Soul, Self and Mark --> Each Realm contains 3 Archetypes
        // There are four questions for each Realm, for a total of 16 Questions
        // Each set of four questions has two Direct question and two scenario questions
            // Direct questions are odd numbers, scenario questions are even numbers

        // To test all Archetypes, a Quiz must include one question from each set
        // Valid Configurations for testing all archetypes are below:
        // <Very Short> = questionID(1, 5, 9, 13)
        // <Short> = questionID(1,2, 5,6, 9,10, 13,14)
        // <Medium> = questionID(1,2,3, 5,6,7, 9,10,11, 13,14,15)

        InsertQuestion(1, "Ego", "Direct", "How do you want to move through the world?");
        InsertQuestion(2, "Ego", "Scenario", "Your group is facing a setback and morale is low. How do you respond?");
        InsertQuestion(3, "Ego", "Direct", "What matters most to you when things get hard?");
        InsertQuestion(4, "Ego", "Scenario", "Something has gone wrong and no one wants to deal with it. What do you do?");

        InsertQuestion(5, "Soul", "Direct", "How do you want to make a difference?");
        InsertQuestion(6, "Soul", "Scenario", "A friend comes to you overwhelmed by a problem they can't see their way out of. How do you respond?");
        InsertQuestion(7, "Soul", "Direct", "What matters most to you in how you help others?");
        InsertQuestion(8, "Soul", "Scenario", "You're handed a situation nobody fully understands and everyone's anxious about. What's your instinct?");

        InsertQuestion(9, "Self", "Direct", "What draws you toward life?");
        InsertQuestion(10, "Self", "Scenario", "A long, gruelling stretch of work has left everyone drained. How do you lift things?");
        InsertQuestion(11, "Self", "Direct", "What matters most to you in how you spend your days?");
        InsertQuestion(12, "Self", "Scenario", "You've got a rare free day and no obligations. What's your pull?");

        InsertQuestion(13, "Mark", "Direct", "How do you want to leave your mark on the world? (direct)");
        InsertQuestion(14, "Mark", "Scenario", "Your team has inherited a project that's a complete mess — no clear direction, and the old way of doing things plainly isn't working. What's your instinct?");
        InsertQuestion(15, "Mark", "Direct", "What matters most to you in the work you do?");
        InsertQuestion(16, "Mark", "Scenario", "You discover a long-standing rule at work is holding good people back, but plenty of others just quietly follow it. What do you do? (scenario)");
    }

    public static void EnsureTables() throws SQLException {
        Connection connection = SQLite.getConnection();
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(CREATE_QUESTIONS);
        }
    }

    public static boolean HasCatalog() throws SQLException {
        Connection connection = SQLite.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM Questions")) {
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;
        }
    }

    public static void InsertQuestion(
            int questionID,
            String realmType,
            String questionType,
            String questionPrompt) throws SQLException {
        EnsureTables();
        Connection connection = SQLite.getConnection();

        try (PreparedStatement statement = connection.prepareStatement(
                """ 
                    INSERT OR IGNORE INTO Questions
                    (questionID, realmType, questionType, questionPrompt)
                    VALUES (?, ?, ?, ?) 
                    """)) {
            statement.setInt(1, questionID);
            statement.setString(2, realmType);
            statement.setString(3, questionType);
            statement.setString(4, questionPrompt);

            statement.executeUpdate();
        }
    }

    public static List<Question> GetQuestions() throws SQLException {
        EnsureTables();
        Connection connection = SQLite.getConnection();
        try(PreparedStatement statement = connection.prepareStatement(
                """
                    SELECT * From Questions ORDER BY questionID
                    """)){
            ResultSet rs = statement.executeQuery();
            List<Question> questions = new ArrayList<>();
            while (rs.next()) {
                int questionID = rs.getInt("questionID");
                //List<Option> options = OptionDAO.GetOptionsById(questionID);
                Option options = OptionDAO.GetOptionById(questionID);
                questions.add(new Question(
                        rs.getInt("questionID"),
                        rs.getString("realmType"),
                        rs.getString("questionType"),
                        rs.getString("questionPrompt"),
                        options
                ));
            }
            return questions;
        }
    }


}





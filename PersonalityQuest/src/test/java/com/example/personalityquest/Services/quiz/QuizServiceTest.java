package com.example.personalityquest.Services.quiz;

import com.example.personalityquest.SQLite;
import com.example.personalityquest.Model.quiz.Archetype;
import com.example.personalityquest.Model.quiz.Option;
import com.example.personalityquest.Model.quiz.Question;
import com.example.personalityquest.Model.quiz.QuizResult;
import com.example.personalityquest.Services.auth.HashingService;
import com.example.personalityquest.Services.quiz.QuizService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class QuizServiceTest {
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        QuizService.Reset();
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");
        SQLite.setConnection(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        QuizService.Reset();
        if (connection != null) {
            connection.close();
        }
    }

    @Test
    void StartQuizLoadsFiveQuestions() {
        QuizService.StartQuiz();

        assertEquals(5, QuizService.GetQuestionCount());
        assertEquals(1, QuizService.GetCurrentQuestion().getId());
        assertEquals(4, QuizService.GetCurrentQuestion().getOptions().size());
        assertTrue(QuizService.HasActiveAttempt());
        assertNull(QuizService.GetResult());
    }

    @Test
    void MethodsThrowWhenQuizHasNotStarted() {
        assertThrows(IllegalStateException.class, QuizService::GetCurrentQuestion);
        assertThrows(IllegalStateException.class, QuizService::CompleteQuiz);
    }

    @Test
    void CompleteQuizScoresInnocentWhenChosenFourTimes() {
        QuizService.StartQuiz();
        AnswerEveryQuestionAtIndex(0);

        QuizResult result = QuizService.CompleteQuiz();

        assertEquals(Archetype.INNOCENT, result.archetype());
        assertEquals(4, result.scores().get(Archetype.INNOCENT));
        assertEquals(1, result.scores().get(Archetype.CAREGIVER));
        assertFalse(QuizService.HasActiveAttempt());
        assertEquals(result, QuizService.GetResult());
    }

    @Test
    void CompleteQuizScoresExplorerWhenChosenEveryTime() {
        QuizService.StartQuiz();
        AnswerEveryQuestionAtIndex(3);

        QuizResult result = QuizService.CompleteQuiz();

        assertEquals(Archetype.EXPLORER, result.archetype());
        assertEquals(5, result.scores().get(Archetype.EXPLORER));
    }

    @Test
    void ScoreBreaksTiesWithTheLaterAnswer() {
        QuizResult result = QuizService.Score(List.of(
                option(Archetype.INNOCENT),
                option(Archetype.INNOCENT),
                option(Archetype.HERO),
                option(Archetype.HERO),
                option(Archetype.SAGE)));

        assertEquals(Archetype.HERO, result.archetype());
        assertEquals(2, result.scores().get(Archetype.INNOCENT));
        assertEquals(2, result.scores().get(Archetype.HERO));
        assertEquals(1, result.scores().get(Archetype.SAGE));
        assertEquals(
                List.of(Archetype.HERO, Archetype.INNOCENT, Archetype.SAGE),
                QuizService.RankedArchetypes(result));
    }

    @Test
    void CannotAdvanceWithoutAnswering() {
        QuizService.StartQuiz();

        assertThrows(IllegalStateException.class, QuizService::GoToNextQuestion);
        assertThrows(IllegalStateException.class, QuizService::CompleteQuiz);
    }

    @Test
    void GoingBackRestoresThePreviousAnswer() {
        QuizService.StartQuiz();
        Option first = QuizService.GetCurrentQuestion().getOptions().get(1);
        QuizService.AnswerCurrentQuestion(first);
        QuizService.GoToNextQuestion();

        QuizService.GoToPreviousQuestion();

        assertEquals(0, QuizService.GetCurrentQuestionIndex());
        assertEquals(first, QuizService.GetAnswerForCurrentQuestion());
    }

    @Test
    void RejectsAnOptionFromADifferentQuestion() {
        QuizService.StartQuiz();
        Option current = QuizService.GetCurrentQuestion().getOptions().get(0);
        QuizService.AnswerCurrentQuestion(current);
        QuizService.GoToNextQuestion();

        Option previous = current;
        assertThrows(IllegalArgumentException.class, () -> QuizService.AnswerCurrentQuestion(previous));
    }

    @Test
    void AssignQuestForCurrentResultCreatesAnActiveQuest() throws SQLException {
        CreateArchetypeSchema();
        QuizService.StartQuiz();
        AnswerEveryQuestionAtIndex(3);
        QuizService.CompleteQuiz();

        QuizResult result = QuizService.AssignQuestForCurrentResult("test@example.com");

        assertNotNull(result.assignedQuest());
        assertEquals("Explore the unknown", result.assignedQuest().getName());
        assertEquals(7, result.assignedQuest().getArchetypeId());
    }

    @Test
    void AssignQuestForCurrentResultIsSafeWhenTheDatabaseHasNoMatch() {
        QuizService.StartQuiz();
        AnswerEveryQuestionAtIndex(0);
        QuizService.CompleteQuiz();

        QuizResult result = QuizService.AssignQuestForCurrentResult("test@example.com");

        assertNull(result.assignedQuest());
        assertEquals(Archetype.INNOCENT, result.archetype());
    }

    @Test
    void QuestionRequiresPromptAndOptions() {
        assertThrows(IllegalArgumentException.class, () ->
                new Question(0, "Prompt", List.of(option(Archetype.HERO), option(Archetype.SAGE))));
        assertThrows(IllegalArgumentException.class, () ->
                new Question(1, " ", List.of(option(Archetype.HERO), option(Archetype.SAGE))));
        assertThrows(IllegalArgumentException.class, () ->
                new Question(1, "Prompt", List.of(option(Archetype.HERO))));
        assertThrows(IllegalArgumentException.class, () ->
                new Option(" ", Archetype.HERO));
        assertThrows(IllegalArgumentException.class, () ->
                new Option("Stay hopeful", null));
    }

    private void AnswerEveryQuestionAtIndex(int optionIndex) {
        while (true) {
            Question question = QuizService.GetCurrentQuestion();
            QuizService.AnswerCurrentQuestion(question.getOptions().get(optionIndex));
            if (QuizService.IsLastQuestion()) {
                return;
            }
            QuizService.GoToNextQuestion();
        }
    }

    private Option option(Archetype archetype) {
        return new Option("Choose " + archetype.getName(), archetype);
    }

    private void CreateArchetypeSchema() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                    CREATE TABLE Accounts (
                        email TEXT NOT NULL,
                        userName TEXT NOT NULL,
                        firstName TEXT NOT NULL,
                        lastName TEXT NOT NULL,
                        password TEXT NOT NULL,
                        PRIMARY KEY(email)
                    )
                    """);
            statement.execute("""
                    CREATE TABLE Archetype (
                        archetypeId INT PRIMARY KEY,
                        name TEXT NOT NULL,
                        smallDescription TEXT NOT NULL
                    )
                    """);
            statement.execute("""
                    CREATE TABLE Quests (
                        labourId INT PRIMARY KEY,
                        archetypeId INT NOT NULL,
                        name TEXT NOT NULL,
                        FOREIGN KEY(archetypeId) REFERENCES Archetype(archetypeId)
                    )
                    """);
            statement.execute("""
                    CREATE TABLE UserQuests (
                        accountEmail TEXT NOT NULL,
                        labourId INT NOT NULL,
                        percentageComplete FLOAT NOT NULL,
                        status TEXT NOT NULL,
                        PRIMARY KEY(accountEmail, labourId),
                        FOREIGN KEY(labourId) REFERENCES Quests(labourId)
                    )
                    """);
        }

        try (PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO Accounts (email, userName, firstName, lastName, password)
                    VALUES ('test@example.com', 'test', 'Test', 'User', ?)
                    """)) {
            statement.setString(1, HashingService.Hash("test"));
            statement.executeUpdate();
        }

        try (PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO Archetype (archetypeId, name, smallDescription)
                    VALUES (7, 'Explorer', 'Freedom and discovery')
                    """)) {
            statement.executeUpdate();
        }

        try (PreparedStatement statement = connection.prepareStatement(
                """
                    INSERT INTO Quests (labourId, archetypeId, name)
                    VALUES (30, 7, 'Explore the unknown')
                    """)) {
            statement.executeUpdate();
        }
    }
}

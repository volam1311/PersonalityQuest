package com.example.personalityquest.Services.quiz;

import com.example.personalityquest.SQLite;
import com.example.personalityquest.Model.quiz.Archetype;
import com.example.personalityquest.Model.quiz.Option;
import com.example.personalityquest.Model.quiz.Question;
import com.example.personalityquest.Model.quiz.QuizResult;
import com.example.personalityquest.Services.auth.HashingService;
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
    private static final int QUIZ_QUESTION_COUNT = 16;

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
    void StartQuizLoadsSixteenQuestions() {
        QuizService.StartQuiz();

        Question question = QuizService.GetCurrentQuestion();
        Option options = question.getOptions();

        assertEquals(QUIZ_QUESTION_COUNT, QuizService.GetQuestionCount());
        assertEquals(1, question.getQuestionID());
        assertFalse(options.getOption1().isBlank());
        assertFalse(options.getOption2().isBlank());
        assertFalse(options.getOption3().isBlank());
        assertTrue(QuizService.HasActiveAttempt());
        assertNull(QuizService.GetResult());
    }

    @Test
    void MethodsThrowWhenQuizHasNotStarted() {
        assertThrows(IllegalStateException.class, QuizService::GetCurrentQuestion);
        assertThrows(IllegalStateException.class, QuizService::CompleteQuiz);
    }

    @Test
    void CompleteQuizScoresInnocentWhenChosenOnEveryEgoQuestion() {
        QuizService.StartQuiz();
        AnswerAllQuestions(1, 1, 1, 1, 2, 2, 2, 3, 2, 2, 2, 3, 2, 2, 2, 3);

        QuizResult result = QuizService.CompleteQuiz();

        assertEquals(Archetype.INNOCENT, result.archetype());
        assertEquals(4, result.scores().get(Archetype.INNOCENT));
        assertFalse(QuizService.HasActiveAttempt());
        assertEquals(result, QuizService.GetResult());
    }

    @Test
    void CompleteQuizScoresExplorerWhenChosenOnEverySelfQuestion() {
        QuizService.StartQuiz();
        AnswerAllQuestions(1, 1, 1, 2, 1, 1, 1, 2, 3, 3, 3, 3, 1, 1, 1, 2);

        QuizResult result = QuizService.CompleteQuiz();

        assertEquals(Archetype.EXPLORER, result.archetype());
        assertEquals(4, result.scores().get(Archetype.EXPLORER));
    }

    @Test
    void ScoreBreaksTiesWithTheLaterAnswer() {
        QuizResult result = QuizService.Score(List.of(
                Archetype.INNOCENT.getArchetypeId(),
                Archetype.INNOCENT.getArchetypeId(),
                Archetype.HERO.getArchetypeId(),
                Archetype.HERO.getArchetypeId(),
                Archetype.SAGE.getArchetypeId()));

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
        int first = QuizService.GetCurrentQuestion().getOptions().getOption2Archetype();
        QuizService.AnswerCurrentQuestion(first);
        QuizService.GoToNextQuestion();

        QuizService.GoToPreviousQuestion();

        assertEquals(0, QuizService.GetCurrentQuestionIndex());
        assertEquals(first, QuizService.GetAnswerForCurrentQuestion());
    }

    @Test
    void RejectsAnOptionFromADifferentRealm() {
        QuizService.StartQuiz();

        assertThrows(IllegalStateException.class, () ->
                QuizService.AnswerCurrentQuestion(Archetype.OUTLAW.getArchetypeId()));
    }

    @Test
    void AssignQuestForCurrentResultCreatesAnActiveQuest() throws SQLException {
        CreateQuestAssignmentSchema();
        QuizService.StartQuiz();
        AnswerAllQuestions(1, 1, 1, 2, 1, 1, 1, 2, 3, 3, 3, 3, 1, 1, 1, 2);
        QuizService.CompleteQuiz();

        QuizResult result = QuizService.AssignQuestForCurrentResult("test@example.com");

        assertNotNull(result.assignedQuest());
        assertEquals("Explore the unknown", result.assignedQuest().getName());
        assertEquals(Archetype.EXPLORER.getArchetypeId(), result.assignedQuest().getArchetypeId());
    }

    @Test
    void AssignQuestForCurrentResultIsSafeWhenTheDatabaseHasNoMatch() {
        QuizService.StartQuiz();
        AnswerAllQuestions(1, 1, 1, 1, 2, 2, 2, 3, 2, 2, 2, 3, 2, 2, 2, 3);
        QuizService.CompleteQuiz();

        QuizResult result = QuizService.AssignQuestForCurrentResult("test@example.com");

        assertNull(result.assignedQuest());
        assertEquals(Archetype.INNOCENT, result.archetype());
    }

    @Test
    void QuestionRequiresPromptAndValidOptions() {
        Option validOption = new Option(1, 3, 6, 9, "Stay hopeful", "Seek truth", "Explore");

        assertThrows(IllegalArgumentException.class, () ->
                new Question(0, "Ego", "Direct", "Prompt", validOption));
        assertThrows(IllegalArgumentException.class, () ->
                new Question(1, "Ego", "Direct", " ", validOption));
        assertThrows(IllegalArgumentException.class, () ->
                new Option(1, 3, 6, 9, " ", "Seek truth", "Explore"));
        assertThrows(IllegalArgumentException.class, () ->
                new Option(1, 0, 6, 9, "Stay hopeful", "Seek truth", "Explore"));
    }

    private void AnswerAllQuestions(int... optionNumbers) {
        assertEquals(QUIZ_QUESTION_COUNT, optionNumbers.length);
        for (int index = 0; index < optionNumbers.length; index++) {
            AnswerCurrentOption(optionNumbers[index]);
            if (!QuizService.IsLastQuestion()) {
                QuizService.GoToNextQuestion();
            }
        }
    }

    private void AnswerCurrentOption(int optionNumber) {
        Option option = QuizService.GetCurrentQuestion().getOptions();
        int archetypeId = switch (optionNumber) {
            case 1 -> option.getOption1Archetype();
            case 2 -> option.getOption2Archetype();
            case 3 -> option.getOption3Archetype();
            default -> throw new IllegalArgumentException("Option number must be 1, 2, or 3");
        };
        QuizService.AnswerCurrentQuestion(archetypeId);
    }

    private void CreateQuestAssignmentSchema() throws SQLException {
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
                    CREATE TABLE Quests (
                        labourId INT PRIMARY KEY,
                        archetypeId INT NOT NULL,
                        name TEXT NOT NULL,
                        narrative TEXT NOT NULL DEFAULT '',
                        decisionQuestion TEXT NOT NULL DEFAULT '',
                        resolution TEXT NOT NULL DEFAULT ''
                    )
                    """);
            statement.execute("""
                    CREATE TABLE UserQuests (
                        accountEmail TEXT NOT NULL,
                        labourId INT NOT NULL,
                        percentageComplete FLOAT NOT NULL,
                        status TEXT NOT NULL,
                        reflection TEXT NOT NULL DEFAULT '',
                        reflectionStatus TEXT NOT NULL DEFAULT 'Not Started',
                        reactionType TEXT NOT NULL DEFAULT 'None',
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
                    INSERT INTO Quests (labourId, archetypeId, name, narrative)
                    VALUES (30, ?, 'Explore the unknown', '')
                    """)) {
            statement.setInt(1, Archetype.EXPLORER.getArchetypeId());
            statement.executeUpdate();
        }
    }
}

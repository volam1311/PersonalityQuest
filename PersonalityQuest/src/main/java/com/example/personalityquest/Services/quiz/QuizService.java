package com.example.personalityquest.Services.quiz;

import com.example.personalityquest.DAO.personalisation.OptionDAO;
import com.example.personalityquest.DAO.personalisation.QuestionDAO;
import com.example.personalityquest.DAO.quest.QuizResultDAO;
import com.example.personalityquest.Services.quest.QuestService;
import com.example.personalityquest.Services.quest.UserQuestService;
import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.Model.quiz.Archetype;
import com.example.personalityquest.Model.quiz.Option;
import com.example.personalityquest.Model.quest.Quest;
import com.example.personalityquest.Model.quiz.Question;
import com.example.personalityquest.Model.quiz.QuizResult;
import com.example.personalityquest.Model.quest.UserQuest;

import java.sql.SQLException;
import java.util.*;

/**
 * Runs the archetype quiz: dummy questions, answers, scoring, and quest assignment.
 */
public class QuizService {
    private static List<Question> questions = List.of();
    private static Integer[] answers = new Integer[0];
    private static int currentIndex;
    private static boolean inProgress;
    private static QuizResult result;

    private QuizService() {
    }

    /**
     * Clears any in-progress attempt or stored result.
     */
    public static void Reset() {
        questions = List.of();
        answers = new Integer[0];
        currentIndex = 0;
        inProgress = false;
        result = null;
    }

    private static final boolean DEMO_MODE = Boolean.getBoolean("quiz.demo");

    /**
     * Starts a new attempt with the dummy five-question bank.
     */
    public static void StartQuiz() {
        try {
            EnsureCatalog();
            questions = QuestionDAO.GetQuestions();
            if (DEMO_MODE){
                questions = DemoQuestionSet(questions);
            }
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not load quiz questions", exception);
        }
        answers = new Integer[questions.size()];
        currentIndex = 0;
        inProgress = true;
        result = null;
    }

    private static List<Question> FirstQuestionPerRealm(List<Question> all){
        List<Question> shortList = new ArrayList<>();
        Set<String> seenRealms = new LinkedHashSet<>();
        for (Question question : all) {
            if (seenRealms.add(question.getRealmType())) {
                shortList.add(question);
            }
        }
        return shortList;
    }

    public static void EnsureCatalog() throws SQLException {
        QuestionDAO.EnsureTables();
        if (!QuestionDAO.HasCatalog()){
            QuestionDAO.SeedCatalog();
        }
        OptionDAO.EnsureTables();
        if (!OptionDAO.HasCatalog()){
            OptionDAO.SeedCatalog();
        }
    }

    /**
     * Whether an attempt is underway and has not been scored yet.
     */
    public static boolean HasActiveAttempt() {
        return inProgress && result == null && !questions.isEmpty();
    }

    /**
     * The most recent scored result, or null if the quiz has not been finished.
     */
    public static QuizResult GetResult() {
        return result;
    }

    /**
     * The dummy question bank used by the current attempt.
     */
    public static List<Question> GetQuestions() {
        EnsureInProgress();
        return questions;
    }

    /**
     * The question currently on screen.
     */
    public static Question GetCurrentQuestion() {
        EnsureInProgress();
        return questions.get(currentIndex);
    }

    /**
     * Zero-based index of the current question.
     */
    public static int GetCurrentQuestionIndex() {
        EnsureInProgress();
        return currentIndex;
    }

    /**
     * How many questions are in the current attempt.
     */
    public static int GetQuestionCount() {
        EnsureInProgress();
        return questions.size();
    }

    /**
     * Whether the current question is the last one.
     */
    public static boolean IsLastQuestion() {
        EnsureInProgress();
        return currentIndex == questions.size() - 1;
    }

    /**
     * Whether the user can move back from the current question.
     */
    public static boolean CanGoBack() {
        return HasActiveAttempt() && currentIndex > 0;
    }

    /**
     * Archetype id chosen for the current question, or null if not yet answered
     */
    public static Integer GetAnswerForCurrentQuestion() {
        EnsureInProgress();
        return answers[currentIndex];
    }

    /**
     * Stores the selected option for the current question.
     * @param archetypeId The archetypes corresponding with the selected question
     */
    public static void AnswerCurrentQuestion(int archetypeId) {
        EnsureInProgress();
        Option option = GetCurrentQuestion().getOptions();
        if (archetypeId != option.getOption1Archetype()
                && archetypeId != option.getOption2Archetype()
                && archetypeId != option.getOption3Archetype()){
            throw new IllegalStateException("Archetype id does not belong to this question");
        }
        answers[currentIndex] = archetypeId;
    }

    /**
     * Moves to the previous question so the user can change an answer.
     */
    public static void GoToPreviousQuestion() {
        EnsureInProgress();
        if (currentIndex == 0) {
            throw new IllegalStateException("Already on the first question");
        }
        currentIndex--;
    }

    /**
     * Moves to the next question after the current one has been answered.
     */
    public static void GoToNextQuestion() {
        EnsureInProgress();
        if (answers[currentIndex] == null) {
            throw new IllegalStateException("Current question has not been answered");
        }
        if (IsLastQuestion()) {
            throw new IllegalStateException("Already on the last question");
        }
        currentIndex++;
    }

    /**
     * Scores every answer and stores the result.
     * @return The winning archetype and per-archetype counts
     */
    public static QuizResult CompleteQuiz() {
        EnsureInProgress();
        for (int index = 0; index < answers.length; index++) {
            if (answers[index] == null) {
                throw new IllegalStateException("Question " + (index + 1) + " has not been answered");
            }
        }

        result = Score(List.of(answers));
        inProgress = false;
        return result;
    }

    /**
     * Assigns a matching quest to the signed-in account when one is available.
     * Missing database rows are ignored so the result page can still be shown.
     * @param email The account to assign a quest to
     * @return The stored result, with an assigned quest when one could be created
     */
    public static QuizResult AssignQuestForCurrentResult(String email) {
        if (result == null) {
            throw new IllegalStateException("Quiz has not been completed");
        }
        if (ApplicationManager.isEmpty(email)) {
            return result;
        }

        try {
            QuizResultDAO.SaveResult(email, result);
        } catch (Exception ignored){
            // Continue to show result if persistence fails
        }

        try {
            Integer archetypeId = QuestService.GetArchetypeIdForName(result.archetype().getName());
            if (archetypeId == null) {
                return result;
            }

            Quest quest = QuestService.GetRandomQuestForArchetypeId(archetypeId);
            if (quest == null) {
                return result;
            }

            UserQuestService.ChangeActiveQuest(quest,email);
            result = result.withAssignedQuest(quest);
            return result;

        } catch (Exception exception) {
            return result;
        }
    }

    /**
     * Counts answers per archetype. Ties go to the later answer among the tied winners.
     */
    public static QuizResult Score(List<Integer> selectedArchetypeIds) {
        if (selectedArchetypeIds == null || selectedArchetypeIds.isEmpty()) {
            throw new IllegalArgumentException("Quiz answers are empty");
        }

        Map<Archetype, Integer> scores = new EnumMap<>(Archetype.class);
        List<Archetype> order = new ArrayList<>();
        for (Integer archetypeID : selectedArchetypeIds) {
            if (archetypeID == null) {
                throw new IllegalArgumentException("Quiz answers contain a null option");
            }
            Archetype archetype = ArchetypeById(archetypeID);
            scores.merge(archetype, 1, Integer::sum);
            order.add(archetype);
        }

        Archetype winner = order.get(order.size() - 1);
        int highest = 0;
        for (Archetype archetype : order) {
            int score = scores.get(archetype);
            if (score >= highest) {
                highest = score;
                winner = archetype;
            }
        }

        return new QuizResult(winner, scores);
    }

    /**
     * Resolves a stored archetype id back to its enum constant
     */

    private static Archetype ArchetypeById(int archetypeId) {
        for (Archetype archetype : Archetype.values()){
            if (archetype.getArchetypeId() == archetypeId){
                return archetype;
            }
        }
        throw new IllegalArgumentException("Archetype with id " + archetypeId + " not found");
    }

    /**
     * Archetypes from a quiz result, winner first, then remaining scores high to low.
     */
    public static List<Archetype> RankedArchetypes(QuizResult quizResult) {
        if (quizResult == null) {
            throw new IllegalArgumentException("Quiz result is null");
        }

        List<Archetype> ranked = new ArrayList<>();
        ranked.add(quizResult.archetype());
        quizResult.scores().entrySet().stream()
                .filter(entry -> entry.getKey() != quizResult.archetype())
                .sorted(Map.Entry.<Archetype, Integer>comparingByValue().reversed()
                        .thenComparing(entry -> entry.getKey().name()))
                .map(Map.Entry::getKey)
                .forEach(ranked::add);
        return ranked;
    }

    /**
     * Each archetype's score, in {@link Archetype#values()} order, normalised so the
     * highest-scoring archetype reaches 1.0. Used to plot the 12-axis archetype radar
     * chart. Returns all zeros when there is no quiz result yet.
     */
    public static double[] ArchetypeScores(QuizResult quizResult) {
        Archetype[] archetypes = Archetype.values();
        double[] values = new double[archetypes.length];
        if (quizResult == null) {
            return values;
        }

        int max = 0;
        for (int score : quizResult.scores().values()) {
            if (score > max) {
                max = score;
            }
        }
        if (max == 0) {
            return values;
        }

        for (int index = 0; index < archetypes.length; index++) {
            Integer score = quizResult.scores().get(archetypes[index]);
            values[index] = score == null ? 0 : (double) score / max;
        }
        return values;
    }

    private static void EnsureInProgress() {
        if (!HasActiveAttempt()) {
            throw new IllegalStateException("Quiz has not been started");
        }
    }

    public static QuizResult LoadStoredResult(String email){
        if (result != null){
            return result;
        }
        if (ApplicationManager.isEmpty(email)){
            return null;
        }
        try {
            QuizResult stored = QuizResultDAO.LoadResult(email);
            if (stored != null){
            UserQuest activeQuest = UserQuestService.GetCurrentActiveUserQuestForEmail(email);
            if (activeQuest != null){
                Quest quest = QuestService.GetQuestForLabourId(activeQuest.getLabourId());
                if (quest != null){
                    stored = stored.withAssignedQuest(quest);
                }
            }
            result = stored;
            }
            return result;
        } catch (Exception exception) {
            return null;
        }
    }

    // Use Quiz questions for hero, Everyman and Innocent
    private static final String FEATURED_DEMO_REALM = "Ego";

    private static List<Question> DemoQuestionSet(List<Question> all){
        List<Question> selected = new ArrayList<>();
        Set<String> seenOtherRealms = new LinkedHashSet<>();
        for (Question question : all){
            if (FEATURED_DEMO_REALM.equals(question.getRealmType())){
                selected.add(question);
            } else if (seenOtherRealms.add(question.getRealmType())){
                selected.add(question);
            }
        }
        return selected;
    }




}

package com.example.personalityquest.Services.quiz;

import com.example.personalityquest.Services.quest.QuestService;
import com.example.personalityquest.Services.quest.UserQuestService;
import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.Model.quiz.Archetype;
import com.example.personalityquest.Model.quiz.Option;
import com.example.personalityquest.Model.quest.Quest;
import com.example.personalityquest.Model.quiz.Question;
import com.example.personalityquest.Model.quiz.QuizResult;
import com.example.personalityquest.Model.quest.UserQuest;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Runs the archetype quiz: dummy questions, answers, scoring, and quest assignment.
 */
public class QuizService {
    private static List<Question> questions = List.of();
    private static Option[] answers = new Option[0];
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
        answers = new Option[0];
        currentIndex = 0;
        inProgress = false;
        result = null;
    }

    /**
     * Starts a new attempt with the dummy five-question bank.
     */
    public static void StartQuiz() {
        questions = BuildQuestions();
        answers = new Option[questions.size()];
        currentIndex = 0;
        inProgress = true;
        result = null;
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
     * Previously chosen option for the current question, or null if none yet.
     */
    public static Option GetAnswerForCurrentQuestion() {
        EnsureInProgress();
        return answers[currentIndex];
    }

    /**
     * Stores the selected option for the current question.
     * @param option The option the user picked
     */
    public static void AnswerCurrentQuestion(Option option) {
        EnsureInProgress();
        if (option == null) {
            throw new IllegalArgumentException("Quiz option is null");
        }
        if (!GetCurrentQuestion().getOptions().contains(option)) {
            throw new IllegalArgumentException("Option does not belong to the current question");
        }
        answers[currentIndex] = option;
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
            UserQuest activeQuest = UserQuestService.GetCurrentActiveUserQuestForEmail(email);
            if (activeQuest != null) {
                return result;
            }

            Integer archetypeId = QuestService.GetArchetypeIdForName(result.archetype().getDisplayName());
            if (archetypeId == null) {
                return result;
            }

            Quest quest = QuestService.GetRandomQuestForArchetypeId(archetypeId);
            if (quest == null) {
                return result;
            }

            UserQuest inserted = UserQuestService.InsertNewQuestForEmail(quest, email);
            UserQuestService.SetUserQuesStatusAsActive(inserted, email);
            result = result.withAssignedQuest(quest);
            return result;
        } catch (Exception exception) {
            return result;
        }
    }

    /**
     * Counts answers per archetype. Ties go to the later answer among the tied winners.
     */
    public static QuizResult Score(List<Option> selectedOptions) {
        if (selectedOptions == null || selectedOptions.isEmpty()) {
            throw new IllegalArgumentException("Quiz answers are empty");
        }

        Map<Archetype, Integer> scores = new EnumMap<>(Archetype.class);
        List<Archetype> order = new ArrayList<>();
        for (Option option : selectedOptions) {
            if (option == null) {
                throw new IllegalArgumentException("Quiz answers contain a null option");
            }
            scores.merge(option.archetype(), 1, Integer::sum);
            order.add(option.archetype());
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

    private static void EnsureInProgress() {
        if (!HasActiveAttempt()) {
            throw new IllegalStateException("Quiz has not been started");
        }
    }

    private static List<Question> BuildQuestions() {
        return List.of(
                new Question(1, "When facing a new challenge, what's your instinct?", List.of(
                        new Option("Trust that things will work out if I stay positive", Archetype.INNOCENT),
                        new Option("Rally the people around me and pull together", Archetype.EVERYMAN),
                        new Option("Take it on directly and prove I can win", Archetype.HERO),
                        new Option("Explore different paths and see where they lead", Archetype.EXPLORER))),
                new Question(2, "What do you most want people to feel around you?", List.of(
                        new Option("Safe, hopeful, and at ease", Archetype.INNOCENT),
                        new Option("Included and like they belong", Archetype.EVERYMAN),
                        new Option("Inspired to push harder", Archetype.HERO),
                        new Option("Free to be themselves and follow their own path", Archetype.EXPLORER))),
                new Question(3, "What's your relationship to rules and the way things are?", List.of(
                        new Option("I trust the system is basically good", Archetype.INNOCENT),
                        new Option("I just want fair treatment for everyone", Archetype.EVERYMAN),
                        new Option("I'll break them if it means winning the right fight", Archetype.HERO),
                        new Option("I question rules that limit freedom or discovery", Archetype.EXPLORER))),
                new Question(4, "What gets you through a hard day?", List.of(
                        new Option("Believing tomorrow will be better", Archetype.INNOCENT),
                        new Option("The people who have my back", Archetype.EVERYMAN),
                        new Option("Refusing to be beaten", Archetype.HERO),
                        new Option("Knowing there are still new possibilities ahead", Archetype.EXPLORER))),
                new Question(5, "How do you most want to change the world?", List.of(
                        new Option("By looking after people who need it", Archetype.CAREGIVER),
                        new Option("By transforming how people see things", Archetype.MAGICIAN),
                        new Option("By uncovering and sharing the truth", Archetype.SAGE),
                        new Option("By opening new paths for people to explore", Archetype.EXPLORER)))
        );
    }
}

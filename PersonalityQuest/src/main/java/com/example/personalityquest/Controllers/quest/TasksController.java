package com.example.personalityquest.Controllers.quest;

import com.example.personalityquest.Controllers.navigation.NavBarController;
import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.DAO.quest.ReflectionPromptDAO;
import com.example.personalityquest.Model.quest.*;
import com.example.personalityquest.ScreenEnum;
import com.example.personalityquest.Services.chat.ReflectionFeedbackService;
import com.example.personalityquest.Services.navigation.NavigationService;
import com.example.personalityquest.Services.profile.StreakService;
import com.example.personalityquest.Services.quest.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

/** Controls the Journal screen: quest challenges, weekly challenges, and reflections */
public class TasksController implements Initializable {

    private static final String CHECKMARK_PATH = "M -4 0.5 L -1 3.5 L 4.5 -3.5";

    @FXML
    private NavBarController navBarController;
    @FXML
    private BorderPane tasksRoot;
    @FXML
    private VBox detailCard;
    @FXML
    private VBox emptyStateBox;
    @FXML
    private VBox taskContentBox;
    @FXML
    private Label taskEyebrowLabel, taskTitleLabel, descriptionLabel, feedbackLabel, aiFeedbackLabel;
    @FXML
    private Label wordCountLabel, savedStatusLabel;
    @FXML
    private TextArea reflectionArea;
    @FXML
    private VBox aiFeedbackCard;
    @FXML
    private Button editButton, submitButton, feedbackButton;
    @FXML
    private Label questTitleLabel, questProgressLabel;
    @FXML
    private Label questChallengesCountLabel, questReflectionsCountLabel;
    @FXML
    private ProgressBar questProgress;
    @FXML
    private ListView<ChallengeListItem> questChallengesList;
    @FXML
    private ListView<ReflectionListItem> questReflectionsList;

    private record ChallengeListItem(JournalEntry entry, Quest quest, Task task, WeeklyTask weeklyTask){}
    private record ReflectionListItem(UserQuest userQuest, Quest quest, ReflectionPrompt prompt){}

    /** The weekly-challenge list item currently shown in the detail panel, or null if a quest challenge/reflection is shown instead */
    private ChallengeListItem selectedWeeklyChallenge;
    /** The journaled quest-challenge entry currently shown in the detail panel, or null otherwise */
    private ChallengeListItem selectedJournalChallenge;
    /** The quest reflection currently shown in the detail panel, or null if a challenge is shown instead */
    private ReflectionListItem selectedReflectionItem;
    private boolean generatingFeedback;
    private int feedbackRequestId;

    private boolean updatingJournalSelection;

    private WeeklyTaskService WeeklyTaskService;
    private UserQuestService UserQuestService;
    private TaskService TaskService;
    private QuestService QuestService;
    private JournalEntryService JournalEntryService;
    private StreakService StreakService;
    private ReflectionFeedbackService ReflectionFeedbackService;
    private ReflectionPromptDAO ReflectionPromptDAO;
    /** Initialises quest challenge/reflection controls and loads the active quest's data
     * @param location the location used to resolve relative paths
     * @param resources the localisation resources for the screen
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        QuestService = new QuestService();
        UserQuestService = new UserQuestService();
        WeeklyTaskService = new WeeklyTaskService();
        TaskService = new TaskService();
        JournalEntryService = new JournalEntryService();
        StreakService = new StreakService();
        ReflectionFeedbackService = new ReflectionFeedbackService();
        ReflectionPromptDAO = new ReflectionPromptDAO();
        navBarController.setCurrentDestination(NavBarController.NavDestination.TASKS);
        ConfigureQuestChallengesList();
        ConfigureQuestReflectionsList();
        ConfigureWordCount();
        PopulateActiveQuestHeader();
        LoadQuestChallenges();
        LoadQuestReflections();
        ShowEmptyDetail();
    }

    /** Keeps the word count label in sync with the reflection editor. */
    private void ConfigureWordCount() {
        reflectionArea.textProperty().addListener((obs, oldText, newText) -> UpdateWordCount(newText));
    }

    private void UpdateWordCount(String text) {
        String trimmed = text == null ? "" : text.trim();
        int count = trimmed.isEmpty() ? 0 : trimmed.split("\\s+").length;
        wordCountLabel.setText(count + (count == 1 ? " word" : " words"));
    }

    private void ShowJournalChallenge(ChallengeListItem item){
        if (item.weeklyTask() != null) {
            ShowWeeklyChallenge(item);
            return;
        }

        selectedWeeklyChallenge = null;
        selectedReflectionItem = null;
        selectedJournalChallenge = item;

        ShowTaskContent();

        JournalEntry entry = item.entry();
        Task task = item.task();

        taskEyebrowLabel.setText("CHALLENGE");
        taskTitleLabel.setText(task == null ? entry.getTitle() : task.getName());
        descriptionLabel.setText(task == null || ApplicationManager.isEmpty(task.getDescription())
                ? "Write what you plan to do for this challenge."
                : task.getDescription());
        SetReflectionAreaCompact(true);
        reflectionArea.setPromptText("Begin with what happened…");
        reflectionArea.setText(entry.getBody() == null ? "" : entry.getBody());
        reflectionArea.setDisable(true);
        SetEditButtonVisible(true);
        submitButton.setDisable(true);
        submitButton.setText("Submit");
        feedbackButton.setDisable(true);
        feedbackLabel.setText("Click Edit, to change what you wrote for this challenge");
        savedStatusLabel.setText("Saved");
        HideAiFeedback();
    }

    private void ShowWeeklyChallenge(ChallengeListItem item){
        selectedReflectionItem = null;
        selectedJournalChallenge = null;
        selectedWeeklyChallenge = item;

        ShowTaskContent();

        WeeklyTask weeklyTask = item.weeklyTask();
        Task task = item.task();
        boolean finished = IsFinished(weeklyTask.getStatus());

        taskEyebrowLabel.setText("WEEKLY CHALLENGE");
        taskTitleLabel.setText(task == null ? "Weekly challenge" : task.getName());
        descriptionLabel.setText(task == null || ApplicationManager.isEmpty(task.getDescription())
                ? "Complete this week's challenge, then write an honest reflection."
                : task.getDescription());
        SetReflectionAreaCompact(true);
        reflectionArea.setPromptText("Begin with what happened…");
        reflectionArea.setText(weeklyTask.getReflection() == null ? "" : weeklyTask.getReflection());
        reflectionArea.setDisable(finished);
        SetEditButtonVisible(finished);
        submitButton.setDisable(finished);
        savedStatusLabel.setText(finished ? "Saved" : "");
        boolean keepGenerating = generatingFeedback
                && selectedWeeklyChallenge != null
                && SameTask(selectedWeeklyChallenge.weeklyTask(), weeklyTask);
        feedbackButton.setDisable(keepGenerating);
        if (keepGenerating) {
            feedbackLabel.setText("Generating AI feedback...");
            ShowAiFeedback("Writing feedback from your reflection...");
        } else {
            feedbackLabel.setText(finished ? "Click Edit, to change the reflection you have written for this" : "");
            ShowStoredFeedback(weeklyTask);
        }
    }

    private void ShowEmptyDetail() {
        selectedWeeklyChallenge = null;
        selectedReflectionItem = null;
        selectedJournalChallenge = null;

        ShowEmptyState();

        reflectionArea.clear();
        reflectionArea.setDisable(true);
        SetEditButtonVisible(false);
        submitButton.setDisable(true);
        submitButton.setText("Submit");
        feedbackButton.setDisable(true);
        feedbackLabel.setText("");
        savedStatusLabel.setText("");
        HideAiFeedback();
    }

    /** Shows the centred "nothing selected" message and hides the editor/buttons. */
    private void ShowEmptyState() {
        emptyStateBox.setVisible(true);
        emptyStateBox.setManaged(true);
        taskContentBox.setVisible(false);
        taskContentBox.setManaged(false);
    }

    /** Shows the task header, editor, and action buttons. */
    private void ShowTaskContent() {
        emptyStateBox.setVisible(false);
        emptyStateBox.setManaged(false);
        taskContentBox.setVisible(true);
        taskContentBox.setManaged(true);
    }

    /** The Edit action only makes sense once something has been submitted. */
    private void SetEditButtonVisible(boolean visible) {
        editButton.setVisible(visible);
        editButton.setManaged(visible);
        editButton.setDisable(!visible);
    }

    @FXML
    private void OnBack() throws IOException {
        NavigationService.LoadScreen(ScreenEnum.DASHBOARD);
    }

    @FXML
    private void OnEditReflection() {
        reflectionArea.setDisable(false);
        submitButton.setDisable(false);
        SetEditButtonVisible(false);
        feedbackLabel.setText("Re-enter you reflection");
        savedStatusLabel.setText("");
    }

    @FXML
    private void OnSubmit() {
        String reflection = CurrentReflection();
        if (ApplicationManager.isEmpty(reflection)) {
            feedbackLabel.setText("Write a reflection before submitting.");
            return;
        }

        String email = ApplicationManager.CurrentAccount.getCurrentEmail();

        if (selectedWeeklyChallenge != null) {
            ChallengeListItem submitted = selectedWeeklyChallenge;
            WeeklyTask weeklyTask = submitted.weeklyTask();
            try {
                boolean alreadyFinished = IsFinished(weeklyTask.getStatus());
                WeeklyTaskService.UpdateGivenTaskToBeFinished(weeklyTask, reflection, email);
                RequestWeeklyAiFeedback(submitted, reflection, "Task submitted. Generating AI feedback...");
                LoadQuestChallenges();
                ReselectWeeklyChallenge(weeklyTask);

                if (!alreadyFinished){
                    MaybeRecordWeekCompletion(email);
                }
                if (alreadyFinished)
                    feedbackLabel.setText("Re-Submitted Reflection for this Weekly Task!");
                else
                    feedbackLabel.setText("Submitted!");
                savedStatusLabel.setText("Saved");
            } catch (Exception exception) {
                feedbackLabel.setText("Could not submit this task right now.");
            }
            return;
        }

        if (selectedJournalChallenge != null) {
            ChallengeListItem submitted = selectedJournalChallenge;
            JournalEntry entry = submitted.entry();
            try {
                String title = submitted.task() == null ? entry.getTitle() : submitted.task().getName();
                JournalEntryService.AddChallengeEntry(email, entry.getLabourId(), title, reflection);
                LoadQuestChallenges();
                ReselectJournalChallenge(entry.getLabourId());
                feedbackLabel.setText("Saved!");
                savedStatusLabel.setText("Saved");
            } catch (Exception exception) {
                feedbackLabel.setText("Could not save this challenge right now.");
            }
            return;
        }

        if (selectedReflectionItem != null) {
            ReflectionListItem submitted = selectedReflectionItem;
            try {
                UserQuest updated = UserQuestService.UpdateQuestReflectionToBeFinished(
                        submitted.userQuest(), reflection, email);
                RequestQuestAiFeedback(submitted, reflection, "Reflection submitted. Generating AI feedback...");
                LoadQuestReflections();
                ReselectReflection(updated);
                feedbackLabel.setText("Re-Submitted your Reflection!");
                savedStatusLabel.setText("Saved");
            } catch (Exception exception) {
                feedbackLabel.setText("Could not submit this reflection right now.");
            }
        }
    }

    /**
     * Once every weekly challenge assigned this week is finished, counts it as the
     * week's streak and nudges the active quest's completion percentage forward.
     */
    private void MaybeRecordWeekCompletion(String email) {
        try {
            int total = 0;
            int finished = 0;
            for (ChallengeListItem item : questChallengesList.getItems()) {
                if (item.weeklyTask() == null) {
                    continue;
                }
                total++;
                if (IsFinished(item.weeklyTask().getStatus())) {
                    finished++;
                }
            }

            if (total == 0 || finished != total) {
                return;
            }

            StreakService.RecordCompletion(email, LocalDate.now());
            UserQuest currentActiveQuest = UserQuestService.GetCurrentActiveUserQuestForEmail(email);

            UserQuest updatedQuest = UserQuestService.SetUserQuestToPercentageComplete(
                    currentActiveQuest, email, currentActiveQuest.getPercentageComplete() + 0.1f);

            if (updatedQuest.getPercentageComplete() >= 1.0f) {
                UserQuestService.SetUserQuestStatusAsComplete(updatedQuest, email);
                StreakService.IncreaseTotalQuestsCompleted(email);
            }
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
        }
    }

    @FXML
    private void OnGetFeedback() {
        String reflection = CurrentReflection();
        if (ApplicationManager.isEmpty(reflection)) {
            feedbackLabel.setText("Write a reflection before requesting AI feedback.");
            return;
        }

        if (selectedWeeklyChallenge != null) {
            RequestWeeklyAiFeedback(selectedWeeklyChallenge, reflection, "Generating AI feedback...");
            return;
        }

        if (selectedReflectionItem != null) {
            RequestQuestAiFeedback(selectedReflectionItem, reflection, "Generating AI feedback...");
        }
    }

    private void ReselectWeeklyChallenge(WeeklyTask weeklyTask) {
        for (ChallengeListItem item : questChallengesList.getItems()) {
            if (item.weeklyTask() != null && SameTask(item.weeklyTask(), weeklyTask)) {
                questChallengesList.getSelectionModel().select(item);
                return;
            }
        }
    }

    private void ReselectJournalChallenge(int labourId) {
        for (ChallengeListItem item : questChallengesList.getItems()) {
            if (item.weeklyTask() == null && item.entry() != null && item.entry().getLabourId() == labourId) {
                questChallengesList.getSelectionModel().select(item);
                return;
            }
        }
    }

    private void ReselectReflection(UserQuest userQuest) {
        for (ReflectionListItem item : questReflectionsList.getItems()) {
            if (item.userQuest().getLabourId() == userQuest.getLabourId()) {
                questReflectionsList.getSelectionModel().select(item);
                return;
            }
        }
    }

    private void RequestWeeklyAiFeedback(ChallengeListItem item, String reflection, String statusMessage) {
        WeeklyTask weeklyTask = item.weeklyTask();
        Task task = item.task();
        String questName = QuestNameForTask(task);

        int requestId = ++feedbackRequestId;
        generatingFeedback = true;
        feedbackButton.setDisable(true);
        feedbackLabel.setText(statusMessage);
        ShowAiFeedback("Writing feedback from your reflection...");

        CompletableFuture.supplyAsync(() -> {
            try {
                return ReflectionFeedbackService.FeedbackFor(
                        ReflectionFeedbackService.ContextFor(task, questName, reflection));
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }).whenComplete((text, error) -> Platform.runLater(() -> {
            if (requestId != feedbackRequestId) {
                return;
            }

            generatingFeedback = false;
            if (selectedWeeklyChallenge == null || !SameTask(selectedWeeklyChallenge.weeklyTask(), weeklyTask)) {
                feedbackButton.setDisable(selectedWeeklyChallenge == null);
                return;
            }

            if (error != null) {
                feedbackLabel.setText(FriendlyFeedbackError(error));
                feedbackButton.setDisable(false);
                return;
            }

            try {
                ReflectionFeedbackService.Save(weeklyTask, text);
            } catch (Exception ignored) {
                // Showing the reply still helps even if it cannot be stored.
            }
            ShowAiFeedback(text);
            feedbackLabel.setText("AI feedback is ready.");
            feedbackButton.setDisable(false);
        }));
    }

    private void RequestQuestAiFeedback(ReflectionListItem item, String reflection, String statusMessage) {
        UserQuest userQuest = item.userQuest();
        String questName = item.quest() == null ? "your current quest" : item.quest().getName();

        int requestId = ++feedbackRequestId;
        generatingFeedback = true;
        feedbackButton.setDisable(true);
        feedbackLabel.setText(statusMessage);
        ShowAiFeedback("Writing feedback from your reflection...");

        CompletableFuture.supplyAsync(() -> {
            try {
                return ReflectionFeedbackService.FeedbackFor(
                        ReflectionFeedbackService.ContextFor(null, questName, reflection));
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }).whenComplete((text, error) -> Platform.runLater(() -> {
            if (requestId != feedbackRequestId) {
                return;
            }

            generatingFeedback = false;
            if (selectedReflectionItem == null
                    || selectedReflectionItem.userQuest().getLabourId() != userQuest.getLabourId()) {
                feedbackButton.setDisable(selectedReflectionItem == null);
                return;
            }

            if (error != null) {
                feedbackLabel.setText(FriendlyFeedbackError(error));
                feedbackButton.setDisable(false);
                return;
            }

            try {
                ReflectionFeedbackService.SaveForQuest(userQuest.getAccountEmail(), userQuest.getLabourId(), text);
            } catch (Exception ignored) {
                // Showing the reply still helps even if it cannot be stored.
            }
            ShowAiFeedback(text);
            feedbackLabel.setText("AI feedback is ready.");
            feedbackButton.setDisable(false);
        }));
    }

    private String QuestNameForTask(Task task) {
        if (task == null) {
            return "your current quest";
        }
        try {
            Quest quest = QuestService.GetQuestForLabourId(task.getLabourId());
            if (quest != null && !ApplicationManager.isEmpty(quest.getName())) {
                return quest.getName();
            }
        } catch (Exception ignored) {
            // fall through to the default name below
        }
        return "your current quest";
    }

    private void ShowStoredFeedback(WeeklyTask weeklyTask) {
        try {
            String stored = ReflectionFeedbackService.Find(weeklyTask);
            if (ApplicationManager.isEmpty(stored)) {
                HideAiFeedback();
                return;
            }
            ShowAiFeedback(stored);
        } catch (Exception exception) {
            HideAiFeedback();
        }
    }

    private void ShowAiFeedback(String text) {
        aiFeedbackLabel.setText(text);
        aiFeedbackCard.setVisible(true);
        aiFeedbackCard.setManaged(true);
    }

    private void HideAiFeedback() {
        aiFeedbackLabel.setText("");
        aiFeedbackCard.setVisible(false);
        aiFeedbackCard.setManaged(false);
    }

    private void CancelPendingFeedback() {
        feedbackRequestId++;
        generatingFeedback = false;
    }

    private static String FriendlyFeedbackError(Throwable error) {
        Throwable cause = error;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        String message = cause.getMessage();
        if (!ApplicationManager.isEmpty(message)) {
            return message;
        }
        return "Could not get AI feedback right now.";
    }

    /** Shrinks the shared text area to a compact 2-line box for challenge entries,
     * or restores its normal expanding height for reflections. */
    private void SetReflectionAreaCompact(boolean compact) {
        reflectionArea.setPrefRowCount(compact ? 2 : 10);
        reflectionArea.setPrefHeight(compact ? Region.USE_COMPUTED_SIZE : 0);
        VBox.setVgrow(reflectionArea, compact ? Priority.NEVER : Priority.ALWAYS);
    }

    private String CurrentReflection() {
        String reflection = reflectionArea.getText();
        return reflection == null ? "" : reflection.trim();
    }

    private static boolean SameTask(WeeklyTask first, WeeklyTask second) {
        return first.getTaskId() == second.getTaskId()
                && Objects.equals(first.getWeekStarted(), second.getWeekStarted())
                && Objects.equals(first.getEmail(), second.getEmail());
    }

    private static boolean IsFinished(String status) {
        return "Finished".equalsIgnoreCase(status);
    }

    /** Strips a leading "Labour of the " from a quest name, e.g. "Labour of the Nemean Lion" -&gt; "The Nemean Lion". */
    private static String LabourSubject(String questName) {
        if (ApplicationManager.isEmpty(questName)) {
            return questName;
        }
        String prefix = "labour of the ";
        if (questName.toLowerCase().startsWith(prefix)) {
            return "The " + questName.substring(prefix.length());
        }
        return questName;
    }

    private void PopulateActiveQuestHeader(){
        String email = ApplicationManager.CurrentAccount.getCurrentEmail();
        try {
            UserQuest userQuest = UserQuestService.GetCurrentActiveUserQuestForEmail(email);
            if (userQuest == null) {
                questTitleLabel.setText("No active quest");
                questProgress.setProgress(0);
                questProgressLabel.setText("0% Complete");
                return;
            }
            Quest quest = QuestService.GetQuestForLabourId(userQuest.getLabourId());
            questTitleLabel.setText(LabourSubject(quest.getName()));
            questProgress.setProgress(userQuest.getPercentageComplete());
            questProgressLabel.setText(String.format("%.0f%% Complete", userQuest.getPercentageComplete() *100));
        } catch (Exception exception) {
            questTitleLabel.setText("No active quest");
            questProgress.setProgress(0);
            questProgressLabel.setText("0% Complete");
        }
    }

    /** Builds the gold tick/circle status indicator (display only, not clickable). */
    private static StackPane BuildStatusIcon() {
        Circle circle = new Circle(8);
        circle.getStyleClass().add("status-icon-circle");

        SVGPath tick = new SVGPath();
        tick.setContent(CHECKMARK_PATH);
        tick.getStyleClass().add("status-icon-tick");

        StackPane icon = new StackPane(circle, tick);
        icon.getStyleClass().add("journal-row-icon-box");
        return icon;
    }

    private static void SetStatusIconFinished(StackPane icon, boolean finished) {
        Circle circle = (Circle) icon.getChildren().get(0);
        SVGPath tick = (SVGPath) icon.getChildren().get(1);
        if (finished) {
            if (!circle.getStyleClass().contains("status-complete")) {
                circle.getStyleClass().add("status-complete");
            }
        } else {
            circle.getStyleClass().remove("status-complete");
        }
        tick.setVisible(finished);
        tick.setManaged(finished);
    }

    /** Clips a label's rendered text to two lines so long descriptions don't blow out row height. */
    private static void ClampToTwoLines(Label label) {
        label.setMinHeight(0);
        label.setPrefHeight(36);
        label.setMaxHeight(36);
        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(label.widthProperty());
        clip.setHeight(36);
        label.setClip(clip);
    }

    private void ConfigureQuestChallengesList() {
        questChallengesList.setPlaceholder(new Label("Add a challenge to your journal from the Quest Viewer"));
        questChallengesList.setCellFactory(list -> new ListCell<>() {
            private final StackPane statusIcon = BuildStatusIcon();
            private final Label eyebrowLabel = new Label();
            private final Label nameLabel = new Label();
            private final Label overviewLabel = new Label();
            private final VBox textBox = new VBox(2, eyebrowLabel, nameLabel, overviewLabel);
            private final HBox row = new HBox(10, statusIcon, textBox);

            {
                row.getStyleClass().add("journal-row");
                eyebrowLabel.getStyleClass().add("journal-row-eyebrow");
                nameLabel.getStyleClass().add("journal-row-title");
                overviewLabel.getStyleClass().add("journal-row-description");
                nameLabel.setWrapText(true);
                overviewLabel.setWrapText(true);
                HBox.setHgrow(textBox, Priority.ALWAYS);
                ClampToTwoLines(overviewLabel);
                row.maxWidthProperty().bind(questChallengesList.widthProperty().subtract(24));
            }

            @Override
            protected void updateItem(ChallengeListItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                boolean finished;
                if (item.weeklyTask() != null) {
                    eyebrowLabel.setText("WEEKLY");
                    nameLabel.setText(item.task() == null ? "Weekly challenge" : item.task().getName());
                    overviewLabel.setText(item.task() == null ? "" : item.task().getOverview());
                    finished = IsFinished(item.weeklyTask().getStatus());
                } else {
                    String subject = item.quest() == null ? "QUEST" : LabourSubject(item.quest().getName()).toUpperCase();
                    eyebrowLabel.setText(subject);
                    nameLabel.setText(item.task() == null ? item.entry().getTitle() : item.task().getName());
                    overviewLabel.setText(item.task() == null ? "" : item.task().getOverview());
                    finished = true;
                }
                SetStatusIconFinished(statusIcon, finished);
                setText(null);
                setGraphic(row);
            }
        });

        questChallengesList.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldItem, newItem) -> {
                    if (updatingJournalSelection || newItem == null) return;
                    CancelPendingFeedback();
                    updatingJournalSelection = true;
                    questReflectionsList.getSelectionModel().clearSelection();
                    updatingJournalSelection = false;
                    ShowJournalChallenge(newItem);
                });
    }

    private void LoadQuestChallenges(){
        questChallengesList.getItems().clear();
        String email = ApplicationManager.CurrentAccount.getCurrentEmail();
        if (ApplicationManager.isEmpty(email)) {
            UpdateChallengesCount();
            return;
        }

        try {
            for (JournalEntry entry : JournalEntryService.GetChallengesForEmail(email)) {
                Quest quest = QuestService.GetQuestForLabourId(entry.getLabourId());
                Task task = FindChallengeTask(email, entry.getLabourId());
                questChallengesList.getItems().add(new ChallengeListItem(entry, quest, task, null));
            }
        } catch (Exception exception) {
            System.err.println("Could not load quest challenges" + exception.getMessage());
        }

        try {
            WeeklyTask[] weekly = WeeklyTaskService.GetTasksForEmailForThisWeek(email);
            if (weekly == null) {
                weekly = WeeklyTaskService.GenerateTasksForThisWeek(email);
            }
            if (weekly != null) {
                for (WeeklyTask weeklyTask : weekly) {
                    Task task = TaskService.GetTaskForId(weeklyTask.getTaskId());
                    questChallengesList.getItems().add(new ChallengeListItem(null, null, task, weeklyTask));
                }
            }
        } catch (Exception exception) {
            System.err.println("Could not load weekly challenges" + exception.getMessage());
        }

        UpdateChallengesCount();
    }

    /** Updates the "x / y" count next to the Challenges heading. */
    private void UpdateChallengesCount() {
        int total = questChallengesList.getItems().size();
        int finished = 0;
        for (ChallengeListItem item : questChallengesList.getItems()) {
            boolean itemFinished = item.weeklyTask() == null || IsFinished(item.weeklyTask().getStatus());
            if (itemFinished) {
                finished++;
            }
        }
        questChallengesCountLabel.setText(finished + " / " + total);
    }

    private Task FindChallengeTask(String email, int labourId) {
        try {
            UserQuest userQuest = UserQuestService.GetUserQuestForEmailAndLabourId(email, labourId);
            if (userQuest == null || ApplicationManager.isEmpty(userQuest.getReactionType())) return null;
            List<Task> tasks = TaskService.GetTasksForLabourIdAndReactionType(labourId, userQuest.getReactionType());
            return tasks.isEmpty() ? null : tasks.get(0);
        } catch (Exception ignored) {
            return null;
        }
    }

    private void LoadQuestReflections(){
        questReflectionsList.getItems().clear();
        String email = ApplicationManager.CurrentAccount.getCurrentEmail();
        if (ApplicationManager.isEmpty(email)) {
            UpdateReflectionsCount();
            return;
        }

        try {
            for (UserQuest userQuest : UserQuestService.GetUserQuestsForEmail(email)) {
                if (ApplicationManager.isEmpty(userQuest.getReactionType())) continue; // quest not started yet
                Quest quest = QuestService.GetQuestForLabourId(userQuest.getLabourId());
                ReflectionPrompt prompt = FindReflectionPrompt(userQuest);
                questReflectionsList.getItems().add(new ReflectionListItem(userQuest, quest, prompt));
            }
            // TODO: append standalone journal entry
        } catch (Exception exception) {
            System.err.println("Could not load quest reflections" + exception.getMessage());
        }

        UpdateReflectionsCount();
    }

    /** Updates the "x / y" count next to the Reflections heading. */
    private void UpdateReflectionsCount() {
        int total = questReflectionsList.getItems().size();
        int finished = 0;
        for (ReflectionListItem item : questReflectionsList.getItems()) {
            if (IsFinished(item.userQuest().getReflectionStatus())) {
                finished++;
            }
        }
        questReflectionsCountLabel.setText(finished + " / " + total);
    }

    private ReflectionPrompt FindReflectionPrompt(UserQuest userQuest) {
        try {
            List<ReflectionPrompt> prompts = ReflectionPromptDAO.GetReflectionPromptsForLabourId(userQuest.getLabourId());
            for (ReflectionPrompt prompt : prompts) {
                if (userQuest.getReactionType().equalsIgnoreCase(prompt.reactionType())) {
                    return prompt;
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    @FXML
    private void OnWriteNewReflection(){
        questReflectionsList.getSelectionModel().clearSelection();
        questChallengesList.getSelectionModel().clearSelection();
        selectedWeeklyChallenge = null;
        selectedJournalChallenge = null;
        selectedReflectionItem = null;

        ShowTaskContent();
        taskEyebrowLabel.setText("NEW REFLECTION");
        taskTitleLabel.setText("Write a new reflection");
        descriptionLabel.setText("Not tied to a specific quest. Write freely about anything on your mind.");
        SetReflectionAreaCompact(false);
        reflectionArea.setPromptText("Begin with what happened…");
        reflectionArea.clear();
        reflectionArea.setDisable(false);
        SetEditButtonVisible(false);
        submitButton.setDisable(false);
        feedbackButton.setDisable(false);
        feedbackLabel.setText("");
        savedStatusLabel.setText("");
        HideAiFeedback();
        // Add full save to journal entries
    }

    private void ConfigureQuestReflectionsList(){
        questReflectionsList.setPlaceholder(new Label("No reflections yet"));
        questReflectionsList.setCellFactory(list -> new ListCell<>() {
            private final StackPane statusIcon = BuildStatusIcon();
            private final Label eyebrowLabel = new Label();
            private final Label nameLabel = new Label();
            private final Label overviewLabel = new Label();
            private final VBox textBox = new VBox(2, eyebrowLabel, nameLabel, overviewLabel);
            private final HBox row = new HBox(10, statusIcon, textBox);

            {
                row.getStyleClass().add("journal-row");
                eyebrowLabel.getStyleClass().add("journal-row-eyebrow");
                nameLabel.getStyleClass().add("journal-row-title");
                overviewLabel.getStyleClass().add("journal-row-description");
                nameLabel.setWrapText(true);
                overviewLabel.setWrapText(true);
                HBox.setHgrow(textBox, Priority.ALWAYS);
                ClampToTwoLines(overviewLabel);
                row.maxWidthProperty().bind(questReflectionsList.widthProperty().subtract(24));
            }

            @Override
            protected void updateItem(ReflectionListItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null){
                    setText(null);
                    setGraphic(null);
                    return;
                }
                String subject = item.quest() == null ? "REFLECTION" : LabourSubject(item.quest().getName()).toUpperCase();
                eyebrowLabel.setText(subject);
                nameLabel.setText(item.prompt() == null ? "Reflection" : item.prompt().name());
                overviewLabel.setText(item.prompt() == null ? "" : item.prompt().overview());
                SetStatusIconFinished(statusIcon, IsFinished(item.userQuest().getReflectionStatus()));
                setText(null);
                setGraphic(row);
            }
        });

        questReflectionsList.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldItem, newItem) -> {
                    if (updatingJournalSelection || newItem == null) return;
                    CancelPendingFeedback();
                    updatingJournalSelection = true;
                    questChallengesList.getSelectionModel().clearSelection();
                    updatingJournalSelection = false;
                    ShowReflection(newItem);
                });
    }

    private void ShowReflection(ReflectionListItem item){
        selectedWeeklyChallenge = null;
        selectedJournalChallenge = null;
        selectedReflectionItem = item;

        ShowTaskContent();

        boolean finished = IsFinished(item.userQuest().getReflectionStatus());

        taskEyebrowLabel.setText("REFLECTION");
        taskTitleLabel.setText(item.prompt() == null ? "Reflection" : item.prompt().name());
        descriptionLabel.setText(item.prompt() == null ? "Reflection prompt not available yet." : item.prompt().prompt());
        SetReflectionAreaCompact(false);
        reflectionArea.setPromptText(item.prompt() == null || ApplicationManager.isEmpty(item.prompt().overview())
                ? "Begin with what happened…"
                : item.prompt().overview());
        String answer = item.userQuest().getReflection();
        reflectionArea.setText(ApplicationManager.isEmpty(answer) ? "" : answer);
        reflectionArea.setDisable(finished);
        SetEditButtonVisible(finished);
        submitButton.setDisable(finished);
        savedStatusLabel.setText(finished ? "Saved" : "");

        boolean keepGenerating = generatingFeedback
                && selectedReflectionItem != null
                && selectedReflectionItem.userQuest().getLabourId() == item.userQuest().getLabourId();
        feedbackButton.setDisable(keepGenerating);
        if (keepGenerating) {
            feedbackLabel.setText("Generating AI feedback...");
            ShowAiFeedback("Writing feedback from your reflection...");
        } else {
            feedbackLabel.setText(finished ? "Click Edit, to change the reflection you have written" : "");
            ShowStoredQuestFeedback(item.userQuest());
        }
    }

    private void ShowStoredQuestFeedback(UserQuest userQuest) {
        try {
            String stored = ReflectionFeedbackService.FindForQuest(userQuest.getAccountEmail(), userQuest.getLabourId());
            if (ApplicationManager.isEmpty(stored)) {
                HideAiFeedback();
                return;
            }
            ShowAiFeedback(stored);
        } catch (Exception exception) {
            HideAiFeedback();
        }
    }

}

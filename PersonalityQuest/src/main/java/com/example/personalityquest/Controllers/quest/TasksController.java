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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

/** Controls weekly-task selection and completion */
public class TasksController implements Initializable {
    private static final double STACKED_BREAKPOINT = 760;
    private static final double FULL_PERCENT = 100;
    private static final double HIDDEN_PERCENT = 0;
    private static final DateTimeFormatter WEEK_FORMAT =
            DateTimeFormatter.ofPattern("d MMM yyyy", Locale.UK);

    @FXML
    private NavBarController navBarController;
    @FXML
    private BorderPane tasksRoot;
    @FXML
    private GridPane tasksGrid;
    @FXML
    private VBox listsColumn, detailCard;
    @FXML
    private Label weekSummaryLabel, weekRangeLabel, questTasksLabel, taskTitleLabel, questLabel,
            descriptionLabel, feedbackLabel, aiFeedbackLabel;
    @FXML
    private ListView<TaskListItem> weekTasks;
    @FXML
    private ListView<Task> questTasks;
    @FXML
    private CheckBox progressBox;
    @FXML
    private TextArea reflectionArea;
    @FXML
    private VBox aiFeedbackCard;
    @FXML
    private Button draftButton, submitButton, feedbackButton;
    @FXML
    private Label questTitleLabel, questProgressLabel;
    @FXML
    private ProgressBar questProgress;
    @FXML
    private ListView<ChallengeListItem> questChallengesList;
    @FXML
    private ListView<ReflectionListItem> questReflectionsList;

    private record ChallengeListItem(JournalEntry entry, Quest quest, Task task){}
    private record ReflectionListItem(UserQuest userQuest, Quest quest, ReflectionPrompt prompt){}
    private WeeklyTask taskToSelect;
    private boolean updatingSelection;
    private boolean generatingFeedback;
    private int feedbackRequestId;

    private boolean updatingJournalSelection;

    /** Initialises quest challenge/reflection controls and loads the active quest's data
     * @param location the location used to resolve relative paths
     * @param resources the localisation resources for the screen
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        navBarController.setCurrentDestination(NavBarController.NavDestination.TASKS);
        ConfigureQuestChallengesList();
        ConfigureQuestReflectionsList();
        PopulateActiveQuestHeader();
        LoadQuestChallenges();
        LoadQuestReflections();
    }

    /** Selects a weekly task for display
     * @param weeklyTask the task to select
     */
    public void selectTask(WeeklyTask weeklyTask) {
        taskToSelect = weeklyTask;
        SelectMatchingTask();
    }

    private void LoadTasks() {
        LoadWeeklyTasks();
        LoadQuestTasks();
        SelectInitialTask();
    }

    private void LoadWeeklyTasks() {
        weekTasks.getItems().clear();
        String email = ApplicationManager.CurrentAccount.getCurrentEmail();

        if (ApplicationManager.isEmpty(email)) {
            weekSummaryLabel.setText("Sign in to see this week's tasks.");
            weekRangeLabel.setText("Your weekly assignments will appear here.");
            return;
        }

        try {
            WeeklyTask[] weekly = WeeklyTaskService.GetTasksForEmailForThisWeek(email);
            if (weekly == null) {
                weekly = WeeklyTaskService.GenerateTasksForThisWeek(email);
            }

            if (weekly == null || weekly.length == 0) {
                weekSummaryLabel.setText("No weekly tasks have been assigned yet.");
                weekRangeLabel.setText("Weekly practices are assigned separately from the storyline.");
                return;
            }

            int finished = 0;
            for (WeeklyTask weeklyTask : weekly) {
                weekTasks.getItems().add(ToListItem(weeklyTask));
                if (IsFinished(weeklyTask.getStatus())) {
                    finished++;
                }
            }

            weekSummaryLabel.setText(finished + " of " + weekly.length + " finished this week.");
            weekRangeLabel.setText("Week of " + FormatWeek(weekly[0].getWeekStarted()));
        } catch (Exception exception) {
            weekSummaryLabel.setText("Could not load this week's tasks right now.");
        }
    }

    private void LoadQuestTasks() {
        questTasks.getItems().clear();
        String email = ApplicationManager.CurrentAccount.getCurrentEmail();

        if (ApplicationManager.isEmpty(email)) {
            questTasksLabel.setText("Sign in to see your current labour.");
            return;
        }

        try {
            UserQuest userQuest = UserQuestService.GetCurrentActiveUserQuestForEmail(email);
            if (userQuest == null) {
                questTasksLabel.setText("No active labour has been assigned yet.");
                return;
            }

            Quest quest = QuestService.GetQuestForLabourId(userQuest.getLabourId());
            List<Task> tasks = TaskService.GetTasksForLabourId(userQuest.getLabourId());
            questTasks.getItems().addAll(tasks);

            String questName = quest == null ? "your current labour" : quest.getName();
            questTasksLabel.setText(tasks.size() + " storyline tasks in " + questName + ".");
        } catch (Exception exception) {
            questTasksLabel.setText("Could not load this labour's tasks right now.");
        }
    }

    private void SelectInitialTask() {
        if (SelectMatchingTask()) {
            return;
        }
        if (!weekTasks.getItems().isEmpty()) {
            weekTasks.getSelectionModel().selectFirst();
            return;
        }
        if (!questTasks.getItems().isEmpty()) {
            questTasks.getSelectionModel().selectFirst();
            return;
        }
        ShowEmptyDetail("Choose a weekly or quest task to read its details.");
    }

    private TaskListItem ToListItem(WeeklyTask weeklyTask) {
        Task task = null;
        String questName = "your current quest";

        try {
            task = TaskService.GetTaskForId(weeklyTask.getTaskId());
            Quest quest = QuestService.GetQuestForLabourId(task.getLabourId());
            if (quest != null && !ApplicationManager.isEmpty(quest.getName())) {
                questName = quest.getName();
            }
        } catch (Exception exception) {
            questName = "your current quest";
        }

        return new TaskListItem(weeklyTask, task, questName);
    }

    private boolean SelectMatchingTask() {
        if (taskToSelect == null) {
            return false;
        }

        for (TaskListItem item : weekTasks.getItems()) {
            if (SameTask(item.weeklyTask(), taskToSelect)) {
                weekTasks.getSelectionModel().select(item);
                return true;
            }
        }
        return false;
    }

    private void ShowWeeklyTask(TaskListItem item) {
        if (item == null) {
            ShowEmptyDetail("Choose a weekly task to read its details and write a reflection.");
            return;
        }

        WeeklyTask weeklyTask = item.weeklyTask();
        Task task = item.task();
        boolean finished = IsFinished(weeklyTask.getStatus());

        taskTitleLabel.setText(item.displayName());
        questLabel.setText("Weekly task · " + item.questName());
        descriptionLabel.setText(task == null || ApplicationManager.isEmpty(task.getDescription())
                ? "Complete this week's challenge, then write an honest reflection."
                : task.getDescription());
        progressBox.setSelected(finished);
        reflectionArea.setText(weeklyTask.getReflection() == null ? "" : weeklyTask.getReflection());
        reflectionArea.setDisable(false);
        draftButton.setDisable(finished);
        submitButton.setDisable(finished);
        submitButton.setText(finished ? "Submitted" : "Submit");

        boolean keepGenerating = generatingFeedback
                && taskToSelect != null
                && SameTask(weeklyTask, taskToSelect);
        feedbackButton.setDisable(keepGenerating);
        if (keepGenerating) {
            feedbackLabel.setText("Generating AI feedback...");
            ShowAiFeedback("Writing feedback from your reflection...");
        } else {
            feedbackLabel.setText(finished ? "This task is already finished." : "");
            ShowStoredFeedback(weeklyTask);
        }
    }

    private void ShowQuestTask(Task task) {
        if (task == null) {
            ShowEmptyDetail("Choose a quest task to read its details.");
            return;
        }

        String questName = "your current labour";
        try {
            Quest quest = QuestService.GetQuestForLabourId(task.getLabourId());
            if (quest != null && !ApplicationManager.isEmpty(quest.getName())) {
                questName = quest.getName();
            }
        } catch (Exception exception) {
            questName = "your current labour";
        }

        taskTitleLabel.setText(task.getName());
        questLabel.setText("Quest task · " + questName);
        descriptionLabel.setText(ApplicationManager.isEmpty(task.getDescription())
                ? "This storyline task belongs to your current labour."
                : task.getDescription());
        progressBox.setSelected(false);
        reflectionArea.clear();
        reflectionArea.setDisable(true);
        draftButton.setDisable(true);
        submitButton.setDisable(true);
        submitButton.setText("Submit");
        feedbackButton.setDisable(true);
        feedbackLabel.setText("Reflections are submitted from weekly practices, not storyline quest tasks.");
        HideAiFeedback();
    }

    private void ShowJournalChallenge(ChallengeListItem item){
        JournalEntry entry = item.entry();
        String questName = item.quest() == null ? "Quest challenge" : item.quest().getName();
        taskTitleLabel.setText(item.task() == null ? entry.getTitle() : item.task().getName());
        questLabel.setText(questName);
        descriptionLabel.setText(entry.getBody());
        progressBox.setSelected(true);
        reflectionArea.clear();
        reflectionArea.setDisable(true);
        draftButton.setDisable(true);
        submitButton.setDisable(true);
        submitButton.setText("Submit");
        feedbackButton.setDisable(true);
        feedbackLabel.setText("");
        HideAiFeedback();
    }

    private void ShowEmptyDetail(String message) {
        taskTitleLabel.setText("Select a task");
        questLabel.setText(message);
        descriptionLabel.setText("");
        progressBox.setSelected(false);
        reflectionArea.clear();
        reflectionArea.setDisable(true);
        draftButton.setDisable(true);
        submitButton.setDisable(true);
        submitButton.setText("Submit");
        feedbackButton.setDisable(true);
        feedbackLabel.setText("");
        HideAiFeedback();
    }

    private void ConfigureWeeklyTaskList() {
        weekTasks.setPlaceholder(new Label("No weekly tasks assigned yet."));
        weekTasks.setCellFactory(list -> new ListCell<>() {
            private final CheckBox completedBox = new CheckBox();
            private final Label nameLabel = new Label();
            private final Label statusLabel = new Label();
            private final Region spacer = new Region();
            private final HBox header = new HBox(8, nameLabel, spacer, statusLabel);
            private final VBox textColumn = new VBox(2, header);
            private final HBox row = new HBox(10, completedBox, textColumn);

            {
                row.getStyleClass().add("week-task-row");
                nameLabel.getStyleClass().add("week-task-name");
                statusLabel.getStyleClass().add("week-task-status");
                HBox.setHgrow(spacer, Priority.ALWAYS);
                HBox.setHgrow(textColumn, Priority.ALWAYS);
                completedBox.setMouseTransparent(true);
                completedBox.setFocusTraversable(false);
            }

            @Override
            protected void updateItem(TaskListItem item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                nameLabel.setText(item.displayName());
                statusLabel.setText(DisplayStatus(item.weeklyTask().getStatus()));
                completedBox.setSelected(IsFinished(item.weeklyTask().getStatus()));
                setText(null);
                setGraphic(row);
            }
        });

        weekTasks.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldItem, newItem) -> {
                    if (updatingSelection || newItem == null) {
                        return;
                    }
                    if (oldItem != null && !SameTask(oldItem.weeklyTask(), newItem.weeklyTask())) {
                        CancelPendingFeedback();
                    }
                    updatingSelection = true;
                    questTasks.getSelectionModel().clearSelection();
                    updatingSelection = false;
                    ShowWeeklyTask(newItem);
                });
    }

    private void ConfigureQuestTaskList() {
        questTasks.setPlaceholder(new Label("No quest tasks assigned yet."));
        questTasks.setCellFactory(list -> new ListCell<>() {
            private final Label nameLabel = new Label();
            private final HBox row = new HBox(10, nameLabel);

            {
                row.getStyleClass().add("week-task-row");
                nameLabel.getStyleClass().add("week-task-name");
            }

            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);

                if (empty || task == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                nameLabel.setText(task.getName());
                setText(null);
                setGraphic(row);
            }
        });

        questTasks.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldItem, newItem) -> {
                    if (updatingSelection || newItem == null) {
                        return;
                    }
                    updatingSelection = true;
                    weekTasks.getSelectionModel().clearSelection();
                    updatingSelection = false;
                    CancelPendingFeedback();
                    ShowQuestTask(newItem);
                });
    }

    @FXML
    private void OnBack() throws IOException {
        NavigationService.LoadScreen(ScreenEnum.DASHBOARD);
    }

    @FXML
    private void OnSaveDraft() {
        TaskListItem selected = weekTasks.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        String reflection = CurrentReflection();
        if (ApplicationManager.isEmpty(reflection)) {
            feedbackLabel.setText("Write something before saving a draft.");
            return;
        }

        try {
            WeeklyTask updated = WeeklyTaskService.UpdateGivenTaskToDraft(
                    selected.weeklyTask(),
                    reflection,
                    ApplicationManager.CurrentAccount.getCurrentEmail());
            taskToSelect = updated;
            feedbackLabel.setText("Draft saved.");
            LoadTasks();
        } catch (Exception exception) {
            feedbackLabel.setText("Could not save this draft right now.");
        }
    }

    @FXML
    private void OnSubmit() {
        TaskListItem selected = weekTasks.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        String reflection = CurrentReflection();
        if (ApplicationManager.isEmpty(reflection)) {
            feedbackLabel.setText("Write a reflection before submitting.");
            return;
        }

        try {
            WeeklyTask updated = WeeklyTaskService.UpdateGivenTaskToBeFinished(
                    selected.weeklyTask(),
                    reflection,
                    ApplicationManager.CurrentAccount.getCurrentEmail());
            taskToSelect = updated;
            RequestAiFeedback(selected, reflection, "Task submitted. Generating AI feedback...");
            LoadTasks();
        } catch (Exception exception) {
            feedbackLabel.setText("Could not submit this task right now.");
        }

        try {
            int amountOfTasksAssigned = weekTasks.getItems().size();
            int count = 0;
            for (TaskListItem taskListItem : weekTasks.getItems()) {
                if (IsFinished(taskListItem.weeklyTask().getStatus())) {
                    count++;
                }
            }

            if (count != amountOfTasksAssigned) {
                throw new Exception("Week not finished");
            }

            StreakService.RecordCompletion(ApplicationManager.CurrentAccount.getCurrentEmail(), LocalDate.now());
            UserQuest currentActiveQuest = UserQuestService.GetCurrentActiveUserQuestForEmail(
                    ApplicationManager.CurrentAccount.getCurrentEmail());

            UserQuest updatedQuest = UserQuestService.SetUserQuestToPercentageComplete(
                    currentActiveQuest,
                    ApplicationManager.CurrentAccount.getCurrentEmail(),
                    currentActiveQuest.getPercentageComplete() + 0.1f);

            if (updatedQuest.getPercentageComplete() >= 1.0f) {
                UserQuestService.SetUserQuestStatusAsComplete(
                        updatedQuest, ApplicationManager.CurrentAccount.getCurrentEmail());
                StreakService.IncreaseTotalQuestsCompleted(ApplicationManager.CurrentAccount.getCurrentEmail());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void OnGetFeedback() {
        TaskListItem selected = weekTasks.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        String reflection = CurrentReflection();
        if (ApplicationManager.isEmpty(reflection)) {
            feedbackLabel.setText("Write a reflection before requesting AI feedback.");
            return;
        }

        taskToSelect = selected.weeklyTask();
        RequestAiFeedback(selected, reflection, "Generating AI feedback...");
    }

    private void RequestAiFeedback(TaskListItem item, String reflection, String statusMessage) {
        int requestId = ++feedbackRequestId;
        generatingFeedback = true;
        feedbackButton.setDisable(true);
        feedbackLabel.setText(statusMessage);
        ShowAiFeedback("Writing feedback from your reflection...");

        CompletableFuture.supplyAsync(() -> {
            try {
                return ReflectionFeedbackService.FeedbackFor(
                        ReflectionFeedbackService.ContextFor(item.task(), item.questName(), reflection));
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }).whenComplete((text, error) -> Platform.runLater(() -> {
            if (requestId != feedbackRequestId) {
                return;
            }

            generatingFeedback = false;
            TaskListItem current = weekTasks.getSelectionModel().getSelectedItem();
            if (current == null || !SameTask(current.weeklyTask(), item.weeklyTask())) {
                feedbackButton.setDisable(current == null);
                return;
            }

            if (error != null) {
                feedbackLabel.setText(FriendlyFeedbackError(error));
                feedbackButton.setDisable(false);
                return;
            }

            try {
                ReflectionFeedbackService.Save(current.weeklyTask(), text);
            } catch (Exception ignored) {
                // Showing the reply still helps even if it cannot be stored.
            }
            ShowAiFeedback(text);
            feedbackLabel.setText("AI feedback is ready.");
            feedbackButton.setDisable(false);
        }));
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

    private String CurrentReflection() {
        String reflection = reflectionArea.getText();
        return reflection == null ? "" : reflection.trim();
    }

    private void ApplyResponsiveLayout(double width) {
        if (width <= 0) {
            return;
        }

        boolean stacked = width < STACKED_BREAKPOINT;
        if (stacked) {
            GridPane.setColumnIndex(listsColumn, 0);
            GridPane.setRowIndex(listsColumn, 0);
            GridPane.setColumnIndex(detailCard, 0);
            GridPane.setRowIndex(detailCard, 1);
            tasksGrid.getColumnConstraints().get(0).setPercentWidth(FULL_PERCENT);
            tasksGrid.getColumnConstraints().get(1).setPercentWidth(HIDDEN_PERCENT);
        } else {
            GridPane.setColumnIndex(listsColumn, 0);
            GridPane.setRowIndex(listsColumn, 0);
            GridPane.setColumnIndex(detailCard, 1);
            GridPane.setRowIndex(detailCard, 0);
            tasksGrid.getColumnConstraints().get(0).setPercentWidth(38);
            tasksGrid.getColumnConstraints().get(1).setPercentWidth(62);
        }
    }

    private static boolean SameTask(WeeklyTask first, WeeklyTask second) {
        return first.getTaskId() == second.getTaskId()
                && Objects.equals(first.getWeekStarted(), second.getWeekStarted())
                && Objects.equals(first.getEmail(), second.getEmail());
    }

    private static boolean IsFinished(String status) {
        return "Finished".equalsIgnoreCase(status);
    }

    private static String DisplayStatus(String status) {
        if (IsFinished(status)) {
            return "Finished";
        }
        if ("Started".equalsIgnoreCase(status)) {
            return "Draft";
        }
        return "Not started";
    }

    private static String FormatWeek(String weekStarted) {
        try {
            return LocalDate.parse(weekStarted).format(WEEK_FORMAT);
        } catch (Exception exception) {
            return weekStarted == null ? "this week" : weekStarted;
        }
    }

    private record TaskListItem(WeeklyTask weeklyTask, Task task, String questName) {
        private String displayName() {
            if (task != null && !ApplicationManager.isEmpty(task.getName())) {
                return task.getName();
            }
            return "Weekly task";
        }
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
            String archetypeName = QuestService.GetArchetypeName(quest.getArchetypeId());
            questTitleLabel.setText(quest.getName() + " (" +archetypeName + ")");
            questProgress.setProgress(userQuest.getPercentageComplete());
            questProgressLabel.setText(String.format("%.0f%% Complete", userQuest.getPercentageComplete() *100));
        } catch (Exception exception) {
            questTitleLabel.setText("No active quest");
            questProgress.setProgress(0);
            questProgressLabel.setText("0% Complete");
        }
    }
    private void ConfigureQuestChallengesList() {
        questChallengesList.setPlaceholder(new Label("Add a challenge to your journal from the Quest Viewer"));
        questChallengesList.setCellFactory(list -> new ListCell<>() {
            private final CheckBox doneBox = new CheckBox();
            private final Region spacer = new Region();
            private final Label nameLabel = new Label();
            private final HBox header = new HBox(8, nameLabel, spacer, doneBox);
            private final Label overviewLabel = new Label();
            private final VBox row = new VBox(4, header, overviewLabel);

            {
                row.getStyleClass().add("labour-task-row");
                nameLabel.getStyleClass().add("labour-task-name");
                overviewLabel.getStyleClass().add("labour-task-detail");
                nameLabel.setWrapText(true);
                overviewLabel.setWrapText(true);
                HBox.setHgrow(nameLabel, Priority.ALWAYS);
                doneBox.setMouseTransparent(true);
                doneBox.setFocusTraversable(false);
                doneBox.getStyleClass().add("labour-task-check");
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
                nameLabel.setText(item.task() == null ? item.entry().getTitle() : item.task().getName());
                overviewLabel.setText(item.task() == null ? "" : item.task().getOverview());
                doneBox.setSelected(true);
                setText(null);
                setGraphic(row);
            }
        });

        questChallengesList.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldItem, newItem) -> {
                    if (updatingJournalSelection || newItem == null) return;
                    updatingJournalSelection = true;
                    questReflectionsList.getSelectionModel().clearSelection();
                    updatingJournalSelection = false;
                    ShowJournalChallenge(newItem);
                });
    }

    private void LoadQuestChallenges(){
        questChallengesList.getItems().clear();
        String email = ApplicationManager.CurrentAccount.getCurrentEmail();
        if (ApplicationManager.isEmpty(email)) return;

        try {
            for (JournalEntry entry : JournalEntryService.GetChallengesForEmail(email)) {
                Quest quest = QuestService.GetQuestForLabourId(entry.getLabourId());
                Task task = FindChallengeTask(email, entry.getLabourId());
                questChallengesList.getItems().add(new ChallengeListItem(entry, quest, task));
            }
        } catch (Exception exception) {
            System.err.println("Could not load quest challenges" + exception.getMessage());
        }
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
        if (ApplicationManager.isEmpty(email)) return;

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
        ShowEmptyDetail("Write a new refleciton, not tied to a specific quest.");
        reflectionArea.setDisable(false);
        // Add full save to journal entries
    }


    private void ConfigureQuestReflectionsList(){
        questReflectionsList.setPlaceholder(new Label("No reflections yet"));
        questReflectionsList.setCellFactory(list -> new ListCell<>() {
            private final CheckBox doneBox = new CheckBox();
            private final Region spacer = new Region();
            private final Label nameLabel = new Label();
            private final HBox header = new HBox(8, nameLabel, spacer, doneBox);
            private final Label overviewLabel = new Label();
            private final VBox row = new VBox(4, header, overviewLabel);

            {
                row.getStyleClass().add("labour-task-row");
                nameLabel.getStyleClass().add("labour-task-name");
                overviewLabel.getStyleClass().add("labour-task-detail");
                nameLabel.setWrapText(true);
                overviewLabel.setWrapText(true);
                HBox.setHgrow(nameLabel, Priority.ALWAYS);
                doneBox.setMouseTransparent(true);
                doneBox.setFocusTraversable(false);
                doneBox.getStyleClass().add("labour-task-check");
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
                nameLabel.setText(item.prompt() == null ? "Reflection" : item.prompt().name());
                overviewLabel.setText(item.prompt() == null ? "" : item.prompt().overview());
                doneBox.setSelected("Finished".equalsIgnoreCase(item.userQuest().getReflectionStatus()));
                setText(null);
                setGraphic(row);
            }
        });

        questReflectionsList.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldItem, newItem) -> {
                    if (updatingJournalSelection || newItem == null) return;
                    updatingJournalSelection = true;
                    questChallengesList.getSelectionModel().clearSelection();
                    updatingJournalSelection = false;
                    ShowReflection(newItem);
                });
    }

private void ShowReflection(TasksController.ReflectionListItem item){
    String questName = item.quest() == null ? "General" : item.quest().getName();
    taskTitleLabel.setText(item.prompt() == null ? "Reflection" : item.prompt().name());
    questLabel.setText("Quest reflection · " + questName);
    descriptionLabel.setText(item.prompt() == null ? "Reflection prompt not available yet." : item.prompt().prompt());
    progressBox.setSelected("Finished".equalsIgnoreCase(item.userQuest().getReflectionStatus()));
    String answer = item.userQuest().getReflection();
    reflectionArea.setText(ApplicationManager.isEmpty(answer) ? "" : answer);
    reflectionArea.setDisable(false);
}



}

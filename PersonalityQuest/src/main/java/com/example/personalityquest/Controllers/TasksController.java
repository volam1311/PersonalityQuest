package com.example.personalityquest.Controllers;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.Applications.DashboardApplication;
import com.example.personalityquest.Model.Quest;
import com.example.personalityquest.Model.Task;
import com.example.personalityquest.Model.WeeklyTask;
import com.example.personalityquest.ScreenEnum;
import com.example.personalityquest.Services.NavigationService;
import com.example.personalityquest.Services.QuestService;
import com.example.personalityquest.Services.TaskService;
import com.example.personalityquest.Services.WeeklyTaskService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

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
    private VBox detailCard;
    @FXML
    private Label weekSummaryLabel, weekRangeLabel, taskTitleLabel, questLabel,
            descriptionLabel, feedbackLabel;
    @FXML
    private ListView<TaskListItem> weekTasks;
    @FXML
    private CheckBox progressBox;
    @FXML
    private TextArea reflectionArea;
    @FXML
    private Button draftButton, submitButton;

    private WeeklyTask taskToSelect;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        navBarController.setCurrentDestination(NavBarController.NavDestination.TASKS);
        ConfigureTaskList();
        LoadWeeklyTasks();

        tasksRoot.widthProperty().addListener((observable, oldWidth, newWidth) ->
                ApplyResponsiveLayout(newWidth.doubleValue()));

        Platform.runLater(() -> ApplyResponsiveLayout(tasksRoot.getWidth()));
    }

    public void selectTask(WeeklyTask weeklyTask) {
        taskToSelect = weeklyTask;
        SelectMatchingTask();
    }

    private void LoadWeeklyTasks() {
        weekTasks.getItems().clear();
        String email = ApplicationManager.CurrentAccount.getCurrentEmail();

        if (ApplicationManager.isEmpty(email)) {
            weekSummaryLabel.setText("Sign in to see this week's tasks.");
            ShowEmptyDetail("Sign in to write a reflection.");
            return;
        }

        try {
            WeeklyTask[] weekly = WeeklyTaskService.GetTasksForEmailForThisWeek(email);
            if (weekly == null) {
                weekly = WeeklyTaskService.GenerateTasksForThisWeek(email);
            }

            if (weekly == null || weekly.length == 0) {
                weekSummaryLabel.setText("No weekly tasks have been assigned yet.");
                weekRangeLabel.setText("Tasks are generated from your active quest.");
                ShowEmptyDetail("No weekly tasks have been assigned yet.");
                return;
            }

            int finished = 0;
            for (WeeklyTask weeklyTask : weekly) {
                TaskListItem item = ToListItem(weeklyTask);
                weekTasks.getItems().add(item);
                if (IsFinished(weeklyTask.getStatus())) {
                    finished++;
                }
            }

            weekSummaryLabel.setText(finished + " of " + weekly.length + " finished this week.");
            weekRangeLabel.setText("Week of " + FormatWeek(weekly[0].getWeekStarted()));

            if (!SelectMatchingTask() && !weekTasks.getItems().isEmpty()) {
                weekTasks.getSelectionModel().selectFirst();
            }
        } catch (Exception exception) {
            weekSummaryLabel.setText("Could not load this week's tasks right now.");
            ShowEmptyDetail("Could not load this week's tasks right now.");
        }
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

    private void ShowTask(TaskListItem item) {
        if (item == null) {
            ShowEmptyDetail("Choose a weekly task to read its details and write a reflection.");
            return;
        }

        WeeklyTask weeklyTask = item.weeklyTask();
        Task task = item.task();
        boolean finished = IsFinished(weeklyTask.getStatus());

        taskTitleLabel.setText(item.displayName());
        questLabel.setText("This task belongs to " + item.questName());
        descriptionLabel.setText(task == null || ApplicationManager.isEmpty(task.getDescription())
                ? "Complete this week's challenge, then write an honest reflection."
                : task.getDescription());
        progressBox.setSelected(finished);
        reflectionArea.setText(weeklyTask.getReflection() == null ? "" : weeklyTask.getReflection());
        reflectionArea.setDisable(false);
        draftButton.setDisable(finished);
        submitButton.setDisable(finished);
        submitButton.setText(finished ? "Submitted" : "Submit");
        feedbackLabel.setText(finished ? "This task is already finished." : "");
    }

    private void ShowEmptyDetail(String message) {
        taskTitleLabel.setText("Select a task");
        questLabel.setText(message);
        descriptionLabel.setText("");
        progressBox.setSelected(false);
        reflectionArea.clear();
        draftButton.setDisable(true);
        submitButton.setDisable(true);
        submitButton.setText("Submit");
        feedbackLabel.setText("");
    }

    private void ConfigureTaskList() {
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
                (observable, oldItem, newItem) -> ShowTask(newItem));
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
            LoadWeeklyTasks();
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
            LoadWeeklyTasks();
            feedbackLabel.setText("Task submitted.");
        } catch (Exception exception) {
            feedbackLabel.setText("Could not submit this task right now.");
        }
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
            GridPane.setColumnIndex(detailCard, 0);
            GridPane.setRowIndex(detailCard, 1);
            tasksGrid.getColumnConstraints().get(0).setPercentWidth(FULL_PERCENT);
            tasksGrid.getColumnConstraints().get(1).setPercentWidth(HIDDEN_PERCENT);
        } else {
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
}

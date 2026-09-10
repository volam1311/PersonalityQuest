package com.example.personalityquest.Controllers;

import com.example.personalityquest.Applications.SettingsApplication;
import com.example.personalityquest.Applications.WeeklyTaskReflectionApplication;
import com.example.personalityquest.Model.EmailDetails;
import com.example.personalityquest.Model.Quest;
import com.example.personalityquest.Model.UserQuest;
import com.example.personalityquest.Model.WeeklyTask;
import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.Services.EmailService;
import com.example.personalityquest.Services.QuestService;
import com.example.personalityquest.Services.UserQuestService;
import com.example.personalityquest.Services.WeeklyTaskService;
import com.example.personalityquest.StreakManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    private static final double SIDEBAR_BREAKPOINT = 900;
    private static final double STACKED_BREAKPOINT = 760;
    private static final double EXPANDED_SIDEBAR_WIDTH = 250;
    private static final double COLLAPSED_SIDEBAR_WIDTH = 72;
    private static final double NAV_BUTTON_HEIGHT = 52;
    private static final double PROFILE_BUTTON_HEIGHT = 44;
    private static final double COMPACT_BUTTON_SIZE = 44;
    private static final double EXPANDED_BUTTON_PREF_WIDTH = 9999;
    private static final double FULL_PERCENT = 100;
    private static final double HIDDEN_PERCENT = 0;
    private static final double QUEST_CARD_PERCENT = 65;
    private static final double DAILIES_CARD_PERCENT = 35;
    private static final double TASKS_CARD_PERCENT = 36;
    private static final double PROGRESS_CARD_PERCENT = 64;
    private static final double DEFAULT_QUEST_PROGRESS = 0.2;
    private static final String DEFAULT_QUEST_PROGRESS_LABEL = "20% Complete";
    private static final int RADAR_AXIS_COUNT = 5;
    private static final int RADAR_LEVEL_COUNT = 5;
    private static final double RADAR_CENTER_Y_OFFSET = 8;
    private static final double RADAR_RADIUS_RATIO = 0.36;
    private static final double[] DEFAULT_RADAR_VALUES = {0.76, 0.52, 0.38, 0.65, 0.48};

    @FXML
    private BorderPane dashboardRoot;
    @FXML
    private VBox sidebar;
    @FXML
    private Label brandLabel, welcomeMessage, streakLabel;
    @FXML
    private Label homeNavLabel, questsNavLabel, tasksNavLabel, archetypeNavLabel,
            profileNameLabel;
    @FXML
    private Button menuButton, homeButton, questsButton,
            tasksButton, archetypeButton, profileButton, settingsButton;
    @FXML
    private GridPane topGrid, lowerGrid;
    @FXML
    private VBox dailiesCard, progressCard;
    @FXML
    private ListView<WeeklyTask> weeklyTasks, dailiesList;
    @FXML
    private ProgressBar questProgress;
    @FXML
    private Label questTitleLabel, questProgressLabel;
    @FXML
    private Canvas progressChart;

    private boolean sidebarExpanded = true;
    private boolean sidebarOverride;
    private String profileText = "Profile";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ConfigureTaskList(weeklyTasks);
        ConfigureTaskList(dailiesList);
        dailiesList.setItems(weeklyTasks.getItems());

        dashboardRoot.widthProperty().addListener((observable, oldWidth, newWidth) ->
                ApplyResponsiveLayout(newWidth.doubleValue()));

        try {
            SetWelcome();
            UpdateStreakLabel();
        } catch (Exception exception) {
            welcomeMessage.setText("Welcome back!");
            streakLabel.setText("Day 0");
        }

        PopulateQuestline();

        try {
            PopulateWeeklyTasks();
        } catch (Exception exception) {
            System.err.println("Could not load weekly tasks: " + exception.getMessage());
        }

        Platform.runLater(() -> {
            ApplyResponsiveLayout(dashboardRoot.getWidth());
            DrawProgressGraph();
        });
    }

    private void SetWelcome() throws Exception {
        // Load the signed-in user's name for the header and profile button.
        EmailDetails emailDetails = EmailService.GetDetailsForEmail(
                ApplicationManager.CurrentAccount.getCurrentEmail());

        if (emailDetails == null) {
            welcomeMessage.setText("Welcome back!");
            profileText = "Profile";
            profileNameLabel.setText(profileText);
            return;
        }

        welcomeMessage.setText("Welcome back, " + emailDetails.getFirstName() + "!");
        profileText = emailDetails.getUserName();
        profileNameLabel.setText(profileText);
    }

    private void UpdateStreakLabel() {
        // Display the saved streak without preventing the dashboard from loading.
        try {
            int streak = StreakManager.GetCurrentStreak(
                    ApplicationManager.CurrentAccount.getCurrentEmail());
            streakLabel.setText("Day " + streak);
        } catch (Exception exception) {
            streakLabel.setText("Day 0");
        }
    }

    private void PopulateQuestline() {
        try{
            UserQuest userQuest = UserQuestService.GetCurrentActiveUserQuestForEmail(ApplicationManager.CurrentAccount.getCurrentEmail());
            Quest trueQuest = QuestService.GetQuestForLabourId(userQuest.getLabourId());

            float truePercentageComplete = userQuest.getPercentageComplete() * 100;
            String formatedPercentageString = String.format("%.0f", truePercentageComplete);
            questTitleLabel.setText(trueQuest.getName());
            questProgress.setProgress(userQuest.getPercentageComplete());
            questProgressLabel.setText(formatedPercentageString + "% Complete");
        }
        catch (Exception e){
            questProgress.setProgress(DEFAULT_QUEST_PROGRESS);
            questProgressLabel.setText(DEFAULT_QUEST_PROGRESS_LABEL);
        }
    }

    private void PopulateWeeklyTasks() throws Exception {
        // Load this week's tasks or create them when none exist
        WeeklyTask[] tasks = WeeklyTaskService.GetTasksForEmailForThisWeek(
                ApplicationManager.CurrentAccount.getCurrentEmail());

        if (tasks == null) {
            tasks = WeeklyTaskService.GenerateTasksForThisWeek(
                    ApplicationManager.CurrentAccount.getCurrentEmail());
        }

        weeklyTasks.getItems().clear();

        if (tasks != null) {
            weeklyTasks.getItems().addAll(tasks);
        }
    }

    private void ConfigureTaskList(ListView<WeeklyTask> taskList) {
        // Render each task with a checkbox and a text label.
        taskList.setCellFactory(list -> new ListCell<>() {
            private final CheckBox completedBox = new CheckBox();
            private final Label taskLabel = new Label();
            private final HBox row = new HBox(10, completedBox, taskLabel);

            {
                row.getStyleClass().add("task-row");
                completedBox.setMouseTransparent(true);
                completedBox.setFocusTraversable(false);
            }

            @Override
            protected void updateItem(WeeklyTask task, boolean empty) {
                super.updateItem(task, empty);

                if (empty || task == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                taskLabel.setText(task.toString());
                completedBox.setSelected("Finished".equalsIgnoreCase(task.getStatus()));
                setText(null);
                setGraphic(row);
            }
        });
    }

    private void ApplyResponsiveLayout(double width) {
        // Change the sidebar and card arrangement as the window width changes.
        if (width <= 0) {
            return;
        }

        if (width >= SIDEBAR_BREAKPOINT) {
            sidebarOverride = false;
        }

        if (!sidebarOverride) {
            sidebarExpanded = width >= SIDEBAR_BREAKPOINT;
        }

        SetSidebarExpanded(sidebarExpanded);

        boolean stacked = width < STACKED_BREAKPOINT;
        SetGridCardLayout(topGrid, dailiesCard, stacked);
        SetGridCardLayout(lowerGrid, progressCard, stacked);
    }

    private void SetSidebarExpanded(boolean expanded) {
        // Apply the expanded or collapsed sidebar state.
        double width = expanded ? EXPANDED_SIDEBAR_WIDTH : COLLAPSED_SIDEBAR_WIDTH;
        sidebar.setMinWidth(width);
        sidebar.setPrefWidth(width);
        sidebar.setMaxWidth(width);

        sidebar.getStyleClass().remove("sidebar-collapsed");
        if (!expanded) {
            sidebar.getStyleClass().add("sidebar-collapsed");
        }

        brandLabel.setManaged(expanded);
        brandLabel.setVisible(expanded);

        SetLabelVisible(homeNavLabel, expanded);
        SetLabelVisible(questsNavLabel, expanded);
        SetLabelVisible(tasksNavLabel, expanded);
        SetLabelVisible(archetypeNavLabel, expanded);
        SetLabelVisible(profileNameLabel, expanded);

        SetButtonDimensions(homeButton, expanded, NAV_BUTTON_HEIGHT);
        SetButtonDimensions(questsButton, expanded, NAV_BUTTON_HEIGHT);
        SetButtonDimensions(tasksButton, expanded, NAV_BUTTON_HEIGHT);
        SetButtonDimensions(archetypeButton, expanded, NAV_BUTTON_HEIGHT);
        SetButtonDimensions(profileButton, expanded, PROFILE_BUTTON_HEIGHT);
        SetButtonDimensions(menuButton, false, COMPACT_BUTTON_SIZE);
        SetButtonDimensions(settingsButton, false, COMPACT_BUTTON_SIZE);
    }

    private void SetButtonDimensions(Button button, boolean expanded, double expandedHeight) {
        boolean square = !expanded;
        double width = square ? COMPACT_BUTTON_SIZE : EXPANDED_BUTTON_PREF_WIDTH;
        double height = square ? COMPACT_BUTTON_SIZE : expandedHeight;

        button.setMinWidth(square ? COMPACT_BUTTON_SIZE : 0);
        button.setPrefWidth(width);
        button.setMaxWidth(square ? COMPACT_BUTTON_SIZE : Double.MAX_VALUE);
        button.setMinHeight(height);
        button.setPrefHeight(height);
        button.setMaxHeight(height);
        button.setPadding(square ? Insets.EMPTY : new Insets(0, 12, 0, 12));
        button.setAlignment(square ? Pos.CENTER : Pos.CENTER_LEFT);
    }

    private void SetLabelVisible(Label label, boolean visible) {
        label.setManaged(visible);
        label.setVisible(visible);
    }

    private void SetGridCardLayout(GridPane grid, VBox secondaryCard, boolean stacked) {
        if (stacked) {
            GridPane.setColumnIndex(secondaryCard, 0);
            GridPane.setRowIndex(secondaryCard, 1);
            grid.getColumnConstraints().get(0).setPercentWidth(FULL_PERCENT);
            grid.getColumnConstraints().get(1).setPercentWidth(HIDDEN_PERCENT);
        } else {
            GridPane.setColumnIndex(secondaryCard, 1);
            GridPane.setRowIndex(secondaryCard, 0);
            grid.getColumnConstraints().get(0).setPercentWidth(
                    grid == topGrid ? QUEST_CARD_PERCENT : TASKS_CARD_PERCENT);
            grid.getColumnConstraints().get(1).setPercentWidth(
                    grid == topGrid ? DAILIES_CARD_PERCENT : PROGRESS_CARD_PERCENT);
        }
    }

    private void DrawProgressGraph() {
        // Draw the radar chart PLACEHOLDER RIGHT NOW
        GraphicsContext graphics = progressChart.getGraphicsContext2D();
        double width = progressChart.getWidth();
        double height = progressChart.getHeight();
        double centerX = width / 2;
        double centerY = height / 2 + RADAR_CENTER_Y_OFFSET;
        double radius = Math.min(width, height) * RADAR_RADIUS_RATIO;
        int axes = RADAR_AXIS_COUNT;

        graphics.clearRect(0, 0, width, height);
        graphics.setLineWidth(1);
        graphics.setStroke(Color.web("#777777"));

        for (int level = 1; level <= RADAR_LEVEL_COUNT; level++) {
            double levelRadius = radius * level / RADAR_LEVEL_COUNT;
            double[] xPoints = new double[axes];
            double[] yPoints = new double[axes];

            for (int axis = 0; axis < axes; axis++) {
                xPoints[axis] = PointX(centerX, levelRadius, axis, axes);
                yPoints[axis] = PointY(centerY, levelRadius, axis, axes);
            }

            graphics.strokePolygon(xPoints, yPoints, axes);
        }

        for (int axis = 0; axis < axes; axis++) {
            graphics.strokeLine(centerX, centerY,
                    PointX(centerX, radius, axis, axes),
                    PointY(centerY, radius, axis, axes));
        }

        double[] values = DEFAULT_RADAR_VALUES;
        double[] xPoints = new double[axes];
        double[] yPoints = new double[axes];

        for (int axis = 0; axis < axes; axis++) {
            xPoints[axis] = PointX(centerX, radius * values[axis], axis, axes);
            yPoints[axis] = PointY(centerY, radius * values[axis], axis, axes);
        }

        graphics.setFill(Color.rgb(46, 135, 207, 0.45));
        graphics.fillPolygon(xPoints, yPoints, axes);
        graphics.setStroke(Color.web("#43a9f2"));
        graphics.setLineWidth(2);
        graphics.strokePolygon(xPoints, yPoints, axes);
    }

    private double PointX(double centerX, double radius, int axis, int axes) {
        return centerX + radius * Math.cos(-Math.PI / 2 + axis * 2 * Math.PI / axes);
    }

    private double PointY(double centerY, double radius, int axis, int axes) {
        return centerY + radius * Math.sin(-Math.PI / 2 + axis * 2 * Math.PI / axes);
    }

    @FXML
    private void OnMenuToggle() {
        sidebarOverride = true;
        sidebarExpanded = !sidebarExpanded;
        SetSidebarExpanded(sidebarExpanded);
    }

    @FXML
    private void OnNavigationClick(ActionEvent event) throws IOException {
        Button button = (Button) event.getSource();

        if (button == settingsButton) {
            SettingsApplication.launch((Stage) dashboardRoot.getScene().getWindow());
            return;
        }

        System.out.println("Selected dashboard navigation: " + button.getId());
    }

    @FXML
    private void OnTaskClick(MouseEvent event) throws IOException {
        WeeklyTask selectedTask = dailiesList.getSelectionModel().getSelectedItem();

        if (selectedTask != null) {
            WeeklyTaskReflectionApplication.launch(
                    (Stage) dailiesList.getScene().getWindow(), selectedTask);
        }
    }
}

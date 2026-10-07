package com.example.personalityquest.Controllers.navigation;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.Model.auth.EmailDetails;
import com.example.personalityquest.Model.quest.Quest;
import com.example.personalityquest.Model.quest.Task;
import com.example.personalityquest.Model.quest.UserQuest;
import com.example.personalityquest.Model.quest.WeeklyTask;
import com.example.personalityquest.Model.quiz.Archetype;
import com.example.personalityquest.Model.quiz.QuizResult;
import com.example.personalityquest.ScreenEnum;
import com.example.personalityquest.Services.auth.EmailService;
import com.example.personalityquest.Services.navigation.NavigationService;
import com.example.personalityquest.Services.profile.StreakService;
import com.example.personalityquest.Services.quest.JournalEntryService;
import com.example.personalityquest.Services.quest.QuestService;
import com.example.personalityquest.Services.quest.TaskService;
import com.example.personalityquest.Services.quest.UserQuestService;
import com.example.personalityquest.Services.quest.WeeklyTaskService;
import com.example.personalityquest.Services.quiz.QuizService;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.io.IOException;

/** Controls dashboard data and user interactions */
public class DashboardController implements Initializable {

    // ---- Archetype balance radar chart constants (copied from ProfileController) ----
    private static final int RADAR_AXIS_COUNT = Archetype.values().length;
    private static final int RADAR_LEVEL_COUNT = 5;
    private static final double RADAR_CENTER_Y_OFFSET = 8;
    private static final double RADAR_RADIUS_RATIO = 0.26;
    private static final String[] RADAR_LABELS = BuildRadarLabels();

    private static String[] BuildRadarLabels() {
        Archetype[] archetypes = Archetype.values();
        String[] labels = new String[archetypes.length];
        for (int index = 0; index < archetypes.length; index++) {
            labels[index] = archetypes[index].getName();
        }
        return labels;
    }

    @FXML
    private NavBarController navBarController;
    @FXML
    private BorderPane dashboardRoot;
    @FXML
    private Label welcomeMessage, streakLabel;
    @FXML
    private Canvas progressChart;
    @FXML
    private VBox progressCard, traitCard, historyCard, weeklyTasksCard, verticalProgressCard;
    @FXML
    private HBox thermometerRow;
    @FXML
    private Label traitLabel, reflectionPromptLabel;
    @FXML
    private ListView<QuestListItem> questHistory;
    @FXML
    private ListView<WeeklyTask> weeklyTasksList;
    @FXML
    private ProgressBar challengesProgress, reflectionsProgress, weeklyProgress;

    private double[] radarValues = new double[RADAR_AXIS_COUNT];
    private boolean isRefreshingQuestline = false;

    private WeeklyTaskService WeeklyTaskService;
    private UserQuestService UserQuestService;
    private TaskService TaskService;
    private QuestService QuestService;
    private JournalEntryService JournalEntryService;
    /** Initialises dashboard controls and loads account data
     * @param location the location used to resolve relative paths
     * @param resources the localisation resources for the screen
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TaskService = new TaskService();
        UserQuestService = new UserQuestService();
        WeeklyTaskService = new WeeklyTaskService();
        QuestService = new QuestService();
        JournalEntryService = new JournalEntryService();
        navBarController.setCurrentDestination(NavBarController.NavDestination.HOME);
        ConfigureHistoryList();
        ConfigureWeeklyTaskList();
        ConfigureResponsiveSizing();

        try {
            SetWelcome();
            UpdateStreakLabel();
        } catch (Exception exception) {
            welcomeMessage.setText("Welcome back!");
            streakLabel.setText("Week 0");
        }

        radarValues = QuizService.ArchetypeScores(
                QuizService.LoadStoredResult(ApplicationManager.CurrentAccount.getCurrentEmail()));

        LoadQuestline();
        UpdateThermometers();

        try {
            PopulateWeeklyTasks();
        } catch (Exception exception) {
            System.err.println("Could not load weekly tasks: " + exception.getMessage());
        }

        Platform.runLater(this::DrawProgressGraph);
    }

    /**
     * Fills the three thermometers: Quest Challenges, Quest Reflections, and
     * Weekly Challenges.
     */
    private void UpdateThermometers() {
        String email = ApplicationManager.CurrentAccount.getCurrentEmail();
        double challengesRatio = 0;
        double reflectionsRatio = 0;
        double weeklyRatio = 0;

        if (!ApplicationManager.isEmpty(email)) {
            try {
                List<UserQuest> userQuests = UserQuestService.GetUserQuestsForEmail(email);
                int totalLabours = userQuests.size();

                int completedChallenges = JournalEntryService.GetChallengesForEmail(email).size();
                challengesRatio = RatioOf(completedChallenges, totalLabours);

                long completedReflections = userQuests.stream()
                        .filter(userQuest -> "Finished".equalsIgnoreCase(userQuest.getReflectionStatus()))
                        .count();
                reflectionsRatio = RatioOf((int) completedReflections, totalLabours);
            } catch (Exception exception) {
                challengesRatio = 0;
                reflectionsRatio = 0;
            }

            try {
                WeeklyTask[] weeklyTasks = WeeklyTaskService.GetTasksForEmailForThisWeek(email);
                if (weeklyTasks == null) {
                    weeklyTasks = WeeklyTaskService.GenerateTasksForThisWeek(email);
                }
                if (weeklyTasks != null) {
                    long completedWeekly = Arrays.stream(weeklyTasks)
                            .filter(task -> "Finished".equalsIgnoreCase(task.getStatus()))
                            .count();
                    weeklyRatio = RatioOf((int) completedWeekly, weeklyTasks.length);
                }
            } catch (Exception exception) {
                weeklyRatio = 0;
            }
        }

        challengesProgress.setProgress(challengesRatio);
        reflectionsProgress.setProgress(reflectionsRatio);
        weeklyProgress.setProgress(weeklyRatio);
    }

    private double RatioOf(int completed, int total) {
        return total > 0 ? Math.min(1.0, (double) completed / total) : 0;
    }

    // ---- Weekly Tasks tile (tick-box list) ----

    /**
     * Loads this week's weekly practices into the home screen's tick-box list,
     * generating a fresh set for the week if none have been assigned yet.
     */
    private void PopulateWeeklyTasks() throws Exception {
        weeklyTasksList.getItems().clear();
        String email = ApplicationManager.CurrentAccount.getCurrentEmail();

        if (ApplicationManager.isEmpty(email)) {
            return;
        }

        WeeklyTask[] tasks = WeeklyTaskService.GetTasksForEmailForThisWeek(email);
        if (tasks == null) {
            tasks = WeeklyTaskService.GenerateTasksForThisWeek(email);
        }

        if (tasks != null) {
            weeklyTasksList.getItems().addAll(tasks);
        }
    }

    /**
     * Gives each weekly task row a checkbox reflecting whether it's finished,
     * alongside the task's name and status.
     */
    private void ConfigureWeeklyTaskList() {
        weeklyTasksList.setPlaceholder(new Label("No weekly tasks assigned yet."));
        weeklyTasksList.setCellFactory(list -> new ListCell<>() {
            private final CheckBox completedBox = new CheckBox();
            private final Label nameLabel = new Label();
            private final Label statusLabel = new Label();
            private final VBox textColumn = new VBox(2, nameLabel, statusLabel);
            private final HBox row = new HBox(10, completedBox, textColumn);

            {
                row.getStyleClass().add("history-row");
                nameLabel.getStyleClass().add("history-name");
                statusLabel.getStyleClass().add("history-detail");
                completedBox.setMouseTransparent(true);
                completedBox.setFocusTraversable(false);
                HBox.setHgrow(textColumn, Priority.ALWAYS);
            }

            @Override
            protected void updateItem(WeeklyTask item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                nameLabel.setText(WeeklyTaskDisplayName(item));
                statusLabel.setText(item.getStatus());
                completedBox.setSelected("Finished".equalsIgnoreCase(item.getStatus()));
                setText(null);
                setGraphic(row);
            }
        });
    }

    private String WeeklyTaskDisplayName(WeeklyTask weeklyTask) {
        try {
            Task task = TaskService.GetTaskForId(weeklyTask.getTaskId());
            if (task != null && !ApplicationManager.isEmpty(task.getName())) {
                return task.getName();
            }
        } catch (Exception exception) {
            // fall through to the default label below
        }
        return "Weekly task";
    }

    /** Opens the Tasks/Journal screen when a weekly task row is clicked */
    @FXML
    private void OnWeeklyTaskClick(MouseEvent event) throws IOException {
        if (weeklyTasksList.getSelectionModel().getSelectedItem() != null) {
            NavigationService.LoadScreen(ScreenEnum.TASKS);
        }
    }

    /**
     * Keeps the radar chart canvas and the rotated vertical progress bar sized
     * to the window instead of a fixed pixel size, so the home grid scales
     * with the open window rather than overflowing it.
     */
    private void ConfigureResponsiveSizing() {
        progressCard.widthProperty().addListener((observable, oldValue, newValue) ->
                UpdateRadarChartSize(newValue.doubleValue(), progressCard.getHeight()));
        progressCard.heightProperty().addListener((observable, oldValue, newValue) ->
                UpdateRadarChartSize(progressCard.getWidth(), newValue.doubleValue()));

        // Bind off the outer card's height (not thermometerRow's) - the rotated progress
        // bars report their unrotated (small) size to the layout system, so the HBox never
        // reports a large preferred/actual height of its own. The card, however, is already
        // stretched to fill its GridPane cell correctly, so it gives us a reliable height
        // to size the bars from.
        verticalProgressCard.heightProperty().addListener((observable, oldValue, newValue) ->
                UpdateThermometerLengths(newValue.doubleValue()));
        Platform.runLater(() -> {
            UpdateRadarChartSize(progressCard.getWidth(), progressCard.getHeight());
            UpdateThermometerLengths(verticalProgressCard.getHeight());
        });
    }

    private void UpdateRadarChartSize(double cardWidth, double cardHeight) {
        double width = Math.max(120, cardWidth - 24);
        double height = Math.max(120, cardHeight - 70);
        if (width == progressChart.getWidth() && height == progressChart.getHeight()) {
            return;
        }
        progressChart.setWidth(width);
        progressChart.setHeight(height);
        DrawProgressGraph();
    }

    private void UpdateThermometerLengths(double cardHeight) {
        // Reserves space for: card padding (24), the "Progress" title (~34) and the
        // spacing below it (10), plus each column's own label (~24) and the spacing
        // around the bar inside thermometerRow (20).
        double reservedSpace = 130;
        double length = Math.max(60, cardHeight - reservedSpace);
        challengesProgress.setPrefWidth(length);
        reflectionsProgress.setPrefWidth(length);
        weeklyProgress.setPrefWidth(length);
    }

    private void SetWelcome() throws Exception {
        EmailDetails emailDetails = EmailService.GetDetailsForEmail(
                ApplicationManager.CurrentAccount.getCurrentEmail());

        if (emailDetails == null) {
            welcomeMessage.setText("Welcome back!");
            return;
        }

        welcomeMessage.setText("Welcome back, " + emailDetails.getFirstName() + "!");
    }

    private void UpdateStreakLabel() {
        try {
            int streak = StreakService.GetCurrentStreak(
                    ApplicationManager.CurrentAccount.getCurrentEmail());
            streakLabel.setText("Week " + streak);
        } catch (Exception exception) {
            streakLabel.setText("Week 0");
        }
    }

    // ---- "Your quests" / "Why this labour" / "This week's challenge" (copied from QuestController) ----

    private void LoadQuestline() {
        String email = ApplicationManager.CurrentAccount.getCurrentEmail();
        questHistory.getItems().clear();

        if (ApplicationManager.isEmpty(email)) {
            ShowEmptyQuest("Sign in to see your questline.");
            return;
        }

        EnsureSecondArchetypeQuestIsAvailable(email);

        try {
            List<UserQuest> userQuests = UserQuestService.GetUserQuestsForEmail(email);
            QuestListItem selected = null;

            for (UserQuest userQuest : userQuests) {
                Quest quest = QuestService.GetQuestForLabourId(userQuest.getLabourId());
                if (quest == null) {
                    continue;
                }

                String archetypeName = QuestService.GetArchetypeName(quest.getArchetypeId());
                QuestListItem item = new QuestListItem(userQuest, quest, archetypeName);
                questHistory.getItems().add(item);

                if (selected == null || "Active".equalsIgnoreCase(userQuest.getStatus())) {
                    selected = item;
                }
            }

            if (selected == null) {
                ShowEmptyQuest("No quests have been assigned yet.");
                return;
            }

            questHistory.getSelectionModel().select(selected);
            ShowQuest(selected);
        } catch (Exception exception) {
            ShowEmptyQuest("Could not load your questline right now.");
        }
    }

    /**
     * Makes sure the user's second-highest scoring archetype has a quest sitting
     * in "Your quests" ready to switch to, even if they haven't started it yet.
     */
    private void EnsureSecondArchetypeQuestIsAvailable(String email) {
        try {
            QuizResult quizResult = QuizService.LoadStoredResult(email);
            if (quizResult == null) {
                return;
            }

            List<Archetype> ranked = QuizService.RankedArchetypes(quizResult);
            if (ranked.size() < 2) {
                return;
            }

            Archetype secondArchetype = ranked.get(1);
            Quest secondQuest = QuestService.GetRandomQuestForArchetypeId(secondArchetype.getArchetypeId());
            if (secondQuest == null) {
                return;
            }

            UserQuest existing = UserQuestService.GetUserQuestForEmailAndLabourId(email, secondQuest.getLabourId());
            if (existing == null) {
                UserQuestService.InsertNewQuestForEmail(secondQuest, email);
            }
        } catch (Exception exception) {
            System.err.println("Could not prepare second archetype quest: " + exception.getMessage());
        }
    }

    private void ShowQuest(QuestListItem item) {
        if (item == null) {
            ShowEmptyQuest("Select a quest to read its story.");
            return;
        }

        traitLabel.setText(item.archetypeName());
        reflectionPromptLabel.setText("Weekly practices are on the Tasks page. These storyline tasks belong to the labour itself.");
    }

    private void ShowEmptyQuest(String message) {
        traitLabel.setText("—");
        reflectionPromptLabel.setText(message);
    }

    private void ConfigureHistoryList() {
        questHistory.setPlaceholder(new Label("No quests assigned yet."));
        questHistory.setCellFactory(list -> new ListCell<>() {
            private final Label nameLabel = new Label();
            private final Label detailLabel = new Label();
            private final Region spacer = new Region();
            private final Label percentLabel = new Label();
            private final HBox header = new HBox(8, nameLabel, spacer, percentLabel);
            private final VBox row = new VBox(2, header, detailLabel);

            {
                row.getStyleClass().add("history-row");
                nameLabel.getStyleClass().add("history-name");
                detailLabel.getStyleClass().add("history-detail");
                percentLabel.getStyleClass().add("history-detail");
                HBox.setHgrow(spacer, Priority.ALWAYS);
            }

            @Override
            protected void updateItem(QuestListItem item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                nameLabel.setText(item.quest().getName());
                detailLabel.setText(item.userQuest().getStatus() + " · " + item.archetypeName());
                percentLabel.setText(String.format("%.0f%%", item.userQuest().getPercentageComplete() * 100));
                setText(null);
                setGraphic(row);
            }
        });

        questHistory.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldItem, newItem) -> {
                    if (newItem == null || isRefreshingQuestline) {
                        return;
                    }
                    ShowQuest(newItem);
                    ActivateQuest(newItem);
                });
    }

    /**
     * Switches the active quest to the one the user clicked on in "Your quests".
     */
    private void ActivateQuest(QuestListItem item) {
        String email = ApplicationManager.CurrentAccount.getCurrentEmail();
        if (ApplicationManager.isEmpty(email)) {
            return;
        }

        try {
            UserQuestService.ChangeActiveQuest(item.quest(), email);
            isRefreshingQuestline = true;
            LoadQuestline();
        } catch (Exception exception) {
            System.err.println("Could not switch active quest: " + exception.getMessage());
        } finally {
            isRefreshingQuestline = false;
        }
    }

    private record QuestListItem(UserQuest userQuest, Quest quest, String archetypeName) {
    }

    // ---- Archetype balance radar chart (copied from ProfileController) ----

    private void DrawProgressGraph() {
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

        double[] xPoints = new double[axes];
        double[] yPoints = new double[axes];
        for (int axis = 0; axis < axes; axis++) {
            xPoints[axis] = PointX(centerX, radius * radarValues[axis], axis, axes);
            yPoints[axis] = PointY(centerY, radius * radarValues[axis], axis, axes);
        }

        graphics.setFill(Color.rgb(46, 135, 207, 0.45));
        graphics.fillPolygon(xPoints, yPoints, axes);
        graphics.setStroke(Color.web("#43a9f2"));
        graphics.setLineWidth(2);
        graphics.strokePolygon(xPoints, yPoints, axes);

        graphics.setFill(Color.web("#c4b5fd"));
        graphics.setFont(Font.font("Poppins", 10));
        graphics.setTextAlign(TextAlignment.CENTER);
        for (int axis = 0; axis < axes; axis++) {
            DrawAxisLabel(graphics, RADAR_LABELS[axis], centerX, centerY, radius, axis, axes);
        }
    }

    private void DrawAxisLabel(
            GraphicsContext graphics,
            String label,
            double centerX,
            double centerY,
            double radius,
            int axis,
            int axes) {
        double labelRadius = radius + 22;
        double x = PointX(centerX, labelRadius, axis, axes);
        double y = PointY(centerY, labelRadius, axis, axes);

        if (x < centerX - 8) {
            graphics.setTextAlign(TextAlignment.RIGHT);
            x -= 4;
        } else if (x > centerX + 8) {
            graphics.setTextAlign(TextAlignment.LEFT);
            x += 4;
        } else {
            graphics.setTextAlign(TextAlignment.CENTER);
        }

        graphics.fillText(label, x, y);
    }

    private double PointX(double centerX, double radius, int axis, int axes) {
        return centerX + radius * Math.cos(-Math.PI / 2 + axis * 2 * Math.PI / axes);
    }

    private double PointY(double centerY, double radius, int axis, int axes) {
        return centerY + radius * Math.sin(-Math.PI / 2 + axis * 2 * Math.PI / axes);
    }
}

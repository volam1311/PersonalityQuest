package com.example.personalityquest.Controllers.profile;

import com.example.personalityquest.Controllers.navigation.NavBarController;
import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.Model.profile.Achievement;
import com.example.personalityquest.Model.auth.EmailDetails;
import com.example.personalityquest.Model.profile.UserProfile;
import com.example.personalityquest.Model.quest.Quest;
import com.example.personalityquest.Model.quest.UserQuest;
import com.example.personalityquest.ScreenEnum;
import com.example.personalityquest.Services.profile.AchievementService;
import com.example.personalityquest.Services.auth.EmailService;
import com.example.personalityquest.Services.navigation.NavigationService;
import com.example.personalityquest.Services.profile.UserProfileService;
import com.example.personalityquest.Services.quest.QuestService;
import com.example.personalityquest.Services.quest.UserQuestService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {
    private static final double STACKED_BREAKPOINT = 760;
    private static final double FULL_PERCENT = 100;
    private static final double HIDDEN_PERCENT = 0;
    private static final double IDENTITY_CARD_PERCENT = 32;
    private static final double PROGRESS_CARD_PERCENT = 68;
    private static final int ACHIEVEMENT_COLUMNS = 5;
    private static final int RADAR_AXIS_COUNT = 5;
    private static final int RADAR_LEVEL_COUNT = 5;
    private static final double RADAR_CENTER_Y_OFFSET = 8;
    private static final double RADAR_RADIUS_RATIO = 0.32;
    private static final String[] RADAR_LABELS = {
            "Quest progress",
            "Weekly tasks",
            "Streak",
            "Labours complete",
            "Tasks finished"
    };

    @FXML
    private NavBarController navBarController;
    @FXML
    private BorderPane profileRoot;
    @FXML
    private GridPane statsGrid, achievementsGrid;
    @FXML
    private VBox progressCard;
    @FXML
    private Label displayNameLabel, personalityTypeLabel, streakLabel, typeBadge;
    @FXML
    private Button archetypeLink, archetypeBadge;
    @FXML
    private Canvas progressChart;

    private double[] radarValues = {0, 0, 0, 0, 0};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        navBarController.setCurrentDestination(NavBarController.NavDestination.PROFILE);
        ConfigureAchievementsGrid();
        LoadProfile();

        profileRoot.widthProperty().addListener((observable, oldWidth, newWidth) ->
                ApplyResponsiveLayout(newWidth.doubleValue()));

        Platform.runLater(() -> {
            ApplyResponsiveLayout(profileRoot.getWidth());
            DrawProgressGraph();
        });
    }

    private void LoadProfile() {
        String email = ApplicationManager.CurrentAccount.getCurrentEmail();
        boolean signedIn = !ApplicationManager.isEmpty(email);

        EmailDetails details = null;
        if (signedIn) {
            try {
                details = EmailService.GetDetailsForEmail(email);
            } catch (Exception ignored) {
                details = null;
            }
        }
        displayNameLabel.setText(UserProfileService.DisplayName(details));

        UserProfile progress = UserProfile.empty();
        if (signedIn) {
            try {
                progress = AchievementService.GetProgress(email);
            } catch (Exception ignored) {
                progress = UserProfile.empty();
            }
        }
        streakLabel.setText("Week " + progress.currentStreak());
        radarValues = UserProfileService.RadarValues(progress);

        String archetypeName = UserProfileService.UNASSIGNED_ARCHETYPE;
        String personalityType = UserProfileService.UNKNOWN_TYPE;
        if (signedIn) {
            try {
                UserQuest selectedQuest = UserQuestService.GetCurrentActiveUserQuestForEmail(email);
                if (selectedQuest == null) {
                    List<UserQuest> userQuests = UserQuestService.GetUserQuestsForEmail(email);
                    if (!userQuests.isEmpty()) {
                        selectedQuest = userQuests.get(0);
                    }
                }
                if (selectedQuest != null) {
                    Quest quest = QuestService.GetQuestForLabourId(selectedQuest.getLabourId());
                    if (quest != null) {
                        archetypeName = UserProfileService.FormatArchetypeName(
                                QuestService.GetArchetypeName(quest.getArchetypeId()));
                        personalityType = UserProfileService.PersonalityType(
                                QuestService.GetArchetypeDescription(quest.getArchetypeId()));
                    }
                }
            } catch (Exception ignored) {
                archetypeName = UserProfileService.UNASSIGNED_ARCHETYPE;
            }
        }

        personalityTypeLabel.setText(personalityType);
        typeBadge.setText(personalityType);
        archetypeLink.setText(archetypeName + " (Click to view)");
        archetypeBadge.setText(archetypeName);

        try {
            PopulateAchievements(AchievementService.GetAchievementsForEmail(email));
        } catch (Exception ignored) {
            PopulateAchievements(List.of());
        }
    }

    private void ConfigureAchievementsGrid() {
        achievementsGrid.getColumnConstraints().clear();
        for (int column = 0; column < ACHIEVEMENT_COLUMNS; column++) {
            ColumnConstraints constraints = new ColumnConstraints();
            constraints.setPercentWidth(FULL_PERCENT / ACHIEVEMENT_COLUMNS);
            constraints.setHgrow(Priority.ALWAYS);
            achievementsGrid.getColumnConstraints().add(constraints);
        }
    }

    private void PopulateAchievements(List<Achievement> achievements) {
        achievementsGrid.getChildren().clear();

        for (int index = 0; index < achievements.size(); index++) {
            Achievement achievement = achievements.get(index);
            StackPane badge = new StackPane();
            badge.getStyleClass().add("achievement-badge");
            if (achievement.unlocked()) {
                badge.getStyleClass().add("unlocked");
            } else {
                badge.getStyleClass().add("locked");
            }

            SVGPath star = new SVGPath();
            star.setContent("M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z");
            star.setStyle("-fx-fill: #ff8a3d;");
            star.getStyleClass().add("achievement-star");
            badge.getChildren().add(star);
            StackPane.setAlignment(star, Pos.CENTER);

            Tooltip.install(badge, new Tooltip(achievement.name() + "\n" + achievement.description()));
            achievementsGrid.add(badge, index % ACHIEVEMENT_COLUMNS, index / ACHIEVEMENT_COLUMNS);
        }
    }

    private void ApplyResponsiveLayout(double width) {
        if (width <= 0) {
            return;
        }

        boolean stacked = width < STACKED_BREAKPOINT;
        if (stacked) {
            GridPane.setColumnIndex(progressCard, 0);
            GridPane.setRowIndex(progressCard, 1);
            statsGrid.getColumnConstraints().get(0).setPercentWidth(FULL_PERCENT);
            statsGrid.getColumnConstraints().get(1).setPercentWidth(HIDDEN_PERCENT);
        } else {
            GridPane.setColumnIndex(progressCard, 1);
            GridPane.setRowIndex(progressCard, 0);
            statsGrid.getColumnConstraints().get(0).setPercentWidth(IDENTITY_CARD_PERCENT);
            statsGrid.getColumnConstraints().get(1).setPercentWidth(PROGRESS_CARD_PERCENT);
        }
    }

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
        graphics.setFont(Font.font("Poppins", 12));
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

    @FXML
    private void OnArchetypeClick() throws IOException {
        NavigationService.LoadScreen(ScreenEnum.ARCHETYPE);
    }
}

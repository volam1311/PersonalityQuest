package com.example.personalityquest.Controllers;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.Model.Quest;
import com.example.personalityquest.Model.QuestLore;
import com.example.personalityquest.Model.UserProfile;
import com.example.personalityquest.Model.UserQuest;
import com.example.personalityquest.Services.AchievementService;
import com.example.personalityquest.Services.QuestService;
import com.example.personalityquest.Services.UserProfileService;
import com.example.personalityquest.Services.UserQuestService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ArchetypeController implements Initializable {
    private static final int RADAR_AXES = 5;
    private static final int RADAR_LEVELS = 5;
    private static final String SELECTED_ARCHETYPE = "selected-archetype";
    private static final String[] RADAR_LABELS = {
            "Progress", "Weekly", "Streak", "Quests", "Tasks"
    };

    @FXML
    private NavBarController navBarController;
    @FXML
    private Label archetypeRankLabel, archetypeNameLabel, overviewLabel, strengthsLabel,
            questFocusLabel;
    @FXML
    private Canvas radarChart;
    @FXML
    private Button firstArchetypeButton, secondArchetypeButton, thirdArchetypeButton;

    private final List<Button> archetypeButtons = new ArrayList<>();
    private List<ArchetypeOption> archetypeOptions = List.of();
    private double[] radarValues = new double[RADAR_AXES];

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        navBarController.setCurrentDestination(NavBarController.NavDestination.ARCHETYPE);
        archetypeButtons.add(firstArchetypeButton);
        archetypeButtons.add(secondArchetypeButton);
        archetypeButtons.add(thirdArchetypeButton);
        radarChart.widthProperty().addListener((observable, oldValue, newValue) -> DrawRadarChart());
        radarChart.heightProperty().addListener((observable, oldValue, newValue) -> DrawRadarChart());
        LoadArchetypes();
    }

    private void LoadArchetypes() {
        String email = ApplicationManager.CurrentAccount.getCurrentEmail();
        if (ApplicationManager.isEmpty(email)) {
            ShowEmptyArchetype("Sign in to discover your archetypes.");
            return;
        }

        try {
            UserQuest userQuest = UserQuestService.GetCurrentActiveUserQuestForEmail(email);
            if (userQuest == null) {
                ShowEmptyArchetype("Complete the onboarding quiz to discover your archetype.");
                return;
            }

            Quest quest = QuestService.GetQuestForLabourId(userQuest.getLabourId());
            if (quest == null) {
                ShowEmptyArchetype("Your current quest does not have an archetype yet.");
                return;
            }

            String archetypeName = UserProfileService.FormatArchetypeName(
                    QuestService.GetArchetypeName(quest.getArchetypeId()));
            String description = QuestService.GetArchetypeDescription(quest.getArchetypeId());
            QuestLore lore = QuestLore.forQuest(quest.getName(), archetypeName);
            ArchetypeOption option = new ArchetypeOption(archetypeName, description, lore);

            archetypeOptions = List.of(option, option, option);

            ConfigureArchetypeButtons();
            DisplayArchetype(option, firstArchetypeButton);
            LoadJourneyProfile(email);
        } catch (Exception exception) {
            ShowEmptyArchetype("Could not load your archetypes right now.");
        }
    }

    private void ConfigureArchetypeButtons() {
        for (int index = 0; index < archetypeButtons.size(); index++) {
            Button button = archetypeButtons.get(index);
            boolean available = index < archetypeOptions.size();
            button.setVisible(available);
            button.setManaged(available);
            if (available) {
                ArchetypeOption option = archetypeOptions.get(index);
                button.setText("#" + (index + 1) + "  " + option.name());
                button.setUserData(option);
            }
        }
    }

    @FXML
    private void OnArchetypeClick(ActionEvent event) {
        if (event.getSource() instanceof Button button
                && button.getUserData() instanceof ArchetypeOption option) {
            DisplayArchetype(option, button);
        }
    }

    private void DisplayArchetype(ArchetypeOption option, Button selectedButton) {
        archetypeRankLabel.setText("#" + (archetypeButtons.indexOf(selectedButton) + 1));
        archetypeNameLabel.setText(option.name());
        overviewLabel.setText(ApplicationManager.isEmpty(option.description())
                ? "No description is available for this archetype yet."
                : option.description());
        strengthsLabel.setText(option.lore().traitFocus());
        questFocusLabel.setText(option.lore().labourTitle() + "\n" + option.lore().challenge());

        for (int index = 0; index < archetypeButtons.size(); index++) {
            Button button = archetypeButtons.get(index);
            button.getStyleClass().remove(SELECTED_ARCHETYPE);
            if (button == selectedButton) {
                button.getStyleClass().add(SELECTED_ARCHETYPE);
            }
        }

        DrawRadarChart();
    }

    private void LoadJourneyProfile(String email) {
        try {
            UserProfile profile = AchievementService.GetProgress(email);
            radarValues = UserProfileService.RadarValues(profile);
        } catch (SQLException exception) {
            radarValues = new double[RADAR_AXES];
        }
        Platform.runLater(this::DrawRadarChart);
    }

    private void ShowEmptyArchetype(String message) {
        archetypeRankLabel.setText("#1");
        archetypeNameLabel.setText("Unassigned");
        overviewLabel.setText(message);
        strengthsLabel.setText("—");
        questFocusLabel.setText("Your quest focus will appear here once an archetype is assigned.");
        archetypeOptions = List.of();
        ConfigureArchetypeButtons();
        radarValues = new double[RADAR_AXES];
        Platform.runLater(this::DrawRadarChart);
    }

    private void DrawRadarChart() {
        GraphicsContext graphics = radarChart.getGraphicsContext2D();
        double width = radarChart.getWidth();
        double height = radarChart.getHeight();
        double centerX = width / 2;
        double centerY = height / 2 + 8;
        double radius = Math.min(width, height) * 0.30;

        graphics.clearRect(0, 0, width, height);
        graphics.setLineWidth(1);
        graphics.setFont(Font.font("Poppins", 11));

        for (int level = 1; level <= RADAR_LEVELS; level++) {
            double levelRadius = radius * level / RADAR_LEVELS;
            double[] xPoints = RadarXPoints(centerX, levelRadius);
            double[] yPoints = RadarYPoints(centerY, levelRadius);
            graphics.setStroke(Color.web("#66538e"));
            graphics.strokePolygon(xPoints, yPoints, RADAR_AXES);
        }

        for (int axis = 0; axis < RADAR_AXES; axis++) {
            graphics.setStroke(Color.web("#66538e"));
            graphics.strokeLine(
                    centerX,
                    centerY,
                    PointX(centerX, radius, axis),
                    PointY(centerY, radius, axis));

            graphics.setFill(Color.web("#c4b5fd"));
            graphics.fillText(
                    RADAR_LABELS[axis],
                    PointX(centerX, radius + 14, axis) - 18,
                    PointY(centerY, radius + 14, axis));
        }

        double[] values = new double[RADAR_AXES];
        for (int index = 0; index < RADAR_AXES; index++) {
            values[index] = index < radarValues.length ? Clamp(radarValues[index]) : 0;
        }

        double[] valueX = new double[RADAR_AXES];
        double[] valueY = new double[RADAR_AXES];
        for (int axis = 0; axis < RADAR_AXES; axis++) {
            valueX[axis] = PointX(centerX, radius * values[axis], axis);
            valueY[axis] = PointY(centerY, radius * values[axis], axis);
        }

        graphics.setFill(Color.rgb(154, 114, 255, 0.35));
        graphics.setStroke(Color.web("#a884ff"));
        graphics.fillPolygon(valueX, valueY, RADAR_AXES);
        graphics.strokePolygon(valueX, valueY, RADAR_AXES);
    }

    private double[] RadarXPoints(double centerX, double radius) {
        double[] points = new double[RADAR_AXES];
        for (int axis = 0; axis < RADAR_AXES; axis++) {
            points[axis] = PointX(centerX, radius, axis);
        }
        return points;
    }

    private double[] RadarYPoints(double centerY, double radius) {
        double[] points = new double[RADAR_AXES];
        for (int axis = 0; axis < RADAR_AXES; axis++) {
            points[axis] = PointY(centerY, radius, axis);
        }
        return points;
    }

    private double PointX(double centerX, double radius, int axis) {
        return centerX + radius * Math.cos(-Math.PI / 2 + axis * 2 * Math.PI / RADAR_AXES);
    }

    private double PointY(double centerY, double radius, int axis) {
        return centerY + radius * Math.sin(-Math.PI / 2 + axis * 2 * Math.PI / RADAR_AXES);
    }

    private double Clamp(double value) {
        if (Double.isNaN(value) || value < 0) {
            return 0;
        }
        return Math.min(value, 1);
    }

    private record ArchetypeOption(
            String name,
            String description,
            QuestLore lore) {
    }
}

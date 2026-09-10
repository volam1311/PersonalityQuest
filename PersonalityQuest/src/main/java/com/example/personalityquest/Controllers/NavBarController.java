package com.example.personalityquest.Controllers;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.Applications.DashboardApplication;
import com.example.personalityquest.Applications.QuestApplication;
import com.example.personalityquest.Applications.SettingsApplication;
import com.example.personalityquest.Model.EmailDetails;
import com.example.personalityquest.Services.EmailService;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NavBarController implements Initializable {
    public enum NavDestination {
        HOME, QUESTS, TASKS, ARCHETYPE, SETTINGS
    }

    private static final double SIDEBAR_BREAKPOINT = 900;
    private static final double EXPANDED_SIDEBAR_WIDTH = 250;
    private static final double COLLAPSED_SIDEBAR_WIDTH = 72;
    private static final double NAV_BUTTON_HEIGHT = 52;
    private static final double PROFILE_BUTTON_HEIGHT = 44;
    private static final double COMPACT_BUTTON_SIZE = 44;
    private static final double EXPANDED_BUTTON_PREF_WIDTH = 9999;
    private static final String SELECTED_NAV_BUTTON = "selected-nav-button";
    private static final String SELECTED_SETTINGS_BUTTON = "selected-settings-button";

    @FXML
    private VBox sidebar;
    @FXML
    private Label brandLabel, profileNameLabel;
    @FXML
    private Label homeNavLabel, questsNavLabel, tasksNavLabel, archetypeNavLabel;
    @FXML
    private Button menuButton, homeButton, questsButton,
            tasksButton, archetypeButton, profileButton, settingsButton;

    private boolean sidebarExpanded = true;
    private boolean sidebarOverride;
    private NavDestination currentDestination;
    private final ChangeListener<Number> sceneWidthListener =
            (observable, oldWidth, newWidth) -> ApplyResponsiveLayout(newWidth.doubleValue());

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        SetProfileLabel();
        SetSidebarExpanded(sidebarExpanded);

        sidebar.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (oldScene != null) {
                oldScene.widthProperty().removeListener(sceneWidthListener);
            }
            if (newScene != null) {
                newScene.widthProperty().addListener(sceneWidthListener);
                ApplyResponsiveLayout(newScene.getWidth());
            }
        });

        Platform.runLater(() -> {
            Scene scene = sidebar.getScene();
            if (scene != null) {
                ApplyResponsiveLayout(scene.getWidth());
            }
        });
    }

    public void setCurrentDestination(NavDestination destination) {
        currentDestination = destination;
        ApplySelectedStyles();
    }

    private void SetProfileLabel() {
        String profileText = "Profile";
        try {
            EmailDetails emailDetails = EmailService.GetDetailsForEmail(
                    ApplicationManager.CurrentAccount.getCurrentEmail());
            if (emailDetails != null) {
                profileText = emailDetails.getUserName();
            }
        } catch (Exception exception) {
            profileText = "Profile";
        }
        profileNameLabel.setText(profileText);
    }

    private void ApplyResponsiveLayout(double width) {
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
    }

    private void SetSidebarExpanded(boolean expanded) {
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

    private void ApplySelectedStyles() {
        homeButton.getStyleClass().remove(SELECTED_NAV_BUTTON);
        questsButton.getStyleClass().remove(SELECTED_NAV_BUTTON);
        tasksButton.getStyleClass().remove(SELECTED_NAV_BUTTON);
        archetypeButton.getStyleClass().remove(SELECTED_NAV_BUTTON);
        settingsButton.getStyleClass().remove(SELECTED_SETTINGS_BUTTON);

        if (currentDestination == null) {
            return;
        }

        switch (currentDestination) {
            case HOME -> homeButton.getStyleClass().add(SELECTED_NAV_BUTTON);
            case QUESTS -> questsButton.getStyleClass().add(SELECTED_NAV_BUTTON);
            case TASKS -> tasksButton.getStyleClass().add(SELECTED_NAV_BUTTON);
            case ARCHETYPE -> archetypeButton.getStyleClass().add(SELECTED_NAV_BUTTON);
            case SETTINGS -> settingsButton.getStyleClass().add(SELECTED_SETTINGS_BUTTON);
        }
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
        NavDestination destination = DestinationFor(button);

        if (destination == null || destination == currentDestination) {
            return;
        }

        Stage stage = (Stage) sidebar.getScene().getWindow();
        switch (destination) {
            case HOME -> DashboardApplication.launch(stage);
            case QUESTS -> QuestApplication.launch(stage);
            case SETTINGS -> SettingsApplication.launch(stage);
            case TASKS, ARCHETYPE -> {
            }
        }
    }

    private NavDestination DestinationFor(Button button) {
        if (button == homeButton) {
            return NavDestination.HOME;
        }
        if (button == questsButton) {
            return NavDestination.QUESTS;
        }
        if (button == tasksButton) {
            return NavDestination.TASKS;
        }
        if (button == archetypeButton) {
            return NavDestination.ARCHETYPE;
        }
        if (button == settingsButton) {
            return NavDestination.SETTINGS;
        }
        return null;
    }
}

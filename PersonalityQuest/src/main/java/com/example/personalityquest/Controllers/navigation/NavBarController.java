package com.example.personalityquest.Controllers.navigation;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.Applications.navigation.DashboardApplication;
import com.example.personalityquest.Applications.quest.QuestApplication;
import com.example.personalityquest.Applications.navigation.SettingsApplication;
import com.example.personalityquest.Applications.quest.TasksApplication;
import com.example.personalityquest.Model.auth.EmailDetails;
import com.example.personalityquest.Model.auth.LoginCache;
import com.example.personalityquest.ScreenEnum;
import com.example.personalityquest.Services.auth.EmailService;
import com.example.personalityquest.Services.auth.LoginCacheService;
import com.example.personalityquest.Services.navigation.NavigationService;
import com.example.personalityquest.Services.quiz.QuizService;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/** Controls navigation destinations and their selected state */
public class NavBarController implements Initializable {
    /** Lists the destinations available in the navigation bar */
    public enum NavDestination {
        HOME, QUESTS, QUEST_VIEWER, TASKS, ARCHETYPE, PROFILE, SETTINGS
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
    private Label brandLabel, profileNameLabel, logoutNavLabel;
    @FXML
    private Label homeNavLabel, questsNavLabel, questViewerNavLabel, tasksNavLabel, archetypeNavLabel;
    @FXML
    private Button menuButton, homeButton, questsButton, questViewerButton,
            tasksButton, archetypeButton, profileButton, settingsButton, logoutButton;

    private boolean sidebarExpanded = true;
    private boolean sidebarOverride;
    private NavDestination currentDestination;
    private final ChangeListener<Number> sceneWidthListener =
            (observable, oldWidth, newWidth) -> ApplyResponsiveLayout(newWidth.doubleValue());

    private EmailService EmailService;
    private QuizService QuizService;
    /** Initialises the navigation bar controls
     * @param location the location used to resolve relative paths
     * @param resources the localisation resources for the screen
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        EmailService = new EmailService();
        QuizService = new QuizService();
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

        ApplyQuizProgressLock();
    }

    /** Marks the supplied destination as selected
     * @param destination the destination to select
     */
    public void setCurrentDestination(NavDestination destination) {
        currentDestination = destination;
        ApplySelectedStyles();
        ApplyQuizProgressLock();
    }

    /**
     * While the user is mid-attempt on the archetype quiz (just clicked "Redo Quiz", or is
     * taking it for the first time), lock them out of Home/Journal/Quest Viewer so they can't
     * wander off with an unsaved attempt in progress. Archetype stays open as the only way out,
     * and picking it abandons the in-progress attempt (see OnNavigationClick).
     */
    private void ApplyQuizProgressLock() {
        boolean quizInProgress = QuizService.HasActiveAttempt();
        homeButton.setDisable(quizInProgress);
        tasksButton.setDisable(quizInProgress);
        questViewerButton.setDisable(quizInProgress);
    }

    private static final String NAV_FLASH_STYLE = "nav-flash";

    /**
     * Briefly flashes the Journal nav button one time to draw the user's
     * attention to it (e.g. after adding a challenge or submitting a reflection).
     */
    public void FlashTasksButtonOnce() {
        FlashButtonOnce(tasksButton);
    }

    private void FlashButtonOnce(Button button) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO, event -> button.getStyleClass().add(NAV_FLASH_STYLE)),
                new KeyFrame(Duration.seconds(0.25), event -> button.getStyleClass().remove(NAV_FLASH_STYLE)),
                new KeyFrame(Duration.seconds(0.5), event -> button.getStyleClass().add(NAV_FLASH_STYLE)),
                new KeyFrame(Duration.seconds(0.75), event -> button.getStyleClass().remove(NAV_FLASH_STYLE)));
        timeline.play();
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
        //SetLabelVisible(questsNavLabel, expanded);
        SetLabelVisible(questViewerNavLabel, expanded);
        SetLabelVisible(tasksNavLabel, expanded);
        SetLabelVisible(archetypeNavLabel, expanded);
        SetLabelVisible(profileNameLabel, expanded);
        SetLabelVisible(logoutNavLabel, expanded);

        SetButtonDimensions(homeButton, expanded, NAV_BUTTON_HEIGHT);
        //SetButtonDimensions(questsButton, expanded, NAV_BUTTON_HEIGHT);
        SetButtonDimensions(questViewerButton, expanded, NAV_BUTTON_HEIGHT);
        SetButtonDimensions(tasksButton, expanded, NAV_BUTTON_HEIGHT);
        SetButtonDimensions(archetypeButton, expanded, NAV_BUTTON_HEIGHT);
        SetButtonDimensions(profileButton, expanded, PROFILE_BUTTON_HEIGHT);
        SetButtonDimensions(logoutButton, expanded, PROFILE_BUTTON_HEIGHT);
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
        //questsButton.getStyleClass().remove(SELECTED_NAV_BUTTON);
        questViewerButton.getStyleClass().remove(SELECTED_NAV_BUTTON);
        tasksButton.getStyleClass().remove(SELECTED_NAV_BUTTON);
        archetypeButton.getStyleClass().remove(SELECTED_NAV_BUTTON);
        profileButton.getStyleClass().remove(SELECTED_NAV_BUTTON);
        settingsButton.getStyleClass().remove(SELECTED_SETTINGS_BUTTON);

        if (currentDestination == null) {
            return;
        }

        switch (currentDestination) {
            case HOME -> homeButton.getStyleClass().add(SELECTED_NAV_BUTTON);
            //case QUESTS -> questsButton.getStyleClass().add(SELECTED_NAV_BUTTON);
            case QUEST_VIEWER ->  questViewerButton.getStyleClass().add(SELECTED_NAV_BUTTON);
            case TASKS -> tasksButton.getStyleClass().add(SELECTED_NAV_BUTTON);
            case ARCHETYPE -> archetypeButton.getStyleClass().add(SELECTED_NAV_BUTTON);
            case PROFILE -> profileButton.getStyleClass().add(SELECTED_NAV_BUTTON);
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
            case HOME -> NavigationService.LoadScreen(ScreenEnum.DASHBOARD);
            case QUESTS -> NavigationService.LoadScreen(ScreenEnum.QUESTS);
            case QUEST_VIEWER -> NavigationService.LoadScreen(ScreenEnum.QUEST_VIEWER);
            case TASKS -> NavigationService.LoadScreen(ScreenEnum.TASKS);
            case SETTINGS -> NavigationService.LoadScreen(ScreenEnum.SETTINGS);
            case PROFILE -> NavigationService.LoadScreen(ScreenEnum.PROFILE);
            case ARCHETYPE -> {
                if (QuizService.HasActiveAttempt()) {
                    // Abandon the in-progress attempt - nothing has been saved yet, so this
                    // reverts the user straight back to whatever result they had before.
                    QuizService.Reset();
                }
                NavigationService.LoadScreen(ScreenEnum.ARCHETYPE);
            }
        }
    }

    @FXML
    private void OnLogout() throws IOException {
        LoginCacheService loginCacheService = new LoginCacheService();
        loginCacheService.ClearCache();
        ApplicationManager.CurrentAccount.setCurrentEmail("");

        NavigationService.LoadScreen(ScreenEnum.ACCOUNT_SIGN_IN);
    }

    private NavDestination DestinationFor(Button button) {
        if (button == homeButton) {
            return NavDestination.HOME;
        }
//        if (button == questsButton) {
//            return NavDestination.QUESTS;
//        }
        if (button == questViewerButton) {
            return NavDestination.QUEST_VIEWER;
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
        if (button == profileButton) {
            return NavDestination.PROFILE;
        }
        return null;
    }
}

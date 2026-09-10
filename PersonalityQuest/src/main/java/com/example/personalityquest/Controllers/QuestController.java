package com.example.personalityquest.Controllers;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.Applications.DashboardApplication;
import com.example.personalityquest.Applications.SettingsApplication;
import com.example.personalityquest.Model.EmailDetails;
import com.example.personalityquest.Model.Quest;
import com.example.personalityquest.Model.QuestLore;
import com.example.personalityquest.Model.Task;
import com.example.personalityquest.Model.UserQuest;
import com.example.personalityquest.Services.EmailService;
import com.example.personalityquest.Services.QuestService;
import com.example.personalityquest.Services.TaskService;
import com.example.personalityquest.Services.UserQuestService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class QuestController implements Initializable {
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

    @FXML
    private BorderPane questRoot;
    @FXML
    private VBox sidebar;
    @FXML
    private Label brandLabel, profileNameLabel;
    @FXML
    private Label homeNavLabel, questsNavLabel, tasksNavLabel, archetypeNavLabel;
    @FXML
    private Button menuButton, homeButton, questsButton,
            tasksButton, archetypeButton, profileButton, settingsButton;
    @FXML
    private GridPane detailGrid, listsGrid;
    @FXML
    private VBox traitCard, historyCard;
    @FXML
    private Label questTitleLabel, questStatusLabel, questArchetypeLabel,
            questStoryLabel, questProgressLabel, challengeLabel, traitLabel,
            reflectionPromptLabel;
    @FXML
    private ProgressBar questProgress;
    @FXML
    private ListView<Task> labourTasks;
    @FXML
    private ListView<QuestListItem> questHistory;

    private boolean sidebarExpanded = true;
    private boolean sidebarOverride;
    private String profileText = "Profile";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ConfigureTaskList();
        ConfigureHistoryList();
        SetProfileLabel();
        LoadQuestline();

        questRoot.widthProperty().addListener((observable, oldWidth, newWidth) ->
                ApplyResponsiveLayout(newWidth.doubleValue()));

        Platform.runLater(() -> ApplyResponsiveLayout(questRoot.getWidth()));
    }

    private void SetProfileLabel() {
        try {
            EmailDetails emailDetails = EmailService.GetDetailsForEmail(
                    ApplicationManager.CurrentAccount.getCurrentEmail());

            if (emailDetails == null) {
                profileText = "Profile";
            } else {
                profileText = emailDetails.getUserName();
            }
        } catch (Exception exception) {
            profileText = "Profile";
        }

        profileNameLabel.setText(profileText);
    }

    private void LoadQuestline() {
        String email = ApplicationManager.CurrentAccount.getCurrentEmail();
        questHistory.getItems().clear();

        if (ApplicationManager.isEmpty(email)) {
            ShowEmptyQuest("Sign in to see your questline.");
            return;
        }

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

    private void ShowQuest(QuestListItem item) {
        if (item == null) {
            ShowEmptyQuest("Select a quest to read its story.");
            return;
        }

        UserQuest userQuest = item.userQuest();
        Quest quest = item.quest();
        QuestLore lore = QuestLore.forQuest(quest.getName(), item.archetypeName());

        questTitleLabel.setText(lore.labourTitle());
        questArchetypeLabel.setText(item.archetypeName() + " · " + quest.getName());
        questStoryLabel.setText(lore.story());
        challengeLabel.setText(lore.challenge());
        traitLabel.setText(lore.traitFocus());
        reflectionPromptLabel.setText(lore.reflectionPrompt());

        float progress = userQuest.getPercentageComplete();
        questProgress.setProgress(progress);
        questProgressLabel.setText(String.format("%.0f%% Complete", progress * 100));
        SetStatusChip(userQuest.getStatus());
        LoadLabourTasks(quest.getLabourId());
    }

    private void ShowEmptyQuest(String message) {
        questTitleLabel.setText("No active quest");
        questArchetypeLabel.setText(message);
        questStoryLabel.setText("When a quest is assigned, its story, challenge, and tasks will appear here.");
        challengeLabel.setText("Your challenge will appear here once a quest is active.");
        traitLabel.setText("—");
        reflectionPromptLabel.setText("—");
        questProgress.setProgress(0);
        questProgressLabel.setText("0% Complete");
        SetStatusChip("None");
        labourTasks.getItems().clear();
    }

    private void LoadLabourTasks(int labourId) {
        labourTasks.getItems().clear();
        try {
            labourTasks.getItems().addAll(TaskService.GetTasksForLabourId(labourId));
        } catch (Exception exception) {
            labourTasks.getItems().clear();
        }
    }

    private void SetStatusChip(String status) {
        questStatusLabel.getStyleClass().removeAll("status-active", "status-complete", "status-other");
        questStatusLabel.setText(status);

        if ("Active".equalsIgnoreCase(status)) {
            questStatusLabel.getStyleClass().add("status-active");
        } else if ("Complete".equalsIgnoreCase(status)) {
            questStatusLabel.getStyleClass().add("status-complete");
        } else {
            questStatusLabel.getStyleClass().add("status-other");
        }
    }

    private void ConfigureTaskList() {
        labourTasks.setPlaceholder(new Label("No tasks for this labour yet."));
        labourTasks.setCellFactory(list -> new ListCell<>() {
            private final Label nameLabel = new Label();
            private final Label detailLabel = new Label();
            private final VBox row = new VBox(2, nameLabel, detailLabel);

            {
                row.getStyleClass().add("labour-task-row");
                nameLabel.getStyleClass().add("labour-task-name");
                detailLabel.getStyleClass().add("labour-task-detail");
                detailLabel.setWrapText(true);
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
                detailLabel.setText(task.getDescription());
                setText(null);
                setGraphic(row);
            }
        });
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
                    if (newItem != null) {
                        ShowQuest(newItem);
                    }
                });
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

        boolean stacked = width < STACKED_BREAKPOINT;
        SetGridCardLayout(detailGrid, traitCard, stacked);
        SetGridCardLayout(listsGrid, historyCard, stacked);
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

    private void SetGridCardLayout(GridPane grid, VBox secondaryCard, boolean stacked) {
        if (stacked) {
            GridPane.setColumnIndex(secondaryCard, 0);
            GridPane.setRowIndex(secondaryCard, 1);
            grid.getColumnConstraints().get(0).setPercentWidth(FULL_PERCENT);
            grid.getColumnConstraints().get(1).setPercentWidth(HIDDEN_PERCENT);
        } else {
            GridPane.setColumnIndex(secondaryCard, 1);
            GridPane.setRowIndex(secondaryCard, 0);
            grid.getColumnConstraints().get(0).setPercentWidth(55);
            grid.getColumnConstraints().get(1).setPercentWidth(45);
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

        if (button == homeButton) {
            DashboardApplication.launch((Stage) questRoot.getScene().getWindow());
            return;
        }

        if (button == settingsButton) {
            SettingsApplication.launch((Stage) questRoot.getScene().getWindow());
            return;
        }

        if (button == questsButton) {
            return;
        }

        System.out.println("Selected quest navigation: " + button.getId());
    }

    private record QuestListItem(UserQuest userQuest, Quest quest, String archetypeName) {
    }
}

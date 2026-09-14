package com.example.personalityquest.Controllers.quest;

import com.example.personalityquest.Controllers.navigation.NavBarController;
import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.Model.quest.Quest;
import com.example.personalityquest.Model.quest.Task;
import com.example.personalityquest.Model.quest.UserQuest;
import com.example.personalityquest.Services.quest.QuestService;
import com.example.personalityquest.Services.quest.TaskService;
import com.example.personalityquest.Services.quest.UserQuestService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class QuestController implements Initializable {
    private static final double STACKED_BREAKPOINT = 760;
    private static final double FULL_PERCENT = 100;
    private static final double HIDDEN_PERCENT = 0;

    @FXML
    private NavBarController navBarController;
    @FXML
    private BorderPane questRoot;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        navBarController.setCurrentDestination(NavBarController.NavDestination.QUESTS);
        ConfigureTaskList();
        ConfigureHistoryList();
        LoadQuestline();

        questRoot.widthProperty().addListener((observable, oldWidth, newWidth) ->
                ApplyResponsiveLayout(newWidth.doubleValue()));

        Platform.runLater(() -> ApplyResponsiveLayout(questRoot.getWidth()));
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
        List<Task> tasks;
        try {
            tasks = TaskService.GetTasksForLabourId(quest.getLabourId());
        } catch (Exception exception) {
            tasks = List.of();
        }

        String story = "";
        try {
            story = QuestService.GetArchetypeDescription(quest.getArchetypeId());
        } catch (Exception exception) {
            story = "";
        }
        String taskDetails = TaskService.JoinTaskDetails(tasks);

        questTitleLabel.setText(quest.getName());
        questArchetypeLabel.setText(item.archetypeName());
        questStoryLabel.setText(ApplicationManager.isEmpty(story)
                ? "Complete this labour's storyline tasks to continue the questline."
                : story);
        challengeLabel.setText(taskDetails.isEmpty()
                ? "No tasks are stored for this labour yet."
                : taskDetails);
        traitLabel.setText(item.archetypeName());
        reflectionPromptLabel.setText("Weekly practices are on the Tasks page. These storyline tasks belong to the labour itself.");

        float progress = userQuest.getPercentageComplete();
        questProgress.setProgress(progress);
        questProgressLabel.setText(String.format("%.0f%% Complete", progress * 100));
        SetStatusChip(userQuest.getStatus());
        labourTasks.getItems().setAll(tasks);
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

        boolean stacked = width < STACKED_BREAKPOINT;
        SetGridCardLayout(detailGrid, traitCard, stacked);
        SetGridCardLayout(listsGrid, historyCard, stacked);
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

    private record QuestListItem(UserQuest userQuest, Quest quest, String archetypeName) {
    }
}

package com.example.personalityquest.Controllers.quest;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.Controllers.navigation.NavBarController;
import com.example.personalityquest.DAO.quest.QuestOptionDAO;
import com.example.personalityquest.Model.quest.Quest;
import com.example.personalityquest.Model.quest.QuestOption;
import com.example.personalityquest.Model.quest.UserQuest;
import com.example.personalityquest.Services.quest.QuestService;
import com.example.personalityquest.Services.quest.UserQuestService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class QuestViewerController implements Initializable {

    private enum Tab {
        STORYLINE, REACTION, CHALLENGE, REFLECTION
    }

    private static final String SELECTED_TAB_BUTTON = "selected-viewer-tab";

    @FXML
    private NavBarController navBarController;
    @FXML
    private Button storylineTabButton, reactionTabButton, challengeTabButton, reflectionTabButton;
    @FXML
    private VBox storylinePane, reactionPane, challengePane, reflectionPane;
    @FXML
    private Label storylineTitleLabel, storylineDescriptionLabel, storylineProgressLabel;
    @FXML
    private ProgressBar storylineProgress;
    @FXML
    private Label reactionQuestionLabel, reactionFeedbackLabel;
    @FXML
    private VBox reactionOptionsBox;

    private QuestOption.ReactionType selectedReaction;

    private Tab currentTab;

    @Override
    public void initialize(URL location, ResourceBundle resources){
        navBarController.setCurrentDestination(NavBarController.NavDestination.QUEST_VIEWER);
        SelectTab(Tab.STORYLINE);
    }

    @FXML
    private void OnTabClick(ActionEvent event) {
        Button button = (Button) event.getSource();
        Tab tab = TabFor(button);
        if (tab == null || tab == currentTab) {
            return;
        }
        SelectTab(tab);
    }

    private void SelectTab(Tab tab) {
        currentTab = tab;

        if (tab == Tab.STORYLINE) {
            LoadStoryline();
        }

        if (tab == Tab.REACTION) {
            LoadReaction();
        }

        SetPaneVisible(storylinePane, tab == Tab.STORYLINE);
        SetPaneVisible(reactionPane, tab == Tab.REACTION);
        SetPaneVisible(challengePane, tab == Tab.CHALLENGE);
        SetPaneVisible(reflectionPane, tab == Tab.REFLECTION);

        storylineTabButton.getStyleClass().remove(SELECTED_TAB_BUTTON);
        reactionTabButton.getStyleClass().remove(SELECTED_TAB_BUTTON);
        challengeTabButton.getStyleClass().remove(SELECTED_TAB_BUTTON);
        reflectionTabButton.getStyleClass().remove(SELECTED_TAB_BUTTON);

        switch (tab) {
            case STORYLINE -> storylineTabButton.getStyleClass().add(SELECTED_TAB_BUTTON);
            case REACTION -> reactionTabButton.getStyleClass().add(SELECTED_TAB_BUTTON);
            case CHALLENGE -> challengeTabButton.getStyleClass().add(SELECTED_TAB_BUTTON);
            case REFLECTION -> reflectionTabButton.getStyleClass().add(SELECTED_TAB_BUTTON);
        }
    }

    private void SetPaneVisible(VBox pane, boolean visible) {
        pane.setVisible(visible);
        pane.setManaged(visible);
    }

    private Tab TabFor(Button button) {
        if (button == storylineTabButton) {
            return Tab.STORYLINE;
        }
        if (button == reactionTabButton) {
            return Tab.REACTION;
        }
        if (button == challengeTabButton) {
            return Tab.CHALLENGE;
        }
        if (button == reflectionTabButton) {
            return Tab.REFLECTION;
        }
        return null;
    }

    private void LoadStoryline() {
        String email = ApplicationManager.CurrentAccount.getCurrentEmail();
        if (ApplicationManager.isEmpty(email)) {
            ShowEmptyStoryline("Sign in to see your questline");
            return;
        }

        try {
            UserQuest userQuest = UserQuestService.GetCurrentActiveUserQuestForEmail(email);
            if (userQuest == null) {
                ShowEmptyStoryline("Complete the quiz to begin a labour.");
                return;
            }

            Quest quest = QuestService.GetQuestForLabourId(userQuest.getLabourId());
            if (quest == null) {
                ShowEmptyStoryline("Complete the quiz to begin a labour.");
                return;
            }

            storylineTitleLabel.setText(quest.getName());
            storylineDescriptionLabel.setText(quest.getNarrative());
            storylineProgress.setProgress(userQuest.getPercentageComplete());
            storylineProgressLabel.setText(String.format("%.0f%% Complete", userQuest.getPercentageComplete() * 100));
        } catch (Exception exception){
            ShowEmptyStoryline("Could not load your questline right now.");
        }
    }

    private void ShowEmptyStoryline(String message) {
        storylineTitleLabel.setText("No active quest");
        storylineDescriptionLabel.setText(message);
        storylineProgress.setProgress(0);
        storylineProgressLabel.setText("0% complete");
    }

    private void LoadReaction() {
        reactionOptionsBox.getChildren().clear();
        reactionFeedbackLabel.setText("");
        selectedReaction = null;

        String email = ApplicationManager.CurrentAccount.getCurrentEmail();
        if (ApplicationManager.isEmpty(email)) {
            reactionQuestionLabel.setText("Sign in to see your current labour's reaction choice.");
            return;
        }

        try {
            UserQuest userQuest = UserQuestService.GetCurrentActiveUserQuestForEmail(email);
            if (userQuest == null) {
                reactionQuestionLabel.setText("Complete the quiz to begin a labour.");
                return;
            }

            Quest quest = QuestService.GetQuestForLabourId(userQuest.getLabourId());
            if (quest == null) {
                reactionQuestionLabel.setText("Complete the quiz to begin a labour.");
                return;
            }

            List<QuestOption> options = QuestOptionDAO.GetQuestOptionsForLabourId(quest.getLabourId());
            if (options.isEmpty()){
                reactionQuestionLabel.setText("Reaction options are not available for this labour yet");
                return;
            }

            reactionQuestionLabel.setText("In \"" +quest.getName() + "\", how would you react?");
            for (QuestOption option : options) {
                AddReactionOptionButton(option);
            }
        } catch (Exception exception) {
            reactionQuestionLabel.setText("Could not load your questline right now.");
        }
    }

    private void AddReactionOptionButton(QuestOption option) {
        Button button = new Button(option.text());
        button.setMaxWidth(Double.MAX_VALUE);
        button.setWrapText(true);
        button.setMnemonicParsing(false);
        button.getStyleClass().add("quiz-option");
        button.setUserData(option);
        button.setOnAction(event -> SelectReaction(button, option));
        reactionOptionsBox.getChildren().add(button);
    }

    private void SelectReaction(Button chosen, QuestOption option) {
        selectedReaction = option.reactionType();
        reactionOptionsBox.getChildren().forEach(node -> node.getStyleClass().remove("selected"));
        chosen.getStyleClass().add("selected");
        reactionFeedbackLabel.setText("Recorded as: " + selectedReaction);
    }
}

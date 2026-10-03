package com.example.personalityquest.Controllers.quest;

import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.Controllers.navigation.NavBarController;
import com.example.personalityquest.DAO.quest.QuestOptionDAO;
import com.example.personalityquest.DAO.quest.ReflectionPromptDAO;
import com.example.personalityquest.Model.quest.*;
import com.example.personalityquest.Services.chat.ReflectionFeedbackService;
import com.example.personalityquest.Services.quest.QuestService;
import com.example.personalityquest.Services.quest.TaskService;
import com.example.personalityquest.Services.quest.UserQuestService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

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
    @FXML
    private Label challengeDetailsLabel;
    @FXML
    private Label reflectionPromptLabel;
    @FXML
    private Button challengeJournalButton;
    @FXML
    private Label challengeJournalFeedbackLabel;
    @FXML
    private TextArea reflectionEntryArea;
    @FXML
    private Button reflectionFeedbackButton, reflectionDraftButton, reflectionSubmitButton;
    @FXML
    private Label reflectionStatusLabel;
    @FXML
    private VBox reflectionAiFeedbackCard;
    @FXML
    private Label reflectionAiFeedbackLabel;
    @FXML
    private VBox resolutionCard;
    @FXML
    private Label resolutionLabel;

    private UserQuest activeUserQuest;

    private QuestOption.ReactionType selectedReaction;

    private Tab currentTab;
    private boolean reactionAnswered;
    private boolean challengeVisited;

    private static final String FLASHING_TAB = "flashing-tab";

    private Timeline storylineFlashTimeline;

    @Override
    public void initialize(URL location, ResourceBundle resources){
        navBarController.setCurrentDestination(NavBarController.NavDestination.QUEST_VIEWER);
        UpdateTabLocks();
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

    @FXML
    private void OnAddToJournal(){
        challengeVisited = true;
        challengeJournalButton.setDisable(true);
        challengeJournalFeedbackLabel.setText("Added to your journal");
        UpdateTabLocks();

        String email = ApplicationManager.CurrentAccount.getCurrentEmail();
        if (ApplicationManager.isEmpty(email)) {
            return;
        }

        try {
            UserQuest userQuest = UserQuestService.GetCurrentActiveUserQuestForEmail(email);
            if (userQuest == null) {
                return;
            }

            Quest quest = QuestService.GetQuestForLabourId(userQuest.getLabourId());
            if (quest == null) {
                return;
            }

            ShowResolutionIfAvailable(quest);
        } catch (Exception exception) {
            // No resolution available right now; the challenge details are still shown.
        }
    }

    private void SelectTab(Tab tab) {
        currentTab = tab;

        if (tab == Tab.STORYLINE) {
            LoadStoryline();
            StopStorylineFlash();
        }

        if (tab == Tab.REACTION) {
            LoadReaction();
        }

        if (tab == Tab.CHALLENGE) {
            LoadChallenge();
        }

        if (tab == Tab.REFLECTION) {
            LoadReflection();
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
        resolutionCard.setVisible(false);
        resolutionCard.setManaged(false);
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

            String question = quest.getDecisionQuestion();
            reactionQuestionLabel.setText(ApplicationManager.isEmpty(question)
                    ? "In \"" + quest.getName() + "\", how would you react?"
                    : question);
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

        reactionAnswered = true;
        UpdateTabLocks();
    }

    private void LoadChallenge(){
        String email = ApplicationManager.CurrentAccount.getCurrentEmail();
        if (ApplicationManager.isEmpty(email)) {
            challengeDetailsLabel.setText("Sign in to see your current labour's challenge choice.");
            return;
        }

        try {
            UserQuest userQuest = UserQuestService.GetCurrentActiveUserQuestForEmail(email);
            if (userQuest == null) {
                challengeDetailsLabel.setText("Complete the quiz to begin a labour.");
                return;
            }

            Quest quest = QuestService.GetQuestForLabourId(userQuest.getLabourId());
            if (quest == null) {
                challengeDetailsLabel.setText("Complete the quiz to begin a labour.");
                return;
            }

            if (selectedReaction == null){
                challengeDetailsLabel.setText("Answer \"Your Reaction\" first to unlock your challenge.");
                return;
            }

            List<Task> tasks = TaskService.GetTasksForLabourIdAndReactionType(quest.getLabourId(), selectedReaction.name());
            String details =TaskService.JoinTaskDetails(tasks);
            challengeDetailsLabel.setText(details.isEmpty()
                    ? "No tasks are stored for this labour yet."
                    : details);
        } catch (Exception exception){
            challengeDetailsLabel.setText("Could not load your challenge right now.");
        }
    }

    private void LoadReflection() {
        String email = ApplicationManager.CurrentAccount.getCurrentEmail();
        if (ApplicationManager.isEmpty(email)) {
            reflectionPromptLabel.setText("Sign in to see your current labour's reflection choice.");
            DisableReflectionActions();
            return;
        }

        try {
            UserQuest userQuest = UserQuestService.GetCurrentActiveUserQuestForEmail(email);
            if (userQuest == null) {
                reflectionPromptLabel.setText("Complete the quiz to begin a labour.");
                DisableReflectionActions();
                return;
            }

            Quest quest = QuestService.GetQuestForLabourId(userQuest.getLabourId());
            if (quest == null) {
                reflectionPromptLabel.setText("Complete the quiz to begin a labour.");
                DisableReflectionActions();
                return;
            }

            List<ReflectionPrompt> prompts = ReflectionPromptDAO.GetReflectionPromptsForLabourId(quest.getLabourId());
            if (prompts.isEmpty()) {
                reflectionPromptLabel.setText("No prompts are stored for this labour yet.");
                DisableReflectionActions();
                return;
            }

            if (selectedReaction == null){
                reflectionPromptLabel.setText("Answer \"Your Reaction\" first to see your reflection prompt");
                DisableReflectionActions();
                return;
            }

            ReflectionPrompt matched = null;
            for (ReflectionPrompt prompt : prompts) {
                if (selectedReaction.name().equalsIgnoreCase(prompt.reactionType())){
                    matched = prompt;
                    break;
                }
            }

            reflectionPromptLabel.setText(matched != null
                    ? matched.prompt()
                    : "No reflection prompt is stored for your reaction yet");

            activeUserQuest = userQuest;
            boolean finished = "Finished".equalsIgnoreCase(activeUserQuest.getReflectionStatus());
            reflectionEntryArea.setText(userQuest.getReflection() == null ? "" : activeUserQuest.getReflection());
            reflectionEntryArea.setDisable(false);
            reflectionDraftButton.setDisable(finished);
            reflectionSubmitButton.setDisable(finished);
            reflectionSubmitButton.setText(finished ? "Submitted" : "Submit");
            reflectionFeedbackButton.setDisable(false);
            reflectionStatusLabel.setText(finished ? "This reflection has already been submitted." : "");
            ShowStoredQuestFeedback(activeUserQuest.getAccountEmail(), activeUserQuest.getLabourId());

        } catch (Exception exception){
            reflectionPromptLabel.setText("Could not load your reflection right now.");
        }


    }

    private void UpdateTabLocks() {
        challengeTabButton.setDisable(!reactionAnswered);
        reflectionTabButton.setDisable(!challengeVisited);
    }

    private void DisableReflectionActions(){
        activeUserQuest = null;
        reflectionEntryArea.clear();
        reflectionEntryArea.setDisable(true);
        reflectionDraftButton.setDisable(true);
        reflectionSubmitButton.setDisable(true);
        reflectionSubmitButton.setText("Submit");
        reflectionFeedbackButton.setDisable(true);
        reflectionStatusLabel.setText("");
        HideQuestAiFeedback();
    }

    @FXML
    private void OnSaveDraft(){
        if (activeUserQuest == null){
            return;
        }

        String reflection = CurrentReflection();
        if (ApplicationManager.isEmpty(reflection)){
            reflectionStatusLabel.setText("Write something before saving a draft");
            return;
        }

        try{
            activeUserQuest = UserQuestService.UpdateQuestReflectionToDraft(
                    activeUserQuest, reflection, ApplicationManager.CurrentAccount.getCurrentEmail());
            reflectionStatusLabel.setText("Draft Saved");
        } catch (Exception exception) {
            reflectionStatusLabel.setText("Could not save your reflection right now.");
        }
    }

    @FXML
    private void OnSubmit() {
        if (activeUserQuest == null) {
            return;
        }
        String reflection = CurrentReflection();
        if (ApplicationManager.isEmpty(reflection)) {
            reflectionStatusLabel.setText("Write a reflection before submitting.");
            return;
        }
        try {
            activeUserQuest = UserQuestService.UpdateQuestReflectionToBeFinished(
                    activeUserQuest, reflection, ApplicationManager.CurrentAccount.getCurrentEmail());
            reflectionDraftButton.setDisable(true);
            reflectionSubmitButton.setDisable(true);
            reflectionSubmitButton.setText("Submitted");
            RequestAiFeedback(reflection, "Reflection submitted. Generating AI feedback...");
        } catch (Exception exception) {
            reflectionStatusLabel.setText("Could not submit this reflection right now.");
        }
    }
    @FXML
    private void OnGetFeedback() {
        if (activeUserQuest == null) {
            return;
        }
        String reflection = CurrentReflection();
        if (ApplicationManager.isEmpty(reflection)) {
            reflectionStatusLabel.setText("Write a reflection before requesting AI feedback.");
            return;
        }
        RequestAiFeedback(reflection, "Generating AI feedback...");
    }
    private void RequestAiFeedback(String reflection, String statusMessage) {
        UserQuest quest = activeUserQuest;
        reflectionFeedbackButton.setDisable(true);
        reflectionStatusLabel.setText(statusMessage);
        CompletableFuture.supplyAsync(() -> {
            try {
                Quest questDetails = QuestService.GetQuestForLabourId(quest.getLabourId());
                List<Task> tasks = TaskService.GetTasksForLabourId(quest.getLabourId());
                Task firstTask = tasks.isEmpty() ? null : tasks.get(0);
                String questName = questDetails == null ? "" : questDetails.getName();
                return ReflectionFeedbackService.FeedbackFor(
                        ReflectionFeedbackService.ContextFor(firstTask, questName, reflection));
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }).whenComplete((text, error) -> Platform.runLater(() -> {
            reflectionFeedbackButton.setDisable(false);
            if (error != null) {
                reflectionStatusLabel.setText("Could not get AI feedback right now.");
                return;
            }
            try {
                ReflectionFeedbackService.SaveForQuest(quest.getAccountEmail(), quest.getLabourId(), text);
            } catch (Exception ignored) {
                // Showing the reply still helps even if it cannot be stored.
            }
            reflectionAiFeedbackLabel.setText(text);
            reflectionAiFeedbackCard.setVisible(true);
            reflectionAiFeedbackCard.setManaged(true);
            reflectionStatusLabel.setText("AI feedback is ready.");
        }));
    }
    private void ShowStoredQuestFeedback(String email, int labourId) {
        try {
            String stored = ReflectionFeedbackService.FindForQuest(email, labourId);
            if (ApplicationManager.isEmpty(stored)) {
                HideQuestAiFeedback();
                return;
            }
            reflectionAiFeedbackLabel.setText(stored);
            reflectionAiFeedbackCard.setVisible(true);
            reflectionAiFeedbackCard.setManaged(true);
        } catch (Exception exception) {
            HideQuestAiFeedback();
        }
    }
    private void HideQuestAiFeedback() {
        reflectionAiFeedbackLabel.setText("");
        reflectionAiFeedbackCard.setVisible(false);
        reflectionAiFeedbackCard.setManaged(false);
    }
    private String CurrentReflection() {
        String reflection = reflectionEntryArea.getText();
        return reflection == null ? "" : reflection.trim();
    }

    private void ShowResolutionIfAvailable(Quest quest) {
        String resolution = quest.getResolution();
        if (!ApplicationManager.isEmpty(resolution)) {
            resolutionLabel.setText(resolution);
            resolutionCard.setVisible(true);
            resolutionCard.setManaged(true);
            StartStorylineFlash();
        }
    }

    private void StartStorylineFlash() {
        StopStorylineFlash();
        storylineFlashTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0.5), event -> ToggleStyleClass(storylineTabButton, FLASHING_TAB)));
        storylineFlashTimeline.setCycleCount(Timeline.INDEFINITE);
        storylineFlashTimeline.play();
    }

    public void ToggleStyleClass(Button button, String styleClass) {
        if (button.getStyleClass().contains(styleClass)) {
            button.getStyleClass().remove(styleClass);
        } else {
            button.getStyleClass().add(styleClass);
        }
    }

    private void StopStorylineFlash() {
        if (storylineFlashTimeline != null) {
            storylineFlashTimeline.stop();
            storylineFlashTimeline = null;
        }
        storylineTabButton.getStyleClass().remove(FLASHING_TAB);
    }


}

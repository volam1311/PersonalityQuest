package com.example.personalityquest.Controllers.quiz;

import com.example.personalityquest.Controllers.navigation.NavBarController;
import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.DAO.personalisation.ArchetypeDAO;
import com.example.personalityquest.Model.quiz.Archetype;
import com.example.personalityquest.Model.quest.Quest;
import com.example.personalityquest.Model.quiz.QuizResult;
import com.example.personalityquest.Model.quest.Task;
import com.example.personalityquest.Model.quest.UserQuest;
import com.example.personalityquest.ScreenEnum;
import com.example.personalityquest.Services.navigation.NavigationService;
import com.example.personalityquest.Services.quest.QuestService;
import com.example.personalityquest.Services.quiz.QuizService;
import com.example.personalityquest.Services.quest.TaskService;
import com.example.personalityquest.Services.profile.UserProfileService;
import com.example.personalityquest.Services.quest.UserQuestService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ArchetypeController implements Initializable {
    private static final String SELECTED_ARCHETYPE = "selected-archetype";

    @FXML
    private NavBarController navBarController;
    @FXML
    private Label archetypeRankLabel, archetypeNameLabel, overviewLabel, valueLabel,
            strengthsLabel, weaknessesLabel, descriptionLabel, questFocusLabel;
    @FXML
    private Button redoQuizButton, egoQuadrantButton, soulQuadrantButton, selfQuadrantButton, markQuadrantButton;
    @FXML
    private VBox rankedListBox;

    private Map<Button, String> quadrantRealms;
    private QuizResult currentResult;
    private Map<Archetype, Integer> rankByArchetype = new EnumMap<>(Archetype.class);
    private Map<Archetype, Integer> scoreByArchetype = Map.of();
    private List<Archetype> rankedArchetypes = List.of();
    private Map<Archetype, Quest> pinnedQuests = Map.of();
    private Archetype selectedArchetype;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        navBarController.setCurrentDestination(NavBarController.NavDestination.ARCHETYPE);
        quadrantRealms = Map.of(
                egoQuadrantButton, "Ego",
                soulQuadrantButton, "Soul",
                selfQuadrantButton, "Self",
                markQuadrantButton, "Mark");
        LoadArchetypes();
    }

    @FXML
    private void OnRedoQuiz() throws IOException {
        QuizService.StartQuiz();
        NavigationService.LoadScreen(ScreenEnum.QUIZ);
    }

    private void EnsureCatalog() throws SQLException {
        ArchetypeDAO.EnsureTables();
        if (ArchetypeDAO.HasCatalog()) {
            return;
        }
        ArchetypeDAO.SeedCatalog();
    }

    private void LoadArchetypes() {
        try {
            EnsureCatalog();
        } catch (Exception exception) {
            ShowEmptyArchetype("Could not load your archetypes right now.");
            return;
        }

        String email = ApplicationManager.CurrentAccount.getCurrentEmail();
        if (ApplicationManager.isEmpty(email)) {
            ShowEmptyArchetype("Sign in to discover your archetypes.");
            return;
        }

        try {
            QuizResult quizResult = QuizService.GetResult();
            if (quizResult != null) {
                ShowQuizResult(quizResult);
                return;
            }

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

            String archetypeName = QuestService.GetArchetypeName(quest.getArchetypeId());
            Archetype archetype = ArchetypeByName(archetypeName);
            if (archetype == null) {
                ShowEmptyArchetype("Your current quest does not have an archetype yet.");
                return;
            }

            ShowSingleArchetype(archetype, quest);
        } catch (Exception exception) {
            ShowEmptyArchetype("Could not load your archetypes right now.");
        }
    }

    /**
     * Populates the screen from a completed quiz: the full 12-archetype ranking,
     * the realm wheel, the ranked list, and the detail panel for the winner.
     */
    private void ShowQuizResult(QuizResult quizResult) {
        currentResult = quizResult;
        scoreByArchetype = quizResult.scores();
        pinnedQuests = new EnumMap<>(Archetype.class);
        if (quizResult.assignedQuest() != null) {
            pinnedQuests.put(quizResult.archetype(), quizResult.assignedQuest());
        }

        rankedArchetypes = FullRankedArchetypes(quizResult);
        rankByArchetype = new EnumMap<>(Archetype.class);
        for (int index = 0; index < rankedArchetypes.size(); index++) {
            rankByArchetype.put(rankedArchetypes.get(index), index + 1);
        }

        ConfigureRealmWheel();
        ConfigureRankedList();
        DisplayArchetype(quizResult.archetype());
    }

    /**
     * All 12 archetypes ordered by score, winner first. Archetypes the user never
     * scored (their score never appeared in {@code quizResult.scores()}) are appended
     * at the end in declaration order.
     */
    private List<Archetype> FullRankedArchetypes(QuizResult quizResult) {
        List<Archetype> ranked = new ArrayList<>(QuizService.RankedArchetypes(quizResult));
        for (Archetype archetype : Archetype.values()) {
            if (!ranked.contains(archetype)) {
                ranked.add(archetype);
            }
        }
        return ranked;
    }

    /**
     * Fallback for users with an assigned quest but no fresh quiz result: shows just
     * that one archetype, with the wheel/list reduced to what we actually know.
     */
    private void ShowSingleArchetype(Archetype archetype, Quest quest) {
        currentResult = null;
        scoreByArchetype = Map.of();
        pinnedQuests = new EnumMap<>(Archetype.class);
        pinnedQuests.put(archetype, quest);

        rankedArchetypes = List.of(archetype);
        rankByArchetype = new EnumMap<>(Archetype.class);
        rankByArchetype.put(archetype, 1);

        ConfigureRealmWheel();
        ConfigureRankedList();
        DisplayArchetype(archetype);
    }

    private Archetype ArchetypeByName(String archetypeName) {
        if (ApplicationManager.isEmpty(archetypeName)) {
            return null;
        }

        String needle = archetypeName.trim().toLowerCase();
        if (needle.startsWith("the ")) {
            needle = needle.substring(4).trim();
        }

        for (Archetype archetype : Archetype.values()) {
            if (archetype.getName().equalsIgnoreCase(needle)) {
                return archetype;
            }
        }
        return null;
    }

    private String QuestFocusFromDatabase(Archetype archetype, Quest assignedQuest) {
        try {
            Quest quest = assignedQuest;
            if (quest == null) {
                Integer archetypeId = QuestService.GetArchetypeIdForName(archetype.getName());
                if (archetypeId == null) {
                    return "No labour is stored for this archetype yet.";
                }

                Quest[] quests = QuestService.GetQuestsForArchetypeId(archetypeId);
                if (quests == null || quests.length == 0) {
                    return "No labour is stored for this archetype yet.";
                }
                quest = quests[0];
            }

            List<Task> tasks = TaskService.GetTasksForLabourId(quest.getLabourId());
            String names = TaskService.JoinTaskNames(tasks);
            if (names.isEmpty()) {
                return quest.getName();
            }
            return quest.getName() + "\n" + names;
        } catch (Exception exception) {
            return "Could not load this archetype's labour from the database.";
        }
    }

    /**
     * Sets each realm quadrant to that realm's top-scoring archetype, or disables it
     * when we have no data for that realm at all.
     */
    private void ConfigureRealmWheel() {
        quadrantRealms.forEach(this::ConfigureQuadrant);
    }

    private void ConfigureQuadrant(Button button, String realm) {
        Archetype best = TopArchetypeForRealm(realm);
        button.setUserData(best);
        button.setDisable(best == null);
        button.setText(best == null
                ? realm + "\n—"
                : realm + "\n" + UserProfileService.FormatArchetypeName(best.getName()));
    }

    private Archetype TopArchetypeForRealm(String realm) {
        Archetype best = null;
        for (Archetype archetype : Archetype.values()) {
            if (!archetype.getRealm().equals(realm) || !rankByArchetype.containsKey(archetype)) {
                continue;
            }
            if (best == null || rankByArchetype.get(archetype) < rankByArchetype.get(best)) {
                best = archetype;
            }
        }
        return best;
    }

    @FXML
    private void OnRealmQuadrantClick(ActionEvent event) {
        if (event.getSource() instanceof Button button && button.getUserData() instanceof Archetype archetype) {
            DisplayArchetype(archetype);
        }
    }

    /**
     * Rebuilds the scrollable list of all 12 archetypes in ranked order.
     */
    private void ConfigureRankedList() {
        rankedListBox.getChildren().clear();
        for (int index = 0; index < rankedArchetypes.size(); index++) {
            Archetype archetype = rankedArchetypes.get(index);
            int rank = index + 1;

            Label nameLabel = new Label("#" + rank + "  " + UserProfileService.FormatArchetypeName(archetype.getName()));
            nameLabel.getStyleClass().add("ranked-archetype-name");

            Integer points = scoreByArchetype.get(archetype);
            Label pointsLabel = new Label(points != null ? points + " pts" : "");
            pointsLabel.getStyleClass().add("ranked-archetype-points");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox row = new HBox(8, nameLabel, spacer, pointsLabel);
            row.setAlignment(Pos.CENTER_LEFT);

            Button button = new Button();
            button.setGraphic(row);
            button.setMaxWidth(Double.MAX_VALUE);
            button.setMnemonicParsing(false);
            button.getStyleClass().add("ranked-archetype-button");
            button.setUserData(archetype);
            button.setOnAction(event -> DisplayArchetype(archetype));
            row.prefWidthProperty().bind(button.widthProperty().subtract(28));

            rankedListBox.getChildren().add(button);
        }
    }

    private void DisplayArchetype(Archetype archetype) {
        selectedArchetype = archetype;

        Integer rank = rankByArchetype.get(archetype);
        archetypeRankLabel.setText(rank != null ? "#" + rank : "—");
        archetypeNameLabel.setText(UserProfileService.FormatArchetypeName(archetype.getName()));
        overviewLabel.setText(archetype.getSmallDescription());
        valueLabel.setText(archetype.getValue() + " — " + archetype.getValueDefinition());
        strengthsLabel.setText(archetype.getStrengths());
        weaknessesLabel.setText(archetype.getWeaknesses());
        descriptionLabel.setText(archetype.getLongDescription());
        questFocusLabel.setText(QuestFocusFromDatabase(archetype, pinnedQuests.get(archetype)));

        HighlightSelection();
    }

    private void HighlightSelection() {
        for (Button button : quadrantRealms.keySet()) {
            button.getStyleClass().remove(SELECTED_ARCHETYPE);
            if (button.getUserData() == selectedArchetype) {
                button.getStyleClass().add(SELECTED_ARCHETYPE);
            }
        }
        for (Node node : rankedListBox.getChildren()) {
            node.getStyleClass().remove(SELECTED_ARCHETYPE);
            if (node.getUserData() == selectedArchetype) {
                node.getStyleClass().add(SELECTED_ARCHETYPE);
            }
        }
    }

    private void ShowEmptyArchetype(String message) {
        currentResult = null;
        scoreByArchetype = Map.of();
        pinnedQuests = Map.of();
        rankedArchetypes = List.of();
        rankByArchetype = new EnumMap<>(Archetype.class);
        selectedArchetype = null;

        archetypeRankLabel.setText("—");
        archetypeNameLabel.setText("Unassigned");
        overviewLabel.setText(message);
        valueLabel.setText("—");
        strengthsLabel.setText("—");
        weaknessesLabel.setText("—");
        descriptionLabel.setText("—");
        questFocusLabel.setText("Your quest focus will appear here once an archetype is assigned.");

        ConfigureRealmWheel();
        rankedListBox.getChildren().clear();
    }
}

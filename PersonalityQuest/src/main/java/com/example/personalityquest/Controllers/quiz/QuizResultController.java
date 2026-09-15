package com.example.personalityquest.Controllers.quiz;

import com.example.personalityquest.Controllers.navigation.NavBarController;
import com.example.personalityquest.Model.quiz.QuizResult;
import com.example.personalityquest.ScreenEnum;
import com.example.personalityquest.Services.navigation.NavigationService;
import com.example.personalityquest.Services.quest.QuestService;
import com.example.personalityquest.Services.quiz.QuizService;
import com.example.personalityquest.Services.profile.UserProfileService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class QuizResultController implements Initializable {
    @FXML
    private NavBarController navBarController;
    @FXML
    private Label archetypeNameLabel, archetypeDescriptionLabel, strengthsLabel, weaknessesLabel, questLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        QuizResult result = QuizService.GetResult();
        if (result == null) {
            archetypeNameLabel.setText(UserProfileService.UNASSIGNED_ARCHETYPE);
            archetypeDescriptionLabel.setText("Complete the quiz to discover your archetype.");
            strengthsLabel.setText("");
            weaknessesLabel.setText("");
            questLabel.setText("");
            return;
        }

        String name = UserProfileService.FormatArchetypeName(result.archetype().getName());
        archetypeNameLabel.setText(name);
        archetypeDescriptionLabel.setText(DescriptionFor(result));
        strengthsLabel.setText(result.archetype().getStrengths());
        weaknessesLabel.setText(result.archetype().getWeaknesses());

        if (result.assignedQuest() != null) {
            questLabel.setText("Your first labour is ready: " + result.assignedQuest().getName());
        } else {
            questLabel.setText("Your questline will use this archetype when a matching labour is available.");
        }
    }

    private String DescriptionFor(QuizResult result) {
        try {
            Integer archetypeId = QuestService.GetArchetypeIdForName(result.archetype().getName());
            if (archetypeId != null) {
                String stored = QuestService.GetArchetypeDescription(archetypeId);
                if (!stored.isBlank()) {
                    return stored;
                }
            }
        } catch (Exception ignored) {
            // Fall back to the in-memory copy when the database has no row.
        }
        return result.archetype().getSmallDescription();
    }

    @FXML
    private void OnRetake() throws IOException {
        QuizService.StartQuiz();
        NavigationService.LoadScreen(ScreenEnum.QUIZ);
    }

    @FXML
    private void OnBeginQuest() throws IOException {
        NavigationService.LoadScreen(ScreenEnum.ARCHETYPE);
    }
}

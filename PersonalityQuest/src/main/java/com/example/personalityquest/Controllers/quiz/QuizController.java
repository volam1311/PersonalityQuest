package com.example.personalityquest.Controllers.quiz;

import com.example.personalityquest.Controllers.navigation.NavBarController;
import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.Model.quiz.Option;
import com.example.personalityquest.Model.quiz.Question;
import com.example.personalityquest.ScreenEnum;
import com.example.personalityquest.Services.navigation.NavigationService;
import com.example.personalityquest.Services.quiz.QuizService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class QuizController implements Initializable {
    @FXML
    private NavBarController navBarController;
    @FXML
    private Label questionNumberLabel, progressLabel, questionLabel, feedbackLabel;
    @FXML
    private ProgressBar quizProgress;
    @FXML
    private VBox optionsBox;
    @FXML
    private Button backButton, nextButton;

    private Option selectedOption;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!QuizService.HasActiveAttempt()) {
            QuizService.StartQuiz();
        }
        RenderCurrentQuestion();
    }

    private void RenderCurrentQuestion() {
        Question question = QuizService.GetCurrentQuestion();
        int number = QuizService.GetCurrentQuestionIndex() + 1;
        int total = QuizService.GetQuestionCount();

        questionNumberLabel.setText("Question " + number + " of " + total);
        questionLabel.setText(question.getPrompt());
        quizProgress.setProgress((double) number / total);
        progressLabel.setText(Math.round((number * 100.0) / total) + "%");
        feedbackLabel.setText("");
        selectedOption = QuizService.GetAnswerForCurrentQuestion();

        optionsBox.getChildren().clear();
        for (Option option : question.getOptions()) {
            Button button = new Button(option.text());
            button.setMaxWidth(Double.MAX_VALUE);
            button.setWrapText(true);
            button.setMnemonicParsing(false);
            button.getStyleClass().add("quiz-option");
            button.setUserData(option);
            if (option.equals(selectedOption)) {
                button.getStyleClass().add("selected");
            }
            button.setOnAction(event -> SelectOption(button));
            optionsBox.getChildren().add(button);
        }

        backButton.setDisable(!QuizService.CanGoBack());
        nextButton.setText(QuizService.IsLastQuestion() ? "See result" : "Next");
        nextButton.setDisable(selectedOption == null);
    }

    private void SelectOption(Button chosen) {
        selectedOption = (Option) chosen.getUserData();
        QuizService.AnswerCurrentQuestion(selectedOption);
        feedbackLabel.setText("");

        optionsBox.getChildren().forEach(node -> node.getStyleClass().remove("selected"));
        if (!chosen.getStyleClass().contains("selected")) {
            chosen.getStyleClass().add("selected");
        }
        nextButton.setDisable(false);
    }

    @FXML
    private void OnBack() {
        if (!QuizService.CanGoBack()) {
            return;
        }
        QuizService.GoToPreviousQuestion();
        RenderCurrentQuestion();
    }

    @FXML
    private void OnNext() throws IOException {
        if (selectedOption == null) {
            feedbackLabel.setText("Pick an option to continue.");
            return;
        }

        QuizService.AnswerCurrentQuestion(selectedOption);

        if (QuizService.IsLastQuestion()) {
            QuizService.CompleteQuiz();
            QuizService.AssignQuestForCurrentResult(ApplicationManager.CurrentAccount.getCurrentEmail());
            NavigationService.LoadScreen(ScreenEnum.ARCHETYPE);
            return;
        }

        QuizService.GoToNextQuestion();
        RenderCurrentQuestion();
    }
}

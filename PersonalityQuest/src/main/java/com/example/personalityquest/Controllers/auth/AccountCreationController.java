package com.example.personalityquest.Controllers.auth;

import com.example.personalityquest.AccountRepository;
import com.example.personalityquest.ScreenEnum;
import com.example.personalityquest.Services.navigation.NavigationService;
import com.example.personalityquest.Validators.auth.AccountSignUpValidator;
import com.example.personalityquest.Services.auth.EmailService;
import com.example.personalityquest.Services.quiz.QuizService;
import com.example.personalityquest.ApplicationManager;
import com.example.personalityquest.SQLite;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class AccountCreationController {
    private static final double COMPACT_BREAKPOINT = 700;

    @FXML
    private StackPane root;
    @FXML
    private GridPane contentGrid;
    @FXML
    private StackPane brandingPane;
    @FXML
    private ImageView brandingImage;
    @FXML
    private VBox formPane;
    @FXML
    private GridPane nameGrid;

    @FXML
    private TextField emailEntry, userNameEntry, firstNameEntry,
            lastNameEntry, passwordEntry, reEnterPasswordEntry;

    @FXML
    private Label Message;

    @FXML
    private void initialize() {
        Rectangle clip = new Rectangle();
        clip.setArcWidth(56);
        clip.setArcHeight(56);
        clip.widthProperty().bind(brandingPane.widthProperty());
        clip.heightProperty().bind(brandingPane.heightProperty());
        brandingPane.setClip(clip);

        brandingImage.fitWidthProperty().bind(brandingPane.widthProperty());
        brandingImage.fitHeightProperty().bind(brandingPane.heightProperty());

        for (TextField field : allFields()) {
            field.textProperty().addListener((obs, oldText, newText) -> clearFieldError(field));
        }

        root.widthProperty().addListener((obs, oldWidth, newWidth) -> applyResponsiveLayout());
        root.heightProperty().addListener((obs, oldHeight, newHeight) -> applyResponsiveLayout());
        Platform.runLater(this::applyResponsiveLayout);
    }

    private void applyResponsiveLayout() {
        if (root.getWidth() <= 0 || contentGrid.getColumnConstraints().size() < 2) {
            return;
        }

        boolean compact = root.getWidth() < COMPACT_BREAKPOINT;
        toggleStyleClass(root, "compact", compact);

        ColumnConstraints leftColumn = contentGrid.getColumnConstraints().get(0);
        ColumnConstraints rightColumn = contentGrid.getColumnConstraints().get(1);
        RowConstraints firstRow = contentGrid.getRowConstraints().get(0);
        RowConstraints secondRow = contentGrid.getRowConstraints().get(1);

        brandingPane.setMinSize(0, 0);
        formPane.setMinSize(0, 0);
        brandingPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        formPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        if (compact) {
            GridPane.setConstraints(brandingPane, 0, 0);
            GridPane.setConstraints(formPane, 0, 1);
            leftColumn.setPercentWidth(100);
            rightColumn.setPercentWidth(0);
            rightColumn.setMinWidth(0);
            rightColumn.setMaxWidth(0);
            firstRow.setPercentHeight(24);
            firstRow.setVgrow(Priority.SOMETIMES);
            secondRow.setPercentHeight(76);
            secondRow.setVgrow(Priority.ALWAYS);
        } else {
            GridPane.setConstraints(brandingPane, 0, 0);
            GridPane.setConstraints(formPane, 1, 0);
            leftColumn.setPercentWidth(50);
            rightColumn.setPercentWidth(50);
            rightColumn.setMinWidth(0);
            rightColumn.setMaxWidth(Double.MAX_VALUE);
            firstRow.setPercentHeight(100);
            firstRow.setVgrow(Priority.ALWAYS);
            secondRow.setPercentHeight(0);
            secondRow.setVgrow(Priority.NEVER);
        }

        applyNameRowLayout(compact);
    }

    private void applyNameRowLayout(boolean compact) {
        if (nameGrid == null || nameGrid.getColumnConstraints().size() < 2) {
            return;
        }

        ColumnConstraints firstNameColumn = nameGrid.getColumnConstraints().get(0);
        ColumnConstraints lastNameColumn = nameGrid.getColumnConstraints().get(1);

        firstNameEntry.setMinWidth(0);
        lastNameEntry.setMinWidth(0);
        firstNameEntry.setMaxWidth(Double.MAX_VALUE);
        lastNameEntry.setMaxWidth(Double.MAX_VALUE);

        if (compact) {
            GridPane.setConstraints(firstNameEntry, 0, 0);
            GridPane.setConstraints(lastNameEntry, 0, 1);
            firstNameColumn.setPercentWidth(100);
            lastNameColumn.setPercentWidth(0);
            lastNameColumn.setMinWidth(0);
            lastNameColumn.setMaxWidth(0);
        } else {
            GridPane.setConstraints(firstNameEntry, 0, 0);
            GridPane.setConstraints(lastNameEntry, 1, 0);
            firstNameColumn.setPercentWidth(50);
            lastNameColumn.setPercentWidth(50);
            lastNameColumn.setMinWidth(0);
            lastNameColumn.setMaxWidth(Double.MAX_VALUE);
        }
    }

    private void toggleStyleClass(Node node, String styleClass, boolean enabled) {
        if (enabled) {
            if (!node.getStyleClass().contains(styleClass)) {
                node.getStyleClass().add(styleClass);
            }
        } else {
            node.getStyleClass().remove(styleClass);
        }
    }

    @FXML
    private void OnSignUp() throws SQLException, IOException {
        clearAllFieldErrors();
        Message.setText("");

        if (hasBlankFields()) {
            markBlankFields();
            Message.setText("*Required");
            return;
        }

        String validationError = AccountSignUpValidator.validationError(
                emailEntry.getText(),
                userNameEntry.getText(),
                firstNameEntry.getText(),
                lastNameEntry.getText(),
                passwordEntry.getText(),
                reEnterPasswordEntry.getText());

        if (validationError != null) {
            Message.setText(validationError);
            if ("Passwords do not match".equals(validationError)
                    || validationError.contains("Password length")) {
                markFieldError(passwordEntry);
                markFieldError(reEnterPasswordEntry);
            }
            return;
        }

        if (EmailService.DoesAccountWithEmailExist(emailEntry.getText())) {
            markFieldError(emailEntry);
            Message.setText("An account with this email already exists");
            return;
        }

        try {
            AccountRepository.createAccount(
                    SQLite.getConnection(),
                    emailEntry.getText(),
                    userNameEntry.getText(),
                    firstNameEntry.getText(),
                    lastNameEntry.getText(),
                    passwordEntry.getText());
        } catch (SQLException e) {
            markFieldError(emailEntry);
            Message.setText("Could not create account. Email may already be in use");
            return;
        }

        ApplicationManager.CurrentAccount.setCurrentEmail(emailEntry.getText());
        QuizService.StartQuiz();
        NavigationService.LoadScreen(ScreenEnum.QUIZ);
        System.out.println("Account created");
    }

    @FXML
    private void OnSignIn(MouseEvent event) throws IOException {
        Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        NavigationService.LoadScreen(ScreenEnum.ACCOUNT_SIGN_IN);
    }

    private TextField[] allFields() {
        return new TextField[]{
                userNameEntry, emailEntry, firstNameEntry,
                lastNameEntry, passwordEntry, reEnterPasswordEntry
        };
    }

    private boolean hasBlankFields() {
        for (TextField field : allFields()) {
            if (isBlank(field.getText())) {
                return true;
            }
        }
        return false;
    }

    private void markBlankFields() {
        for (TextField field : allFields()) {
            if (isBlank(field.getText())) {
                markFieldError(field);
            }
        }
    }

    private void clearAllFieldErrors() {
        for (TextField field : allFields()) {
            clearFieldError(field);
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private void markFieldError(TextField field) {
        if (!field.getStyleClass().contains("error")) {
            field.getStyleClass().add("error");
        }
    }

    private void clearFieldError(TextField field) {
        field.getStyleClass().remove("error");
    }
}

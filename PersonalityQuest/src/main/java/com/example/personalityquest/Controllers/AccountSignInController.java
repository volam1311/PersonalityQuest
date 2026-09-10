package com.example.personalityquest.Controllers;

import com.example.personalityquest.Applications.*;
import com.example.personalityquest.DAO.EmailDAO;
import com.example.personalityquest.DAO.PasswordDAO;
import com.example.personalityquest.ApplicationManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.*;
import java.sql.*;

public class AccountSignInController {
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
    private TextField emailEntry, passwordEntry;

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

        emailEntry.textProperty().addListener((obs, oldText, newText) -> clearFieldError(emailEntry));
        passwordEntry.textProperty().addListener((obs, oldText, newText) -> clearFieldError(passwordEntry));

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
            firstRow.setPercentHeight(32);
            firstRow.setVgrow(Priority.SOMETIMES);
            secondRow.setPercentHeight(68);
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

    /*
     * When "Sign In" Button is clicked
     * */
    @FXML
    private void OnSignIn() throws SQLException, IOException {
        if (!isValidSignIn()) {
            System.out.println("Account Sign In Invalid");
            return;
        }

        /*
         * Set currently logged in account
         * */
        ApplicationManager.CurrentAccount.setCurrentEmail(emailEntry.getText());

        DashboardApplication.launch((Stage)emailEntry.getScene().getWindow());


        System.out.println("Sign in complete");
    }

    /*
     * Checks to see if the given email and password will work as a valid
     * sign in
     * */
    private boolean isValidSignIn() throws SQLException {
        clearFieldError(emailEntry);
        clearFieldError(passwordEntry);

        boolean emailMissing = isBlank(emailEntry.getText());
        boolean passwordMissing = isBlank(passwordEntry.getText());
        if (emailMissing || passwordMissing) {
            if (emailMissing) {
                markFieldError(emailEntry);
            }
            if (passwordMissing) {
                markFieldError(passwordEntry);
            }
            Message.setText("*Required");
            return false;
        }

        // Does the email exist and does the password match the email
        if (!EmailDAO.DoesAccountWithEmailExist(emailEntry.getText())
        || !PasswordDAO.isPasswordForEmail(emailEntry.getText(), passwordEntry.getText())){
            markFieldError(emailEntry);
            markFieldError(passwordEntry);
            Message.setText("Email or Password Was entered incorrectly");
            return false;
        }

        Message.setText("");
        return true;
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

    /*
    * When "Click here to Sign Up" label is clicked
    * */
    @FXML
    private void OnSignUp(MouseEvent event) throws IOException {
        Stage currentStage = (Stage)((Node) event.getSource()).getScene().getWindow();
        AccountCreationApplication.launch(currentStage);
    }
}

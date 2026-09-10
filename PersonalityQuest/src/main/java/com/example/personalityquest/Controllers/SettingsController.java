package com.example.personalityquest.Controllers;

import com.example.personalityquest.Applications.DashboardApplication;
import com.example.personalityquest.Model.EmailDetails;
import com.example.personalityquest.Services.EmailService;
import com.example.personalityquest.ApplicationManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {
    private static final double SIDEBAR_BREAKPOINT = 900;
    private static final double EXPANDED_SIDEBAR_WIDTH = 250;
    private static final double COLLAPSED_SIDEBAR_WIDTH = 72;
    private static final double NAV_BUTTON_HEIGHT = 52;
    private static final double PROFILE_BUTTON_HEIGHT = 44;
    private static final double COMPACT_BUTTON_SIZE = 44;
    private static final double EXPANDED_BUTTON_PREF_WIDTH = 9999;

    @FXML
    private BorderPane settingsRoot;
    @FXML
    private VBox sidebar;
    @FXML
    private Label brandLabel, profileNameLabel, feedbackLabel;
    @FXML
    private Label homeNavLabel, questsNavLabel, tasksNavLabel, archetypeNavLabel;
    @FXML
    private Button menuButton, homeButton, questsButton,
            tasksButton, archetypeButton, profileButton, settingsButton;
    @FXML
    private ToggleGroup backgroundColorGroup, languageGroup;
    @FXML
    private ToggleButton colorDarkButton, colorDuskButton, colorForestButton,
            colorOceanButton, colorLightButton;
    @FXML
    private ToggleButton languageEnglishButton, languageVietnameseButton,
            languageSpanishButton;

    private boolean sidebarExpanded = true;
    private boolean sidebarOverride;
    private String profileText = "Profile";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        KeepToggleSelected(backgroundColorGroup);
        KeepToggleSelected(languageGroup);
        SetProfileLabel();

        settingsRoot.widthProperty().addListener((observable, oldWidth, newWidth) ->
                ApplyResponsiveLayout(newWidth.doubleValue()));

        Platform.runLater(() -> ApplyResponsiveLayout(settingsRoot.getWidth()));
    }

    private void KeepToggleSelected(ToggleGroup group) {
        group.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            if (newToggle == null && oldToggle != null) {
                oldToggle.setSelected(true);
            }
        });
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
            DashboardApplication.launch((Stage) settingsRoot.getScene().getWindow());
            return;
        }

        if (button == settingsButton) {
            return;
        }

        System.out.println("Selected settings navigation: " + button.getId());
    }

    @FXML
    private void OnSave() {
        feedbackLabel.setText("Saved " + GetSelectedColorName() + " / " + GetSelectedLanguageName());
        System.out.println("Saved settings: colour=" + GetSelectedColorName()
                + ", language=" + GetSelectedLanguageName());
    }

    @FXML
    private void OnReset() {
        colorDarkButton.setSelected(true);
        languageEnglishButton.setSelected(true);
        feedbackLabel.setText("Reset to default");
        System.out.println("Reset settings to default");
    }

    private String GetSelectedColorName() {
        Toggle selected = backgroundColorGroup.getSelectedToggle();

        if (selected == colorDuskButton) {
            return "Dusk";
        }
        if (selected == colorForestButton) {
            return "Forest";
        }
        if (selected == colorOceanButton) {
            return "Ocean";
        }
        if (selected == colorLightButton) {
            return "Light";
        }
        return "Dark";
    }

    private String GetSelectedLanguageName() {
        Toggle selected = languageGroup.getSelectedToggle();

        if (selected == languageVietnameseButton) {
            return "Tiếng Việt";
        }
        if (selected == languageSpanishButton) {
            return "Español";
        }
        return "English";
    }
}

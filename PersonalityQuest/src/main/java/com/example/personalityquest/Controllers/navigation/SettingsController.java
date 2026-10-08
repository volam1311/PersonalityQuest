package com.example.personalityquest.Controllers.navigation;

import com.example.personalityquest.ScreenEnum;
import com.example.personalityquest.Services.navigation.NavigationService;
import com.example.personalityquest.Services.navigation.ThemeService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/** Controls settings interactions */
public class SettingsController implements Initializable {
    @FXML
    private NavBarController navBarController;
    @FXML
    private Label feedbackLabel;
    @FXML
    private ToggleGroup backgroundColorGroup, languageGroup;
    @FXML
    private ToggleButton colorDarkButton, colorDuskButton, colorForestButton,
            colorOceanButton, colorLightButton;
    @FXML
    private ToggleButton languageEnglishButton, languageVietnameseButton,
            languageSpanishButton;
    private ThemeService themeService;

    /** Initialises the settings controls
     * @param location the location used to resolve relative paths
     * @param resources the localisation resources for the screen
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        themeService = NavigationService.GetThemeService();
        navBarController.setCurrentDestination(NavBarController.NavDestination.SETTINGS);
        KeepToggleSelected(backgroundColorGroup);
        KeepToggleSelected(languageGroup);
        SelectCurrentTheme();
    }

    private void KeepToggleSelected(ToggleGroup group) {
        group.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            if (newToggle == null && oldToggle != null) {
                oldToggle.setSelected(true);
            }
        });
    }

    @FXML
    private void OnSave() {
        try {
            themeService.setCurrentTheme(GetSelectedTheme());
        } catch (IllegalStateException exception) {
            feedbackLabel.setText("Could not save the selected theme.");
            return;
        }

        try {
            NavigationService.LoadScreen(ScreenEnum.SETTINGS);
        } catch (IOException exception) {
            feedbackLabel.setText("Theme saved, but the screen could not refresh.");
        }
    }

    @FXML
    private void OnReset() {
        colorDarkButton.setSelected(true);
        languageEnglishButton.setSelected(true);
        try {
            themeService.setCurrentTheme(ThemeService.Theme.DARK);
        } catch (IllegalStateException exception) {
            feedbackLabel.setText("Could not reset the theme.");
            return;
        }
        try {
            NavigationService.LoadScreen(ScreenEnum.SETTINGS);
        } catch (IOException exception) {
            feedbackLabel.setText("Default theme saved, but the screen could not refresh.");
        }
    }

    private void SelectCurrentTheme() {
        Toggle selectedTheme = switch (themeService.getCurrentTheme()) {
            case DARK -> colorDarkButton;
            case DUSK -> colorDuskButton;
            case FOREST -> colorForestButton;
            case OCEAN -> colorOceanButton;
            case LIGHT -> colorLightButton;
        };
        backgroundColorGroup.selectToggle(selectedTheme);
    }

    private ThemeService.Theme GetSelectedTheme() {
        Toggle selected = backgroundColorGroup.getSelectedToggle();
        if (selected == colorDuskButton) {
            return ThemeService.Theme.DUSK;
        }
        if (selected == colorForestButton) {
            return ThemeService.Theme.FOREST;
        }
        if (selected == colorOceanButton) {
            return ThemeService.Theme.OCEAN;
        }
        if (selected == colorLightButton) {
            return ThemeService.Theme.LIGHT;
        }
        return ThemeService.Theme.DARK;
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

package com.example.personalityquest.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import java.net.URL;
import java.util.ResourceBundle;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        navBarController.setCurrentDestination(NavBarController.NavDestination.SETTINGS);
        KeepToggleSelected(backgroundColorGroup);
        KeepToggleSelected(languageGroup);
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

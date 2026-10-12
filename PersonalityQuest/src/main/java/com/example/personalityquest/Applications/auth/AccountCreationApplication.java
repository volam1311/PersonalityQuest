package com.example.personalityquest.Applications.auth;

import com.example.personalityquest.AppFonts;
import com.example.personalityquest.ScreenEnum;
import com.example.personalityquest.Services.navigation.NavigationService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


/** Launches the account-creation screen */
public class AccountCreationApplication extends Application {
    /** Loads the account-creation screen into the primary stage
     * @param stage the primary JavaFX stage
     * @throws IOException if the screen resource cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        AppFonts.load();
        stage.setTitle("Account Creation");
        stage.setMinWidth(420);
        stage.setMinHeight(520);
        stage.setWidth(1100);
        stage.setHeight(720);

    }
}

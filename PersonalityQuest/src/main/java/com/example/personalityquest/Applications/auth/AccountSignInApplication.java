package com.example.personalityquest.Applications.auth;

import com.example.personalityquest.AppFonts;
import com.example.personalityquest.ScreenEnum;
import com.example.personalityquest.Services.navigation.NavigationService;
import javafx.application.Application;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;

import java.io.*;

/** Launches the account sign-in screen */
public class AccountSignInApplication extends Application {
    /** Loads the account sign-in screen into the primary stage
     * @param stage the primary JavaFX stage
     * @throws IOException if the screen resource cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
            AppFonts.load();
            stage.setTitle("Account Login");
            stage.setMinWidth(420);
            stage.setMinHeight(480);
            stage.setWidth(1100);
            stage.setHeight(720);
            stage.show();
    }
}

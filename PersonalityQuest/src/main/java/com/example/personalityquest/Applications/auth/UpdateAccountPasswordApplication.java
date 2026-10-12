package com.example.personalityquest.Applications.auth;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


/** Launches the password-update screen */
public class UpdateAccountPasswordApplication extends Application {
    /** Loads the password-update screen into the primary stage
     * @param stage the primary JavaFX stage
     * @throws IOException if the screen resource cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        stage.setTitle("Update Password");
        stage.show();
    }
}

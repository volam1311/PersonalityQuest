package com.example.personalityquest.Applications.auth;

import com.example.personalityquest.AppFonts;
import com.example.personalityquest.ScreenEnum;
import com.example.personalityquest.Services.navigation.NavigationService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class AccountCreationApplication extends Application {
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

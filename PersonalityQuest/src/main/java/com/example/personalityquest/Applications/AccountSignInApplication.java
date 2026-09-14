package com.example.personalityquest.Applications;

import com.example.personalityquest.AppFonts;
import com.example.personalityquest.ScreenEnum;
import com.example.personalityquest.Services.NavigationService;
import javafx.application.Application;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.stage.*;

import java.io.*;

public class AccountSignInApplication extends Application {
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

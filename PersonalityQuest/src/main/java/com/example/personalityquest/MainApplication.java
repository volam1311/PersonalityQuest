package com.example.personalityquest;

import com.example.personalityquest.Applications.AccountCreationApplication;
import com.example.personalityquest.Applications.UpdateAccountPersonalDetailsApplication;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    public final static String fxmlPrefix = "/com/example/personalityquest/";
    @Override
    public void start(Stage stage) throws IOException{
        AppFonts.load();
        AccountCreationApplication.launch(stage);
    }
}

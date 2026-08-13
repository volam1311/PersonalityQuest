package com.example.personalityquest;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException{
        AccountCreationApplication.launch(stage);
    }
}

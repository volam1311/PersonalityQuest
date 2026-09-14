package com.example.personalityquest;

import com.example.personalityquest.Applications.auth.AccountCreationApplication;
import com.example.personalityquest.Applications.auth.UpdateAccountPersonalDetailsApplication;
import com.example.personalityquest.Services.navigation.NavigationService;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException{
        AppFonts.load();
        try {
            NavigationService.Init(stage);
            NavigationService.LoadScreen(ScreenEnum.ACCOUNT_CREATION);
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}

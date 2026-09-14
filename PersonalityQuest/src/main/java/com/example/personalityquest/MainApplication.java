package com.example.personalityquest;

import com.example.personalityquest.Applications.AccountCreationApplication;
import com.example.personalityquest.Applications.UpdateAccountPersonalDetailsApplication;
import com.example.personalityquest.Services.NavigationService;
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

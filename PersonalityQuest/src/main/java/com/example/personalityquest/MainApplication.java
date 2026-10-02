package com.example.personalityquest;

import com.example.personalityquest.Applications.auth.AccountCreationApplication;
import com.example.personalityquest.Applications.auth.UpdateAccountPersonalDetailsApplication;
import com.example.personalityquest.DAO.auth.UserDAO;
import com.example.personalityquest.DAO.personalisation.ArchetypeDAO;
import com.example.personalityquest.DAO.quest.QuestDAO;
import com.example.personalityquest.DAO.quest.UserQuestDAO;
import com.example.personalityquest.Services.navigation.NavigationService;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException{
        AppFonts.load();

        // Runs once, before any other DB call has a chance to leave an open
        // statement on the shared connection. Doing this here (rather than on
        // every visit to the Archetype screen) avoids SQLITE_LOCKED errors caused
        // by other DAOs not closing their statements/result sets.
        try {
            ArchetypeDAO.ResetAndSeedCatalog();
            QuestDAO.ResetAndSeedCatalog();
            initialiseUserQuestData();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        try {
            NavigationService.Init(stage);
            NavigationService.LoadScreen(ScreenEnum.ACCOUNT_CREATION);
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    static void initialiseUserQuestData() throws Exception{
        UserQuestDAO.EnsureTables();
    }
}

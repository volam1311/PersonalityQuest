package com.example.personalityquest;

import com.example.personalityquest.Applications.auth.AccountCreationApplication;
import com.example.personalityquest.Applications.auth.UpdateAccountPersonalDetailsApplication;
import com.example.personalityquest.DAO.auth.UserDAO;
import com.example.personalityquest.DAO.personalisation.ArchetypeDAO;
import com.example.personalityquest.DAO.quest.QuestDAO;
import com.example.personalityquest.DAO.quest.UserQuestDAO;
import com.example.personalityquest.Model.auth.LoginCache;
import com.example.personalityquest.Services.auth.*;
import com.example.personalityquest.Services.navigation.NavigationService;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.prefs.Preferences;

/** Starts the JavaFX application and opens its initial screen */
public class MainApplication extends Application {
    /** Initialises application resources and displays the account screen
     * @param stage the primary JavaFX stage
     * @throws IOException if the screen resource cannot be loaded
     */
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

            LocalDate now = LocalDate.now();

            LoginCache loginCache = LoginCacheService.GetLoginCache();

            // do we have login details
            if (loginCache.GetEmail().isEmpty()){
                NavigationService.LoadScreen(ScreenEnum.ACCOUNT_CREATION);
                return;
            }

            try{
                AttemptLogin(loginCache);
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
                NavigationService.LoadScreen(ScreenEnum.ACCOUNT_CREATION);
            }
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
    }

    static void initialiseUserQuestData() throws Exception{
        UserQuestDAO.EnsureTables();
    }

    static void AttemptLogin(LoginCache loginCache) throws Exception {

        if (LoginCacheService.IsPastTimeLimit(loginCache.GetLastLoginDate())){
            throw new Exception("Login Cache Expired");
        }

        if (!EmailService.DoesAccountWithEmailExist(loginCache.GetEmail())){
            throw new Exception("Account does not exist with this email");
        }

        ApplicationManager.CurrentAccount.setCurrentEmail(loginCache.GetEmail());
        NavigationService.LoadScreen(ScreenEnum.DASHBOARD);
    }
}
